package com.bond.chess_dashboard.game;

import java.time.Instant;

interface StudentActivityProjection {
    Long getStudentId();
    long getGamesLast14Days();
    long getGamesLast7Days();
    Instant getLastPlayedAt();
}
