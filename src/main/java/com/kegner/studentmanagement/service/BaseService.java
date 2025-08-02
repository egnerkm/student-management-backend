package com.kegner.studentmanagement.service;

import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;

public interface BaseService<T> {
    public T get(Long Id);

    public ListWrapper<T> getAll(DataQuery query);

    public void insert(T object);

    public void update(T object);

    public void delete(Long id);
}
