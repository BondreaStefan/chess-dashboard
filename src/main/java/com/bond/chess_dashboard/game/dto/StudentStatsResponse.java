package com.bond.chess_dashboard.game.dto;

public record StudentStatsResponse(
    long total,
    long wins,
    long losses,
    long draws,
    double winRate,
    long gamesAsWhite,
    long winsAsWhite,
    double winRateAsWhite,
    long gamesAsBlack,
    long winsAsBlack,
    double winRateAsBlack
) {}
