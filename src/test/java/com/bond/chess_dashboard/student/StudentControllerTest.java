package com.bond.chess_dashboard.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@WebMvcTest(StudentController.class)
class StudentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Test
    void returns404WhenStudentDoesNotExist() throws Exception {
        when(studentService.getStudentById(1L))
            .thenThrow(new ResourceNotFoundException("Student", 1L));

        mockMvc.perform(get("/api/v1/students/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Student with id 1 not found"));
    }

    @Test
    void returns400WhenFirstNameIsBlank() throws Exception {
        String body = """
                {
                "firstName": "",
                "lastName": "Popescu",
                "email": "ion@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());

        verifyNoInteractions(studentService);
    }

    @Test
    void returns201WhenStudentIsCreated() throws Exception {
        String body = """
                {
                "firstName": "Ion",
                "lastName": "Popescu",
                "email": "ion@example.com"
                }
                """;

        StudentResponse response = new StudentResponse(1L, "Ion", "Popescu",
         "ion@example.com", 1L, null, null, OffsetDateTime.now(), null);

        when(studentService.createStudent(any(CreateStudentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ion"));

        verify(studentService).createStudent(any(CreateStudentRequest.class));
    }

    @Test
    void routesToCoachFilterWhenCoachIdIsPresent() throws Exception {
        StudentResponse student = new StudentResponse(
            1L, "Andrei", "Ionescu", "andrei@example.com",
            5L, null, null, OffsetDateTime.now(), null);

        when(studentService.getStudentsByCoachId(5L)).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students").param("coachId", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(jsonPath("$[0].coachId").value(5));

        verify(studentService).getStudentsByCoachId(5L);
        verify(studentService, never()).getAllStudents();
    }

    @Test
    void routesToListAllWhenCoachIdIsAbsent() throws Exception {
        when(studentService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(studentService).getAllStudents();
        verify(studentService, never()).getStudentsByCoachId(any());
    }
}
