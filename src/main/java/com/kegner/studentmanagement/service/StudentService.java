package com.kegner.studentmanagement.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kegner.studentmanagement.exceptions.ResourceExistsException;
import com.kegner.studentmanagement.exceptions.ResourceNotFoundException;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.model.StudentDto;
import com.kegner.studentmanagement.model.StudentEntity;
import com.kegner.studentmanagement.persistence.StudentRepository;

@Service
public class StudentService implements BaseService<StudentDto> {
    @Autowired
    private StudentRepository repository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public StudentDto get(Long id) {
        StudentEntity entity = repository.get(id);
        if (entity != null) {
            return modelMapper.map(entity, StudentDto.class);
        }

        throw new ResourceNotFoundException("A student with the ID " + id + " could not be found.");
    }

    @Override
    public ListWrapper<StudentDto> getAll(DataQuery query) {
        ListWrapper<StudentEntity> students = repository.getAll(query);
        List<StudentDto> studentDtos = new ArrayList<>();

        for (StudentEntity entity : students.getData()) {
            studentDtos.add(modelMapper.map(entity, StudentDto.class));
        }

        return ListWrapper.<StudentDto>builder()
                .data(studentDtos)
                .count(students.getCount())
                .total(students.getTotal())
                .build();
    }

    @Override
    public void insert(StudentDto dto) {
        // enforce that POST should create resources
        if (dto.getId() != null && repository.get(dto.getId()) != null) {
            throw new ResourceExistsException("A student with the ID " + dto.getId() + " already exists.");
        }

        StudentEntity entity = modelMapper.map(dto, StudentEntity.class);
        repository.insert(entity);
    }

    @Override
    public void update(StudentDto dto) {
        StudentEntity entity = modelMapper.map(dto, StudentEntity.class);
        repository.update(entity);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }
}
