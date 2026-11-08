package com.bond.chess_dashboard.game;

import java.time.Instant;

interface StudentCombinationProjection {
    Long getStudentId();
    Platform getPlatform();
    TimeClass getTimeClass();
    long getRecentGames();
    Instant getLastPlayedAt();
}
