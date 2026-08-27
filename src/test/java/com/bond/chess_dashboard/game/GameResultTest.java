package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GameResultTest {

    @ParameterizedTest
    @CsvSource({
            "1-0,      WHITE, WIN",
            "1-0,      BLACK, LOSS",
            "0-1,      BLACK, WIN",
            "0-1,      WHITE, LOSS",
            "1/2-1/2,  WHITE, DRAW",
            "1/2-1/2,  BLACK, DRAW",
            "*,        WHITE, UNKNOWN"
    })
    void mapsPgnResultToStudentPerspective(String pgnResult, Color color, GameResult expected) {
        assertThat(GameResult.from(pgnResult, color)).isEqualTo(expected);
    }

    @Test
    void returnsUnknownForNullResult() {
        assertThat(GameResult.from(null, Color.WHITE)).isEqualTo(GameResult.UNKNOWN);
    }
}