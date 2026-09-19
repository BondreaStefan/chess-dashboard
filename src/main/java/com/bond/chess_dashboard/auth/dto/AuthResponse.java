package com.bond.chess_dashboard.auth.dto;

import com.bond.chess_dashboard.coach.Role;

public record AuthResponse(
    String token,
    Long coachId,
    String firstName,
    String lastName,
    String email,
    Role role
) {}
