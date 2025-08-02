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
import com.kegner.studentmanagement.model.CourseDto;
import com.kegner.studentmanagement.model.DataQuery;
import com.kegner.studentmanagement.model.ListWrapper;
import com.kegner.studentmanagement.service.CourseService;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<Long> idCaptor;

    @Captor
    private ArgumentCaptor<DataQuery> queryCaptor;

    @Captor
    private ArgumentCaptor<CourseDto> courseCaptor;

    @Test
    void testGet() throws Exception {
        when(service.get(anyLong())).thenReturn(CourseDto.builder().id(20L).build());

        mockMvc.perform(get("/api/v1/courses/{id}", 20L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L));

        verify(service).get(idCaptor.capture());
        assertEquals(20L, idCaptor.getValue());
    }

    @Test
    void testGetAll() throws Exception {
        when(service.getAll(any(DataQuery.class)))
                .thenReturn(ListWrapper.<CourseDto>builder().count(10).build());

        mockMvc.perform(get("/api/v1/courses")
                .param("ids", "1,2")
                .param("page", "0")
                .param("pageSize", "10")
                .param("sort", "courseName:asc")
                .param("search", "History"))
                .andExpect(status().isOk());

        verify(service).getAll(queryCaptor.capture());
        assertEquals(List.of(1L, 2L), queryCaptor.getValue().getIds());
        assertEquals(0, queryCaptor.getValue().getPage());
        assertEquals(10, queryCaptor.getValue().getPageSize());
        assertEquals("History", queryCaptor.getValue().getSearch());
        assertEquals("courseName:asc", queryCaptor.getValue().getSort());
    }

    @Test
    void testInsert() throws Exception {
        doNothing().when(service).insert(any(CourseDto.class));

        String requestBody = objectMapper.writeValueAsString(CourseDto.builder().id(5L).build());

        mockMvc.perform(post("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(service).insert(courseCaptor.capture());
        assertEquals(5L, courseCaptor.getValue().getId());
    }

    @Test
    void testUpdate() throws Exception {
        doNothing().when(service).update(any(CourseDto.class));

        String requestBody = objectMapper.writeValueAsString(CourseDto.builder().id(15L).build());

        mockMvc.perform(put("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(service).update(courseCaptor.capture());
        assertEquals(15L, courseCaptor.getValue().getId());
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(anyLong());

        mockMvc.perform(delete("/api/v1/courses/{id}", 30L))
                .andExpect(status().isOk());

        verify(service).delete(idCaptor.capture());
        assertEquals(30L, idCaptor.getValue());
    }
}
