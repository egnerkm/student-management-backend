package com.kegner.studentmanagement.model;

import java.time.LocalDate;
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
public class StudentEntity {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phoneNumber;
    private final LocalDate dateOfBirth;
    private final Double gpa;
    private final String major;
    @Builder.Default
    private final List<Long> courseIds = new ArrayList<>();
    private final OffsetDateTime createdDate;
    private final OffsetDateTime modifiedDate;
}
