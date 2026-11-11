package com.bond.chess_dashboard.dashboard.dto;

public record DashboardSummary(
    int totalStudents,
    long activeThisWeek,
    MostActiveStudent mostActive
) {}
