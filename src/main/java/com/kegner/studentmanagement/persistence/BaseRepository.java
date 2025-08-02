package com.kegner.studentmanagement.persistence;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;

public interface BaseRepository<T> {
    static final int MAX_LIMIT = 1000;
    static final List<String> validSorting = List.of("ASC", "DESC");

    public T get(Long Id);

    public ListWrapper<T> getAll(DataQuery query);

    public void insert(T object);

    public void update(T object);

    public void delete(Long id);

    // shared method for creating order by clause
    public static String getOrderByClause(DataQuery query, List<String> validColumns) {
        String orderBy = " ORDER BY modified_date DESC NULLS LAST ";

        if (StringUtils.isNotBlank(query.getSort())) {
            String[] sort = query.getSort().split(":");

            if (sort.length == 2) {
                String dbField = sort[0].replaceAll("([A-Z])", "_$1").toLowerCase();
                if (validColumns.contains(dbField) && validSorting.contains(sort[1].toUpperCase())) {
                    orderBy = " ORDER BY " + dbField + " " + sort[1] + " ";
                }
            }
        }

        return orderBy;
    }
}
