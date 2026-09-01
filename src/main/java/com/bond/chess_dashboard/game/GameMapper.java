package com.bond.chess_dashboard.game;

import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.GameSummaryResponse;

class GameMapper {
    
    private GameMapper() {

    }

    static GameSummaryResponse toSummaryResponse(Game game) {
        return new GameSummaryResponse(
            game.getId(),
            game.getStudentId(),
            game.getSource(),
            game.getStudentColor(),
            game.getStudentResult(),
            game.getPlayedAt(),
            game.getOpponentName(),
            game.getResult(),
            game.getEcoCode(),
            game.getTimeControl(),
            game.getWhiteElo(),
            game.getBlackElo(),
            game.getMoveCount()
        );
    }

    static GameDetailResponse toDetailResponse(Game game) {
        return new GameDetailResponse(
            game.getId(),
            game.getStudentId(),
            game.getSource(),
            game.getExternalId(),
            game.getStudentColor(),
            game.getStudentResult(),
            game.getPlayedAt(),
            game.getOpponentName(),
            game.getResult(),
            game.getEcoCode(),
            game.getTimeControl(),
            game.getWhiteElo(),
            game.getBlackElo(),
            game.getMoveCount(),
            game.getPgn(),
            game.getCreatedAt(),
            game.getUpdatedAt()
        );
    }
}
