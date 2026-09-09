package com.bond.chess_dashboard.game.dto;


public record ImportGamesResponse(
    int imported,
    int skipped
) {}