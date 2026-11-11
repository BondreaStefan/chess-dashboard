package com.bond.chess_dashboard.dashboard.dto;

import java.util.List;
import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;
import com.bond.chess_dashboard.dashboard.Trend;
import com.bond.chess_dashboard.game.dto.RatingPoint;

public record RatingOverview(
    Platform platform,
    TimeClass timeClass,
    int current,
    Integer change,
    Trend trend,
    List<RatingPoint> history
) {}
