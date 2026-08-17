package com.bond.chess_dashboard.coach;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.coach.dto.CreateCoachRequest;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;

@WebMvcTest(CoachController.class)
class CoachControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoachService coachService;

    @Test
    void returns404WhenCoachDoesNotExist() throws Exception {
        when(coachService.getCoachById(999L))
                .thenThrow(new ResourceNotFoundException("Coach", 999L));

        mockMvc.perform(get("/api/v1/coaches/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Coach with id 999 not found"));
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

        mockMvc.perform(post("/api/v1/coaches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.firstName").exists());

        verifyNoInteractions(coachService);
    }

    @Test
    void returns201WhenCoachIsCreated() throws Exception {
        String body = """
                {
                "firstName": "Ion",
                "lastName": "Popescu",
                "email": "ion@example.com"
                }
                """;

        CoachResponse response = new CoachResponse(
        1L, "Ion", "Popescu", "ion@example.com", OffsetDateTime.now(), null);

        when(coachService.createCoach(any(CreateCoachRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/coaches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ion"));

        verify(coachService).createCoach(any(CreateCoachRequest.class));
    }
}
