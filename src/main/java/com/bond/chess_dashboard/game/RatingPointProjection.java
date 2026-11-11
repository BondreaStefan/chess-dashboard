package com.bond.chess_dashboard.game;

import java.time.Instant;

public interface RatingPointProjection {
    Instant getPlayedAt();
    int getRating();
}
