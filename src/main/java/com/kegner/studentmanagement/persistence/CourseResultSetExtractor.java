package com.kegner.studentmanagement.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.kegner.studentmanagement.model.CourseEntity;

@Component
public class CourseResultSetExtractor implements ResultSetExtractor<Map<Long, CourseEntity>> {
    @Override
    public @NonNull Map<Long, CourseEntity> extractData(@NonNull ResultSet rs) throws SQLException {
        Map<Long, CourseEntity> map = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");

            if (!map.containsKey(id)) {
                CourseEntity newEntity = CourseEntity.builder()
                        .id(id)
                        .courseName(rs.getString("course_name"))
                        .departmentName(rs.getString("department_name"))
                        .semester(rs.getString("semester"))
                        .courseYear(rs.getInt("course_year"))
                        .credits(rs.getInt("credits"))
                        .professorName(rs.getString("professor_name"))
                        .createdDate(rs.getObject("created_date", OffsetDateTime.class))
                        .modifiedDate(rs.getObject("modified_date", OffsetDateTime.class))
                        .build();

                map.put(id, newEntity);
            }

            Long studentId = rs.getObject("student_id", Long.class);

            if (studentId != null) {
                CourseEntity entity = map.get(id);
                List<Long> studentIds = entity.getStudentIds();
                studentIds.add(studentId);
                CourseEntity entityWithStudentId = entity.toBuilder().studentIds(studentIds).build();
                map.put(id, entityWithStudentId);
            }
        }

        return map;
    }
}
