package com.kegner.studentmanagement.controller;

import java.util.List;

import com.kegner.studentmanagement.model.ListWrapper;

public interface BaseRestController<T> {
    public T get(Long id);

    public ListWrapper<T> getAll(List<Long> ids, String sort, String search, Integer page, Integer pageSize);

    public void insert(T object);

    public void update(T object);

    public void delete(Long id);
}
