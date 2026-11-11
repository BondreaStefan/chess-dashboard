package com.bond.chess_dashboard.dashboard.dto;

import java.time.Instant;
import com.bond.chess_dashboard.dashboard.ActivityLevel;

public record StudentOverview(
    Long id,
    String firstName,
    String lastName,
    long gamesLast14Days,
    ActivityLevel activityLevel,
    Instant lastPlayedAt,
    RatingOverview rating
) {}
