package com.bond.chess_dashboard.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;
import com.bond.chess_dashboard.game.dto.PlatformBreakdown;
import com.bond.chess_dashboard.game.dto.RatingPoint;

class DashboardRulesTest {

    private static final Instant WINDOW_START = Instant.parse("2026-10-24T00:00:00Z");

    private static PlatformBreakdown breakdown(TimeClass timeClass, long recentGames, String lastPlayedAt) {
        return new PlatformBreakdown(1L, Platform.CHESS_COM, timeClass, recentGames,
                lastPlayedAt == null ? null : Instant.parse(lastPlayedAt));
    }

    private static RatingPoint point(String playedAt, int rating) {
        return new RatingPoint(Instant.parse(playedAt), rating);
    }

    // --- activityLevel ---

    @ParameterizedTest(name = "{0} games -> {1}")
    @CsvSource({
            "0,  INACTIVE",
            "1,  LOW",
            "5,  LOW",
            "6,  ACTIVE",
            "20, ACTIVE",
            "21, VERY_ACTIVE"
    })
    void classifiesActivity(long games, ActivityLevel expected) {
        assertThat(DashboardRules.activityLevel(games)).isEqualTo(expected);
    }

    // --- chooseCombination ---

    @Test
    void choosesTheOnlyCombination() {
        PlatformBreakdown blitz = breakdown(TimeClass.BLITZ, 3, "2026-11-01T10:00:00Z");

        assertThat(DashboardRules.chooseCombination(List.of(blitz))).contains(blitz);
    }

    @Test
    void choosesCombinationWithMostRecentGames() {
        PlatformBreakdown blitz = breakdown(TimeClass.BLITZ, 5, "2026-11-01T10:00:00Z");
        PlatformBreakdown rapid = breakdown(TimeClass.RAPID, 2, "2026-11-05T10:00:00Z");

        assertThat(DashboardRules.chooseCombination(List.of(rapid, blitz))).contains(blitz);
    }

    @Test
    void breaksTiesByMostRecentlyPlayed() {
        PlatformBreakdown blitz = breakdown(TimeClass.BLITZ, 3, "2026-11-01T10:00:00Z");
        PlatformBreakdown rapid = breakdown(TimeClass.RAPID, 3, "2026-11-05T10:00:00Z");

        assertThat(DashboardRules.chooseCombination(List.of(blitz, rapid))).contains(rapid);
    }

    @Test
    void fallsBackToMostRecentlyPlayedWhenNothingIsRecent() {
        PlatformBreakdown blitz = breakdown(TimeClass.BLITZ, 0, "2026-08-01T10:00:00Z");
        PlatformBreakdown bullet = breakdown(TimeClass.BULLET, 0, "2026-09-15T10:00:00Z");

        assertThat(DashboardRules.chooseCombination(List.of(blitz, bullet))).contains(bullet);
    }

    @Test
    void prefersDatedCombinationOverUndatedOne() {
        PlatformBreakdown undated = breakdown(TimeClass.BLITZ, 0, null);
        PlatformBreakdown dated = breakdown(TimeClass.RAPID, 0, "2026-08-01T10:00:00Z");

        assertThat(DashboardRules.chooseCombination(List.of(undated, dated))).contains(dated);
    }

    @Test
    void returnsEmptyWhenThereAreNoCombinations() {
        assertThat(DashboardRules.chooseCombination(List.of())).isEmpty();
    }

    // --- ratingChange ---

    @Test
    void returnsNullWhenNoGamesInWindow() {
        List<RatingPoint> points = List.of(point("2026-10-01T10:00:00Z", 1500));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isNull();
    }

    @Test
    void returnsNullForSinglePointWithoutBaseline() {
        List<RatingPoint> points = List.of(point("2026-10-30T10:00:00Z", 1500));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isNull();
    }

    @Test
    void comparesSinglePointWithBaseline() {
        List<RatingPoint> points = List.of(
                point("2026-10-20T10:00:00Z", 1500),
                point("2026-10-30T10:00:00Z", 1530));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isEqualTo(30);
    }

    @Test
    void comparesFirstAndLastInWindowWithoutBaseline() {
        List<RatingPoint> points = List.of(
                point("2026-10-25T10:00:00Z", 1400),
                point("2026-10-28T10:00:00Z", 1420),
                point("2026-11-02T10:00:00Z", 1437));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isEqualTo(37);
    }

    @Test
    void usesLastRatingBeforeWindowAsBaseline() {
        List<RatingPoint> points = List.of(
                point("2026-10-10T10:00:00Z", 1550),
                point("2026-10-22T10:00:00Z", 1500),
                point("2026-10-25T10:00:00Z", 1480),
                point("2026-10-28T10:00:00Z", 1460),
                point("2026-11-02T10:00:00Z", 1440));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isEqualTo(-60);
    }

    @Test
    void handlesUnsortedPoints() {
        List<RatingPoint> points = List.of(
                point("2026-11-02T10:00:00Z", 1440),
                point("2026-10-22T10:00:00Z", 1500),
                point("2026-10-25T10:00:00Z", 1480));

        assertThat(DashboardRules.ratingChange(points, WINDOW_START)).isEqualTo(-60);
    }

    // --- trend ---

    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "37,  UP",
            "10,  UP",
            "9,   STABLE",
            "0,   STABLE",
            "-9,  STABLE",
            "-10, DOWN",
            "-15, DOWN"
    })
    void classifiesTrend(int change, Trend expected) {
        assertThat(DashboardRules.trend(change)).isEqualTo(expected);
    }

    @Test
    void treatsMissingChangeAsStable() {
        assertThat(DashboardRules.trend(null)).isEqualTo(Trend.STABLE);
    }
}
