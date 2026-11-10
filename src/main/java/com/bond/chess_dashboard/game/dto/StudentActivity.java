package com.bond.chess_dashboard.game.dto;

import java.time.Instant;

public record StudentActivity(
    Long studentId, 
    long gamesLast14Days, 
    long gamesLast7Days, 
    Instant lastPlayedAt
) {}
