package com.bond.chess_dashboard.game;

import java.time.OffsetDateTime;

public record ParsedGame(
    String result,          // "1-0", brut
    String whiteName,
    String blackName,
    Integer whiteElo,
    Integer blackElo,
    OffsetDateTime playedAt,
    String ecoCode,
    String timeControl,
    int moveCount
) {}
