package com.kegner.studentmanagement.model;

import java.util.ArrayList;
import java.util.List;

import com.kegner.studentmanagement.persistence.BaseRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DataQuery {
    @Builder.Default
    private final List<Long> ids = new ArrayList<>();
    private final String search;
    private final String sort;
    @Builder.Default
    private final Integer page = 0;
    @Builder.Default
    private final Integer pageSize = BaseRepository.MAX_LIMIT;
}