package com.bond.chess_dashboard.student.dto;

import java.time.OffsetDateTime;

public record StudentResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    Long coachId,
    String lichessUsername,
    String chessComUsername,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}

