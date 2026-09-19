package com.bond.chess_dashboard.auth.dto;

import com.bond.chess_dashboard.coach.Role;

public record AuthResponse(
    String token,
    Long coachId,
    String email,
    Role role
) {}
