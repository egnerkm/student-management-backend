package com.kegner.studentmanagement.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CourseEntity {
    private final Long id;
    private final String courseName;
    private final String departmentName;
    private final String semester;
    private final Integer courseYear;
    private final Integer credits;
    private final String professorName;
    @Builder.Default
    private final List<Long> studentIds = new ArrayList<>();
    private final OffsetDateTime createdDate;
    private final OffsetDateTime modifiedDate;
}
