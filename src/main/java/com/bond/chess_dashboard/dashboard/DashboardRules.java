package com.bond.chess_dashboard.dashboard;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Comparator;

import com.bond.chess_dashboard.game.dto.PlatformBreakdown;
import com.bond.chess_dashboard.game.dto.RatingPoint;

final class DashboardRules {

    private static final int TREND_THRESHOLD = 10;
    private static final int LOW_ACTIVITY_MAX = 5;
    private static final int ACTIVE_MAX = 20;

    private DashboardRules() {
        }

    static ActivityLevel activityLevel(long gamesLast14Days) {
        if (gamesLast14Days == 0) {
            return ActivityLevel.INACTIVE;
        } else if (gamesLast14Days <= LOW_ACTIVITY_MAX) {
            return ActivityLevel.LOW;
        } else if (gamesLast14Days <= ACTIVE_MAX) {
            return ActivityLevel.ACTIVE;
        } else {
            return ActivityLevel.VERY_ACTIVE;
        }
    }

    static Optional<PlatformBreakdown> chooseCombination(List<PlatformBreakdown> breakdown) {
        Comparator<PlatformBreakdown> byRecentGames = Comparator.comparingLong(PlatformBreakdown::recentGames);

        Comparator<PlatformBreakdown> byLastPlayed = Comparator.comparing(PlatformBreakdown::lastPlayedAt,
                Comparator.nullsFirst(Comparator.naturalOrder()));

        return breakdown.stream()
                .max(byRecentGames.thenComparing(byLastPlayed));
    }

    static Integer ratingChange(List<RatingPoint> points, Instant windowStart) {
        List<RatingPoint> inWindow = points.stream()
                .filter(p -> !p.playedAt().isBefore(windowStart))
                .sorted(Comparator.comparing(RatingPoint::playedAt))
                .toList();

        if (inWindow.isEmpty()) {
            return null;
        }

        Optional<RatingPoint> baseline = points.stream()
                .filter(p -> p.playedAt().isBefore(windowStart))
                .max(Comparator.comparing(RatingPoint::playedAt));

        if (baseline.isEmpty() && inWindow.size() < 2) {
            return null;
        }

        int start = baseline
                .map(RatingPoint::rating)
                .orElse(inWindow.getFirst().rating());

        return inWindow.getLast().rating() - start;
    }

    static Trend trend(Integer change) {
        if (change == null) {
            return Trend.STABLE;
        }
        if (change >= TREND_THRESHOLD) {
            return Trend.UP;
        }
        if (change <= -TREND_THRESHOLD) {
            return Trend.DOWN;
        }
        return Trend.STABLE;
    }
}
