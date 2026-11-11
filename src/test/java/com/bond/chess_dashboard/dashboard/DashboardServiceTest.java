package com.bond.chess_dashboard.dashboard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.dashboard.dto.DashboardResponse;
import com.bond.chess_dashboard.dashboard.dto.StudentOverview;
import com.bond.chess_dashboard.game.GameStatsService;
import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;
import com.bond.chess_dashboard.game.dto.PlatformBreakdown;
import com.bond.chess_dashboard.game.dto.RatingPoint;
import com.bond.chess_dashboard.game.dto.StudentActivity;
import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-07T12:00:00Z");
    private static final Instant SINCE_7 = NOW.minus(Duration.ofDays(7));
    private static final Instant SINCE_14 = NOW.minus(Duration.ofDays(14));
    private static final AuthenticatedCoach COACH =
            new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Mock
    private StudentService studentService;

    @Mock
    private GameStatsService gameStatsService;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
        dashboardService = new DashboardService(studentService, gameStatsService, clock);
    }

    private static StudentResponse student(Long id, String firstName) {
        return new StudentResponse(id, firstName, "Test", firstName.toLowerCase() + "@example.com",
                COACH.id(), null, null, NOW.atOffset(ZoneOffset.UTC), null);
    }

    @Test
    void queriesStatisticsOnlyForCoachStudents() {
        when(studentService.getStudents(COACH))
                .thenReturn(List.of(student(10L, "Ana"), student(11L, "Bogdan")));

        dashboardService.getDashboard(COACH);

        verify(gameStatsService).getActivity(List.of(10L, 11L), SINCE_14, SINCE_7);
        verify(gameStatsService).getPlatformBreakdown(List.of(10L, 11L), SINCE_14);
    }

    @Test
    void showsStudentWithoutGamesAsInactiveWithoutRating() {
        when(studentService.getStudents(COACH)).thenReturn(List.of(student(10L, "Ana")));

        DashboardResponse response = dashboardService.getDashboard(COACH);

        StudentOverview overview = response.students().getFirst();
        assertThat(overview.gamesLast14Days()).isZero();
        assertThat(overview.activityLevel()).isEqualTo(ActivityLevel.INACTIVE);
        assertThat(overview.lastPlayedAt()).isNull();
        assertThat(overview.rating()).isNull();

        assertThat(response.summary().activeThisWeek()).isZero();
        assertThat(response.summary().mostActive()).isNull();

        verify(gameStatsService, never()).getRatingHistory(any(), any(), any(), any());
    }

    @Test
    void buildsRatingFromDominantCombination() {
        Instant lastBlitz = NOW.minus(Duration.ofDays(1));

        when(studentService.getStudents(COACH)).thenReturn(List.of(student(10L, "Ana")));
        when(gameStatsService.getActivity(List.of(10L), SINCE_14, SINCE_7))
                .thenReturn(List.of(new StudentActivity(10L, 6, 2, lastBlitz)));
        when(gameStatsService.getPlatformBreakdown(List.of(10L), SINCE_14))
                .thenReturn(List.of(
                        new PlatformBreakdown(10L, Platform.CHESS_COM, TimeClass.BLITZ, 5, lastBlitz),
                        new PlatformBreakdown(10L, Platform.CHESS_COM, TimeClass.RAPID, 1, NOW.minus(Duration.ofDays(3)))));
        when(gameStatsService.getRatingHistory(10L, Platform.CHESS_COM, TimeClass.BLITZ,
                lastBlitz.minus(Duration.ofDays(90))))
                .thenReturn(List.of(
                        new RatingPoint(NOW.minus(Duration.ofDays(20)), 1400),
                        new RatingPoint(NOW.minus(Duration.ofDays(5)), 1420),
                        new RatingPoint(lastBlitz, 1437)));

        StudentOverview overview = dashboardService.getDashboard(COACH).students().getFirst();

        assertThat(overview.activityLevel()).isEqualTo(ActivityLevel.ACTIVE);
        assertThat(overview.rating().platform()).isEqualTo(Platform.CHESS_COM);
        assertThat(overview.rating().timeClass()).isEqualTo(TimeClass.BLITZ);
        assertThat(overview.rating().current()).isEqualTo(1437);
        assertThat(overview.rating().change()).isEqualTo(37);   // baseline 1400, before window
        assertThat(overview.rating().trend()).isEqualTo(Trend.UP);
        assertThat(overview.rating().history()).hasSize(3);
    }

    @Test
    void keepsChartForInactiveStudentBasedOnLastGame() {
        Instant lastPlayed = NOW.minus(Duration.ofDays(200));

        when(studentService.getStudents(COACH)).thenReturn(List.of(student(10L, "Ana")));
        when(gameStatsService.getPlatformBreakdown(List.of(10L), SINCE_14))
                .thenReturn(List.of(
                        new PlatformBreakdown(10L, Platform.LICHESS, TimeClass.RAPID, 0, lastPlayed)));
        when(gameStatsService.getRatingHistory(10L, Platform.LICHESS, TimeClass.RAPID,
                lastPlayed.minus(Duration.ofDays(90))))
                .thenReturn(List.of(
                        new RatingPoint(lastPlayed.minus(Duration.ofDays(30)), 1600),
                        new RatingPoint(lastPlayed, 1650)));

        StudentOverview overview = dashboardService.getDashboard(COACH).students().getFirst();

        assertThat(overview.activityLevel()).isEqualTo(ActivityLevel.INACTIVE);
        assertThat(overview.rating()).isNotNull();
        assertThat(overview.rating().current()).isEqualTo(1650);
        assertThat(overview.rating().change()).isNull();
        assertThat(overview.rating().trend()).isEqualTo(Trend.STABLE);
    }

    @Test
    void summarizesActivityAcrossStudents() {
        when(studentService.getStudents(COACH))
                .thenReturn(List.of(student(10L, "Ana"), student(11L, "Bogdan"), student(12L, "Carmen")));
        when(gameStatsService.getActivity(List.of(10L, 11L, 12L), SINCE_14, SINCE_7))
                .thenReturn(List.of(
                        new StudentActivity(10L, 3, 0, NOW.minus(Duration.ofDays(9))),
                        new StudentActivity(11L, 12, 4, NOW.minus(Duration.ofDays(1)))));

        DashboardResponse response = dashboardService.getDashboard(COACH);

        assertThat(response.summary().totalStudents()).isEqualTo(3);
        assertThat(response.summary().activeThisWeek()).isEqualTo(1);
        assertThat(response.summary().mostActive().studentId()).isEqualTo(11L);
        assertThat(response.summary().mostActive().name()).isEqualTo("Bogdan Test");
        assertThat(response.summary().mostActive().games()).isEqualTo(12);
    }
}
