package com.kegner.studentmanagement.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StudentDto {
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
    @JsonProperty(access = Access.READ_ONLY)
    private final OffsetDateTime createdDate;
    @JsonProperty(access = Access.READ_ONLY)
    private final OffsetDateTime modifiedDate;

    @JsonProperty("fullName")
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
