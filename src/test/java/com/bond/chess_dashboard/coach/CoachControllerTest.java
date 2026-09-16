package com.bond.chess_dashboard.coach;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
