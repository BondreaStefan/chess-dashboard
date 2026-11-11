package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class TimeClassTest {

    @ParameterizedTest(name = "{0} on {1} -> {2}")
    @CsvSource({
            // Lichess: estimated = base + 40 * increment; < 30 / 180 / 480 / 1500
            "15+0,     LICHESS,   ULTRA_BULLET",
            "29+0,     LICHESS,   ULTRA_BULLET",
            "30+0,     LICHESS,   BULLET",
            "60+0,     LICHESS,   BULLET",
            "179+0,    LICHESS,   BULLET",
            "180+0,    LICHESS,   BLITZ",
            "479+0,    LICHESS,   BLITZ",
            "480+0,    LICHESS,   RAPID",
            "300+5,    LICHESS,   RAPID",
            "1499+0,   LICHESS,   RAPID",
            "1500+0,   LICHESS,   CLASSICAL",
            "1800+0,   LICHESS,   CLASSICAL",

            // Chess.com: same formula; < 180 / 600, no classical category
            "60+0,     CHESS_COM, BULLET",
            "179+0,    CHESS_COM, BULLET",
            "180+0,    CHESS_COM, BLITZ",
            "300+5,    CHESS_COM, BLITZ",
            "599+0,    CHESS_COM, BLITZ",
            "600+0,    CHESS_COM, RAPID",
            "120+12,   CHESS_COM, RAPID",
            "1800+0,   CHESS_COM, RAPID",

            // Other platforms fall back to Lichess rules
            "300+5,    OTHER,     RAPID",

            // Correspondence and unparseable formats
            "1/86400,  CHESS_COM, CORRESPONDENCE",
            "1/259200, CHESS_COM, CORRESPONDENCE",
            "-,        LICHESS,   CORRESPONDENCE",
            "abc,      LICHESS,   UNKNOWN",
            "300+x,    CHESS_COM, UNKNOWN"
    })
    void classifiesTimeControl(String timeControl, Platform platform, TimeClass expected) {
        assertThat(TimeClass.from(timeControl, platform)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void returnsUnknownForMissingTimeControl(String timeControl) {
        assertThat(TimeClass.from(timeControl, Platform.LICHESS)).isEqualTo(TimeClass.UNKNOWN);
    }

    @Test
    void returnsUnknownForMissingPlatform() {
        assertThat(TimeClass.from("300+0", null)).isEqualTo(TimeClass.UNKNOWN);
    }
}
