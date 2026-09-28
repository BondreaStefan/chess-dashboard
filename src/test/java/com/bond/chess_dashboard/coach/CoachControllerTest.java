package com.bond.chess_dashboard.coach;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.bond.chess_dashboard.auth.JwtService;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.common.config.SecurityConfig;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;

@WebMvcTest(CoachController.class)
@Import(SecurityConfig.class)
class CoachControllerTest {

    private static final AuthenticatedCoach COACH = new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoachService coachService;

    @MockitoBean 
    private JwtService jwtService;

    @Test
    void returns404WhenCoachDoesNotExist() throws Exception {
        when(coachService.getCoachById(999L))
                .thenThrow(new ResourceNotFoundException("Coach", 999L));

        mockMvc.perform(get("/api/v1/coaches/999").with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Coach with id 999 not found"));
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/coaches"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(coachService);
    }

    @Test
    void coachCanReadOwnProfile() throws Exception {
        when(coachService.getCurrentCoach(COACH)).thenReturn(
                new CoachResponse(1L, "Ana", "Antrenor", "coach@example.com", null, null));

        mockMvc.perform(get("/api/v1/coaches/me").with(asCoach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void coachCannotListCoaches() throws Exception {
        mockMvc.perform(get("/api/v1/coaches").with(asCoach()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(coachService);
    }

    @Test
    void adminCanListCoaches() throws Exception {
        when(coachService.getAllCoaches()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/coaches").with(asAdmin()))
                .andExpect(status().isOk());
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
