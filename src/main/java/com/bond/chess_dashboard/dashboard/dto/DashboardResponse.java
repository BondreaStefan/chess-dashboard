package com.bond.chess_dashboard.dashboard.dto;

import java.util.List;

public record DashboardResponse(
    DashboardSummary summary, 
    List<StudentOverview> students
) {}