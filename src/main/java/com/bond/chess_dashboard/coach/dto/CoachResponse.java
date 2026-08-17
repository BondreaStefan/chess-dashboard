package com.bond.chess_dashboard.coach.dto;

import java.time.OffsetDateTime;

public record CoachResponse(
    Long id,
    String firstName,
    String lastName,
    String email,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}