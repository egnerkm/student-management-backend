package com.kegner.studentmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.model.StudentDto;
import com.kegner.studentmanagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Captor
    private ArgumentCaptor<DataQuery> queryCaptor;

    @Captor
    private ArgumentCaptor<StudentDto> studentCaptor;

    @Test
    void testGet() throws Exception {
        when(service.get(anyLong())).thenReturn(StudentDto.builder().id(20L).build());

        mockMvc.perform(get("/api/v1/students/{id}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L));

        verify(service).get(idCaptor.capture());
        assertEquals(20L, idCaptor.getValue());
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAll(any(DataQuery.class)))
                .thenReturn(ListWrapper.<StudentDto>builder().count(10).build());

        mockMvc.perform(get("/api/v1/students")
                .param("ids", "1,2")
                .param("page", "0")
                .param("pageSize", "10")
                .param("sort", "firstName:asc")
                .param("search", "Bob"))
                .andExpect(status().isOk());

        verify(service).getAll(queryCaptor.capture());
        assertEquals(List.of(1L, 2L), queryCaptor.getValue().getIds());
        assertEquals(0, queryCaptor.getValue().getPage());
        assertEquals(10, queryCaptor.getValue().getPageSize());
        assertEquals("Bob", queryCaptor.getValue().getSearch());
        assertEquals("firstName:asc", queryCaptor.getValue().getSort());
    }

    @Test
    void testInsert() throws Exception {
        doNothing().when(service).insert(any(StudentDto.class));

        String requestBody = objectMapper.writeValueAsString(StudentDto.builder().id(5L).build());

        mockMvc.perform(post("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(service).insert(studentCaptor.capture());
        assertEquals(5L, studentCaptor.getValue().getId());
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(service).update(any(StudentDto.class));

        String requestBody = objectMapper.writeValueAsString(StudentDto.builder().id(15L).build());

        mockMvc.perform(put("/api/v1/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(service).update(studentCaptor.capture());
        assertEquals(15L, studentCaptor.getValue().getId());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(anyLong());

        mockMvc.perform(delete("/api/v1/students/{id}", 30L))
                .andExpect(status().isOk());

        verify(service).delete(idCaptor.capture());
        assertEquals(30L, idCaptor.getValue());
    }
}
