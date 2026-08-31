package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class PgnParserTest {

    private static final String SAMPLE_PGN = """
            [Event "Live Chess"]
            [Site "Chess.com"]
            [Date "2026.08.30"]
            [Round "?"]
            [White "S-Bondrea"]
            [Black "Rookin-Good"]
            [Result "1-0"]
            [TimeControl "900+10"]
            [WhiteElo "1430"]
            [BlackElo "1400"]
            [Termination "S-Bondrea won by checkmate"]
            [ECO "B21"]
            [EndTime "16:37:47 GMT+0000"]
            [Link "https://www.chess.com/game/live/173753573144"]

            1. e4 c5 2. d4 cxd4 3. c3 e6 4. cxd4 d5 5. e5 Nc6 6. Bb5 Qb6 7. Nc3 a6 8. Qa4
            Bd7 9. Nf3 Nge7 10. O-O Rc8 11. Bxc6 Rxc6 12. Qc2 Nf5 13. Be3 Nxe3 14. fxe3 Be7
            15. e4 dxe4 16. Qxe4 Qxb2 17. d5 Rxc3 18. d6 Bc6 19. Qd4 Bd5 20. Rab1 Qa3 21.
            Rfd1 Rc4 22. Qa7 Bxd6 23. Qa8+ Kd7 24. Qxh8 Bc5+ 25. Kh1 Qxa2 26. Rxb7+ Kc6 27.
            Qc8# 1-0
            """;

    @Test
    void parsesBasicMetadata() {
        ParsedGame parsed = PgnParser.parse(SAMPLE_PGN);

        System.out.println(parsed);

        assertThat(parsed.result()).isEqualTo("1-0");
        assertThat(parsed.whiteName()).isEqualTo("S-Bondrea");
        assertThat(parsed.blackName()).isEqualTo("Rookin-Good");
        assertThat(parsed.whiteElo()).isEqualTo(1430);
        assertThat(parsed.blackElo()).isEqualTo(1400);
        assertThat(parsed.playedAt()).isEqualTo(OffsetDateTime.parse("2026-08-30T16:37:47Z"));
    }

    @Test
    void handlesMissingTags() {
        String minimal = """
                [Result "1-0"]

                1. e4 e5 1-0
                """;

        ParsedGame parsed = PgnParser.parse(minimal);
        System.out.println(parsed);
        assertThat(parsed.whiteName()).isNull();
        assertThat(parsed.whiteElo()).isNull(); 
        assertThat(parsed.playedAt()).isNull();
    }

    @Test
    void usesMidnightWhenTimeIsMissing() {
        String pgn = """
                [Event "Test"]
                [Date "2026.03.14"]
                [Result "1-0"]

                1. e4 e5 1-0
                """;

        ParsedGame parsed = PgnParser.parse(pgn);

        assertThat(parsed.playedAt()).isEqualTo(OffsetDateTime.parse("2026-03-14T00:00:00Z"));
    }
}
