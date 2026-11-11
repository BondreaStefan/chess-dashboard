package com.bond.chess_dashboard.game.dto;

import java.time.Instant;

public record RatingPoint(
    Instant playedAt, 
    int rating
) {}
