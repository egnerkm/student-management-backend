package com.kegner.studentmanagement.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.kegner.studentmanagement.model.StudentEntity;

@Component
public class StudentResultSetExtractor implements ResultSetExtractor<Map<Long, StudentEntity>> {
    @Override
    public @NonNull Map<Long, StudentEntity> extractData(@NonNull ResultSet rs) throws SQLException {
        Map<Long, StudentEntity> map = new LinkedHashMap<>();

        while (rs.next()) {
            Long id = rs.getLong("id");

            if (!map.containsKey(id)) {
                StudentEntity newEntity = StudentEntity.builder()
                        .id(id)
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .email(rs.getString("email"))
                        .phoneNumber(rs.getString("phone_number"))
                        .dateOfBirth(rs.getObject("date_of_birth", LocalDate.class))
                        .gpa(rs.getDouble("gpa"))
                        .major(rs.getString("major"))
                        .createdDate(rs.getObject("created_date", OffsetDateTime.class))
                        .modifiedDate(rs.getObject("modified_date", OffsetDateTime.class))
                        .build();

                map.put(id, newEntity);
            }

            Long courseId = rs.getObject("course_id", Long.class);

            if (courseId != null) {
                StudentEntity entity = map.get(id);
                List<Long> courseIds = entity.getCourseIds();
                courseIds.add(courseId);
                StudentEntity entityWithCourseId = entity.toBuilder().courseIds(courseIds).build();
                map.put(id, entityWithCourseId);
            }
        }

        return map;
    }
}
