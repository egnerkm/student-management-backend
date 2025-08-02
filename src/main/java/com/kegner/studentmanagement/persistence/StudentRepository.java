package com.kegner.studentmanagement.persistence;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.model.StudentEntity;

@Repository
public class StudentRepository implements BaseRepository<StudentEntity> {
    // Used for non-joins/subqueries
    private static final String GET_QUERY = """
                SELECT id, first_name, last_name, email, phone_number, date_of_birth, gpa, major,
                created_date, modified_date
            """;

    // Join query to include course id
    private static final String GET_QUERY_JOIN = GET_QUERY + ", course_id";

    // Valid columns for SQL injection safety
    private static final List<String> validColumns = List.of(
            "id", "first_name", "last_name", "email",
            "phone_number", "date_of_birth", "gpa", "major", "created_date", "modified_date");

    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private StudentResultSetExtractor resultSetExtractor;

    @Override
    public StudentEntity get(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        // Includes the course ids
        Map<Long, StudentEntity> studentMap = jdbcTemplate.query(
                GET_QUERY_JOIN + " FROM student s LEFT JOIN student_course sc ON s.id = sc.student_id WHERE s.id = :id",
                params, resultSetExtractor);

        if (studentMap == null) {
            return null;
        }

        return studentMap.get(id);
    }

    @Override
    public ListWrapper<StudentEntity> getAll(DataQuery query) {
        // Pagination values
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", Math.min(query.getPageSize(), MAX_LIMIT))
                .addValue("offset", query.getPage() * query.getPageSize());

        String whereClause = " WHERE 1=1 ";

        // Where clause for ids
        if (!CollectionUtils.isEmpty(query.getIds())) {
            params.addValue("ids", query.getIds());
            whereClause += " AND id IN (:ids) ";
        }

        // Where clause for search field
        if (StringUtils.isNotBlank(query.getSearch())) {
            params.addValue("search", query.getSearch() + "%");
            whereClause += " AND first_name ILIKE :search OR last_name ILIKE :search ";
        }

        String orderBy = BaseRepository.getOrderByClause(query, validColumns);

        // Get the paginated values
        Map<Long, StudentEntity> studentMap = jdbcTemplate.query(
                GET_QUERY_JOIN + " FROM( " + GET_QUERY + " FROM student "
                        + whereClause + " LIMIT :limit OFFSET :offset) as s "
                        + " LEFT JOIN student_course sc ON s.id = sc.student_id " + orderBy,
                params, resultSetExtractor);

        if (studentMap == null) {
            studentMap = new HashMap<>();
        }

        // Get the total count to calculate how many pages there are for the frontend
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM student " + whereClause,
                params, Integer.class);

        List<StudentEntity> studentList = new ArrayList<>(studentMap.values());

        return ListWrapper.<StudentEntity>builder()
                .data(studentList)
                .count(studentList.size())
                .total(total)
                .build();
    }

    @Override
    @Transactional
    public void insert(StudentEntity student) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("firstName", student.getFirstName())
                .addValue("lastName", student.getLastName())
                .addValue("email", student.getEmail())
                .addValue("phoneNumber", student.getPhoneNumber())
                .addValue("dateOfBirth", student.getDateOfBirth())
                .addValue("gpa", student.getGpa())
                .addValue("major", student.getMajor());

        KeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update("""
                    INSERT INTO student (first_name, last_name, email, phone_number, date_of_birth, gpa, major)
                    VALUES (:firstName, :lastName, :email, :phoneNumber, :dateOfBirth, :gpa, :major)
                """, params, holder, new String[] { "id" });

        // Need to grab the id for the junction table insertions
        Number key = holder.getKey();

        if (key == null) {
            logger.error("Insert query did not return a primary key.");
            return;
        }

        // No course ids, skip the batch inserts
        if (CollectionUtils.isEmpty(student.getCourseIds())) {
            return;
        }

        String sql = "INSERT INTO student_course (student_id, course_id) VALUES (:studentId, :courseId)";

        List<SqlParameterSource> batchArgs = new ArrayList<>();
        for (Long courseId : student.getCourseIds()) {
            batchArgs.add(getParamSource(key.longValue(), courseId));
        }

        // Batch update to add the course ids in the junction table
        jdbcTemplate.batchUpdate(sql, batchArgs.toArray(new SqlParameterSource[0]));
    }

    @Override
    @Transactional
    public void update(StudentEntity student) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", student.getId())
                .addValue("firstName", student.getFirstName())
                .addValue("lastName", student.getLastName())
                .addValue("email", student.getEmail())
                .addValue("phoneNumber", student.getPhoneNumber())
                .addValue("dateOfBirth", student.getDateOfBirth())
                .addValue("gpa", student.getGpa())
                .addValue("major", student.getMajor())
                .addValue("modifiedDate", OffsetDateTime.now());

        // Update the main object
        jdbcTemplate.update("""
                    UPDATE student
                    SET first_name = :firstName, last_name = :lastName, email = :email, phone_number = :phoneNumber,
                    date_of_birth = :dateOfBirth, gpa = :gpa, major = :major, modified_date = :modifiedDate
                    WHERE id = :id
                """,
                params);

        Set<Long> courseIds = new HashSet<>(student.getCourseIds());

        String courseIdSql = "SELECT course_id FROM student_course WHERE student_id = :id";
        Set<Long> existingCourseIds = new HashSet<>(jdbcTemplate.queryForList(courseIdSql, params, Long.class));

        Set<Long> courseIdsToAdd = courseIds.stream()
                .filter(courseId -> !existingCourseIds.contains(courseId)).collect(Collectors.toSet());
        Set<Long> courseIdsToRemove = existingCourseIds.stream()
                .filter(courseId -> !courseIds.contains(courseId)).collect(Collectors.toSet());

        String insertSql = "INSERT INTO student_course (student_id, course_id) VALUES (:studentId, :courseId)";
        String removeSql = "DELETE FROM student_course WHERE student_id = :studentId AND course_id = :courseId";

        List<SqlParameterSource> addBatchArgs = new ArrayList<>();
        for (Long courseId : courseIdsToAdd) {
            addBatchArgs.add(getParamSource(student.getId(), courseId));
        }

        List<SqlParameterSource> removeBatchArgs = new ArrayList<>();
        for (Long courseId : courseIdsToRemove) {
            removeBatchArgs.add(getParamSource(student.getId(), courseId));
        }

        // Update the courses that were added and the courses that were removed
        jdbcTemplate.batchUpdate(insertSql, addBatchArgs.toArray(new SqlParameterSource[0]));
        jdbcTemplate.batchUpdate(removeSql, removeBatchArgs.toArray(new SqlParameterSource[0]));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        // Delete from the junction table first
        jdbcTemplate.update("DELETE FROM student_course WHERE student_id = :id", params);
        jdbcTemplate.update("DELETE FROM student WHERE id = :id", params);
    }

    private MapSqlParameterSource getParamSource(Long studentId, Long courseId) {
        return new MapSqlParameterSource()
                .addValue("studentId", studentId)
                .addValue("courseId", courseId);
    }
}
