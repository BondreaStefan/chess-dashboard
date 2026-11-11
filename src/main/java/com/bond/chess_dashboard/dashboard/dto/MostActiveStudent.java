package com.bond.chess_dashboard.dashboard.dto;

public record MostActiveStudent(
    Long studentId, 
    String name, 
    long games
) {}
