package com.bond.chess_dashboard.coach.dto;

import com.bond.chess_dashboard.coach.Role;

public record CoachCredentials(
    Long id,
    String email,
    String passwordHash,
    Role role,
    boolean enabled
) {}
