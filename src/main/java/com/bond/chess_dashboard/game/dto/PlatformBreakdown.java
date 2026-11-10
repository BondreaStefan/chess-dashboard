package com.bond.chess_dashboard.game.dto;

import java.time.Instant;

import com.bond.chess_dashboard.game.Platform;
import com.bond.chess_dashboard.game.TimeClass;

public record PlatformBreakdown(
    Long studentId, 
    Platform platform, 
    TimeClass timeClass, 
    long recentGames, 
    Instant lastPlayedAt
) {}
