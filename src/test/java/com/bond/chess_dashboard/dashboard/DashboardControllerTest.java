package com.bond.chess_dashboard.dashboard;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bond.chess_dashboard.auth.JwtService;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.config.SecurityConfig;
import com.bond.chess_dashboard.dashboard.dto.DashboardResponse;
import com.bond.chess_dashboard.dashboard.dto.DashboardSummary;
import com.bond.chess_dashboard.dashboard.dto.MostActiveStudent;
import com.bond.chess_dashboard.dashboard.dto.RatingOverview;
import com.bond.chess_dashboard.dashboard.dto.StudentOverview;
import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;
import com.bond.chess_dashboard.game.dto.RatingPoint;

@WebMvcTest(DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerTest {

    private static final AuthenticatedCoach COACH =
            new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void returnsDashboardForAuthenticatedCoach() throws Exception {
        Instant lastPlayed = Instant.parse("2026-11-06T12:00:00Z");

        RatingOverview rating = new RatingOverview(
                Platform.CHESS_COM, TimeClass.BLITZ, 1437, 37, Trend.UP,
                List.of(new RatingPoint(Instant.parse("2026-10-20T12:00:00Z"), 1400),
                        new RatingPoint(lastPlayed, 1437)));

        DashboardResponse response = new DashboardResponse(
                new DashboardSummary(2, 1, new MostActiveStudent(10L, "Ana Test", 8)),
                List.of(
                        new StudentOverview(10L, "Ana", "Test", 8, ActivityLevel.ACTIVE, lastPlayed, rating),
                        new StudentOverview(11L, "Bogdan", "Test", 0, ActivityLevel.INACTIVE, null, null)));

        when(dashboardService.getDashboard(COACH)).thenReturn(response);

        mockMvc.perform(get("/api/v1/dashboard").with(asCoach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.totalStudents").value(2))
                .andExpect(jsonPath("$.summary.mostActive.name").value("Ana Test"))
                .andExpect(jsonPath("$.students[0].activityLevel").value("ACTIVE"))
                .andExpect(jsonPath("$.students[0].lastPlayedAt").value("2026-11-06T12:00:00Z"))
                .andExpect(jsonPath("$.students[0].rating.trend").value("UP"))
                .andExpect(jsonPath("$.students[0].rating.history[1].rating").value(1437))
                .andExpect(jsonPath("$.students[1].activityLevel").value("INACTIVE"))
                .andExpect(jsonPath("$.students[1].rating").doesNotExist());
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/dashboard"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(dashboardService);
    }

    private static RequestPostProcessor asCoach() {
        return authentication(new UsernamePasswordAuthenticationToken(
                COACH, null, List.of(new SimpleGrantedAuthority("ROLE_COACH"))));
    }
}
