package com.kegner.studentmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import com.kegner.studentmanagement.exceptions.ResourceExistsException;
import com.kegner.studentmanagement.exceptions.ResourceNotFoundException;
import com.kegner.studentmanagement.model.CourseDto;
import com.kegner.studentmanagement.model.CourseEntity;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.persistence.CourseRepository;

@SpringBootTest
public class CourseServiceTest {
    @InjectMocks
    private CourseService service;

    @Mock
    private CourseRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Captor
    private ArgumentCaptor<DataQuery> queryCaptor;

    @Captor
    private ArgumentCaptor<CourseEntity> courseCaptor;

    @Test
    void testGet() throws Exception {
        when(repository.get(anyLong())).thenReturn(CourseEntity.builder().id(20L).build());
        when(modelMapper.map(any(), any())).thenReturn(CourseDto.builder().id(20L).build());

        CourseDto dto = service.get(20L);
        verify(repository).get(idCaptor.capture());
        assertEquals(20L, idCaptor.getValue());
        assertEquals(20L, dto.getId());
    }

    @Test
    void testGetNotFound() throws Exception {
        when(repository.get(anyLong())).thenReturn(null);
        when(modelMapper.map(any(), any())).thenReturn(CourseDto.builder().id(20L).build());
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.get(20L));

        verify(repository).get(idCaptor.capture());
        assertEquals(20L, idCaptor.getValue());
        assertEquals("A course with the ID 20 could not be found.", exception.getMessage());
    }

    @Test
    void testGetAll() throws Exception {
        when(repository.getAll(any(DataQuery.class)))
                .thenReturn(ListWrapper.<CourseEntity>builder().count(10).build());
        when(modelMapper.map(any(), any())).thenReturn(CourseDto.builder().id(50L).build());

        ListWrapper<CourseDto> list = service.getAll(DataQuery.builder().page(0).build());
        verify(repository).getAll(queryCaptor.capture());
        assertEquals(0, queryCaptor.getValue().getPage());
        assertEquals(10, list.getCount());
    }

    @Test
    void testInsert() throws Exception {
        CourseEntity student = CourseEntity.builder().id(25L).build();

        doNothing().when(repository).insert(any(CourseEntity.class));
        when(repository.get(anyLong())).thenReturn(null);
        when(modelMapper.map(any(), any())).thenReturn(student);

        service.insert(CourseDto.builder().id(25L).build());
        verify(repository).get(idCaptor.capture());
        verify(repository).insert(courseCaptor.capture());
        assertEquals(25L, idCaptor.getValue());
        assertEquals(25L, courseCaptor.getValue().getId());
    }

    @Test
    void testInsertResourceExists() throws Exception {
        CourseEntity student = CourseEntity.builder().id(25L).build();

        doNothing().when(repository).insert(any(CourseEntity.class));
        when(repository.get(anyLong())).thenReturn(student);
        when(modelMapper.map(any(), any())).thenReturn(student);

        ResourceExistsException exception = assertThrows(
                ResourceExistsException.class,
                () -> service.insert(CourseDto.builder().id(25L).build()));

        verify(repository).get(idCaptor.capture());
        assertEquals(25L, idCaptor.getValue());
        assertEquals("A course with the ID 25 already exists.", exception.getMessage());
    }

    @Test
    void testUpdate() throws Exception {
        CourseEntity student = CourseEntity.builder().id(25L).build();

        doNothing().when(repository).update(any(CourseEntity.class));
        when(modelMapper.map(any(), any())).thenReturn(student);

        service.update(CourseDto.builder().id(25L).build());
        verify(repository).update(courseCaptor.capture());
        assertEquals(25L, courseCaptor.getValue().getId());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(repository).delete(anyLong());

        service.delete(35L);
        verify(repository).delete(idCaptor.capture());
        assertEquals(35L, idCaptor.getValue());
    }
}
