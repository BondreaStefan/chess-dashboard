package com.bond.chess_dashboard.game;

interface StudentStatsProjection {
    long getTotal();
    long getWins();
    long getLosses();
    long getDraws();
    long getGamesAsWhite();
    long getWinsAsWhite();
    long getGamesAsBlack();
    long getWinsAsBlack();
}
