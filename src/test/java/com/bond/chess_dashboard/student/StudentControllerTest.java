package com.bond.chess_dashboard.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bond.chess_dashboard.auth.JwtService;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.config.SecurityConfig;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@WebMvcTest(StudentController.class)
@Import(SecurityConfig.class)
class StudentControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean 
    private JwtService jwtService;

    private static final AuthenticatedCoach COACH = new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Test
    void returns404WhenStudentDoesNotExist() throws Exception {
        when(studentService.getStudentById(1L, COACH))
            .thenThrow(new ResourceNotFoundException("Student", 1L));

        mockMvc.perform(get("/api/v1/students/1")
                        .with(asCoach()))
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
                        .with(asCoach())
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

        when(studentService.createStudent(any(CreateStudentRequest.class), eq(COACH))).thenReturn(response);

        mockMvc.perform(post("/api/v1/students")
                        .with(asCoach())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ion"));

        verify(studentService).createStudent(any(CreateStudentRequest.class), eq(COACH));
    }

    @Test
    void listsStudentsForAuthenticatedCoach() throws Exception {
        StudentResponse student = new StudentResponse(
            1L, "Andrei", "Ionescu", "andrei@example.com",
            5L, null, null, OffsetDateTime.now(), null);

        when(studentService.getStudents(COACH)).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/students")
                        .with(asCoach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Andrei"))
                .andExpect(jsonPath("$[0].coachId").value(5));

        verify(studentService).getStudents(COACH);
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(studentService);
    }

    @Test
    void rejectsCoachAssigningStudentToAnotherCoach() throws Exception {
        String body = """
                {
                "coachId": 7
                }
                """;

        mockMvc.perform(put("/api/v1/students/1/coach")
                        .with(asCoach())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        verifyNoInteractions(studentService);
    }

    @Test
    void allowsAdminToAssignStudent() throws Exception {
        StudentResponse response = new StudentResponse(1L, "Ion", "Popescu",
            "ion@example.com", 7L, null, null, OffsetDateTime.now(), null);

        when(studentService.assignCoach(1L, 7L)).thenReturn(response);

        mockMvc.perform(put("/api/v1/students/1/coach")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"coachId": 7}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coachId").value(7));
    }

    private static RequestPostProcessor asCoach() {
        return authentication(new UsernamePasswordAuthenticationToken(
                COACH, null, List.of(new SimpleGrantedAuthority("ROLE_COACH"))));
    }

    private static RequestPostProcessor asAdmin() {
        AuthenticatedCoach admin = new AuthenticatedCoach(9L, "admin@example.com", Role.ADMIN);
        return authentication(new UsernamePasswordAuthenticationToken(
                admin, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }
}
