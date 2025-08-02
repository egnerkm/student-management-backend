package com.kegner.studentmanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kegner.studentmanagement.model.CourseDto;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.service.CourseService;

@RestController
@CrossOrigin(origins = "${cors.allowed-origins}")
@RequestMapping("/api/v1/courses")
public class CourseController implements BaseRestController<CourseDto> {
    @Autowired
    private CourseService service;

    @GetMapping("/{id}")
    @Override
    public CourseDto get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    @Override
    public ListWrapper<CourseDto> getAll(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize) {
        DataQuery.DataQueryBuilder builder = DataQuery.builder()
                .ids(ids)
                .search(search)
                .sort(sort);

        // Only override page defaults if they were provided
        if (page != null) {
            builder.page(page);
        }

        if (pageSize != null) {
            builder.pageSize(pageSize);
        }

        return service.getAll(builder.build());
    }

    @PostMapping
    @Override
    public void insert(@RequestBody CourseDto dto) {
        service.insert(dto);
    }

    @PutMapping
    @Override
    public void update(@RequestBody CourseDto dto) {
        service.update(dto);
    }

    @DeleteMapping("/{id}")
    @Override
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
