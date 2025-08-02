package com.kegner.studentmanagement.model;

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
public class ListWrapper<T> {
    @Builder.Default
    private final List<T> data = new ArrayList<>();
    private final Integer count;
    private final Integer total;
}
