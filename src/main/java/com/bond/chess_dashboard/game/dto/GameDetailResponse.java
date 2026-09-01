package com.bond.chess_dashboard.game.dto;

import java.time.OffsetDateTime;

import com.bond.chess_dashboard.game.Color;
import com.bond.chess_dashboard.game.GameResult;
import com.bond.chess_dashboard.game.GameSource;

public record GameDetailResponse(
    Long id,
    Long studentId,
    GameSource source,
    String externalId,
    Color studentColor,
    GameResult studentResult,
    OffsetDateTime playedAt,
    String opponentName,
    String result,
    String ecoCode,
    String timeControl,
    Integer whiteElo,
    Integer blackElo,
    Integer moveCount,
    String pgn,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
