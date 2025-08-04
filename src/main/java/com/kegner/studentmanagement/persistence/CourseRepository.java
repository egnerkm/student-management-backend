package com.kegner.studentmanagement.persistence;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.kegner.studentmanagement.model.CourseEntity;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;

@Repository
public class CourseRepository implements BaseRepository<CourseEntity> {
    // Used for non-joins/subqueries
    private static final String GET_QUERY = """
                SELECT id, course_name, department_name, semester, course_year, credits, professor_name,
                created_date, modified_date
            """;

    // Join query to include course id
    private static final String GET_QUERY_JOIN = GET_QUERY + ", student_id";

    // Valid columns for SQL injection safety
    private static final List<String> validColumns = List.of(
            "id", "course_name", "department_name", "semester",
            "course_year", "credits", "professor_name", "created_date", "modified_date");

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private CourseResultSetExtractor resultSetExtractor;

    @Override
    public CourseEntity get(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        // Includes the student ids
        Map<Long, CourseEntity> courses = jdbcTemplate.query(
                GET_QUERY_JOIN + " FROM course c LEFT JOIN student_course sc ON c.id = sc.course_id WHERE c.id = :id",
                params, resultSetExtractor);

        if (courses == null) {
            return null;
        }

        return courses.get(id);
    }

    @Override
    public ListWrapper<CourseEntity> getAll(DataQuery query) {
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
            whereClause += " AND course_name ILIKE :search ";
        }

        String orderBy = BaseRepository.getOrderByClause(query, validColumns);

        // Get the paginated values
        Map<Long, CourseEntity> courseMap = jdbcTemplate.query(
                GET_QUERY_JOIN + " FROM( " + GET_QUERY + " FROM course "
                        + whereClause + orderBy + " LIMIT :limit OFFSET :offset) as c "
                        + " LEFT JOIN student_course sc ON c.id = sc.course_id " + orderBy,
                params, resultSetExtractor);

        if (courseMap == null) {
            courseMap = new HashMap<>();
        }

        // Get the total count to calculate how many pages there are for the frontend
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM course " + whereClause,
                params, Integer.class);

        List<CourseEntity> courseList = new ArrayList<>(courseMap.values());

        return ListWrapper.<CourseEntity>builder()
                .data(courseList)
                .count(courseList.size())
                .total(total)
                .build();
    }

    @Override
    @Transactional
    public void insert(CourseEntity student) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("courseName", student.getCourseName())
                .addValue("departmentName", student.getDepartmentName())
                .addValue("semester", student.getSemester())
                .addValue("courseYear", student.getCourseYear())
                .addValue("credits", student.getCredits())
                .addValue("professorName", student.getProfessorName());

        jdbcTemplate.update("""
                    INSERT INTO course (course_name, department_name, semester, course_year, credits, professor_name)
                    VALUES (:courseName, :departmentName, :semester, :courseYear, :credits, :professorName)
                """,
                params);
    }

    @Override
    @Transactional
    public void update(CourseEntity student) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", student.getId())
                .addValue("courseName", student.getCourseName())
                .addValue("departmentName", student.getDepartmentName())
                .addValue("semester", student.getSemester())
                .addValue("courseYear", student.getCourseYear())
                .addValue("credits", student.getCredits())
                .addValue("professorName", student.getProfessorName())
                .addValue("modifiedDate", OffsetDateTime.now());

        jdbcTemplate.update("""
                    UPDATE course
                    SET course_name = :courseName, department_name = :departmentName, semester = :semester,
                    course_year = :courseYear, credits = :credits, professor_name = :professorName,
                    modified_date = :modifiedDate
                    WHERE id = :id
                """, params);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id);

        // Delete from the junction table first
        jdbcTemplate.update("DELETE FROM student_course WHERE course_id = :id", params);
        jdbcTemplate.update("DELETE FROM course WHERE id = :id", params);
    }
}
