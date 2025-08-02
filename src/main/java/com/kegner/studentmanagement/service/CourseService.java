package com.kegner.studentmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kegner.studentmanagement.exceptions.ResourceExistsException;
import com.kegner.studentmanagement.exceptions.ResourceNotFoundException;
import com.kegner.studentmanagement.model.CourseDto;
import com.kegner.studentmanagement.model.CourseEntity;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.persistence.CourseRepository;

@Service
public class CourseService implements BaseService<CourseDto> {
    @Autowired
    private CourseRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CourseDto get(Long id) {
        CourseEntity entity = repository.get(id);
        if (entity != null) {
            return modelMapper.map(entity, CourseDto.class);
        }

        throw new ResourceNotFoundException("A course with the ID " + id + " could not be found.");
    }

    @Override
    public ListWrapper<CourseDto> getAll(DataQuery query) {
        ListWrapper<CourseEntity> courses = repository.getAll(query);
        List<CourseDto> courseDtos = new ArrayList<>();

        for (CourseEntity entity : courses.getData()) {
            courseDtos.add(modelMapper.map(entity, CourseDto.class));
        }

        return ListWrapper.<CourseDto>builder()
                .data(courseDtos)
                .count(courses.getCount())
                .total(courses.getTotal())
                .build();
    }

    @Override
    public void insert(CourseDto dto) {
        // enforce that POST should create resources
        if (dto.getId() != null && repository.get(dto.getId()) != null) {
            throw new ResourceExistsException("A course with the ID " + dto.getId() + " already exists.");
        }

        CourseEntity entity = modelMapper.map(dto, CourseEntity.class);
        repository.insert(entity);
    }

    @Override
    public void update(CourseDto dto) {
        CourseEntity entity = modelMapper.map(dto, CourseEntity.class);
        repository.update(entity);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
