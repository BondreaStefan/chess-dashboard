package com.bond.chess_dashboard.auth.dto;

import com.bond.chess_dashboard.coach.Role;

public record AuthenticatedCoach(
    Long id,
    String email,
    Role role
) {}
