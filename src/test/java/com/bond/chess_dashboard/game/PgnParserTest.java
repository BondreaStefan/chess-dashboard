package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

import com.bond.chess_dashboard.common.exception.InvalidPgnException;

class PgnParserTest {

    private static final String SAMPLE_CHESS_COM_PGN = """
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

    private static final String SAMPLE_LICHESS_PGN = """
            [Event "rated blitz game"]
            [Site "https://lichess.org/z5wCkUFp"]
            [Date "2026.08.31"]
            [Round "-"]
            [White "VipStef"]
            [Black "MsAs"]
            [Result "1-0"]
            [GameId "z5wCkUFp"]
            [UTCDate "2026.08.31"]
            [UTCTime "11:37:23"]
            [WhiteElo "1267"]
            [BlackElo "1269"]
            [WhiteRatingDiff "+74"]
            [BlackRatingDiff "-5"]
            [Variant "Standard"]
            [TimeControl "300+0"]
            [ECO "C64"]
            [Opening "Ruy Lopez: Classical Variation, Central Variation"]
            [Termination "Time forfeit"]
            [Annotator "lichess.org"]

            1. e4 e5 2. Nf3 Nc6 3. Bb5 Bc5 4. c3 { C64 Ruy Lopez: Classical Variation, Central Variation } 
            Qf6 5. O-O Nge7 6. Re1 O-O 7. d4 d6 8. dxc5 Bg4 9. Bg5 Qg6 10. Bxe7 Nxe7 11. cxd6 cxd6 
            12. Nbd2 a6 13. Be2 f5 14. h3 Bxh3 15. exf5 Nxf5 16. g3 Ne3 17. Nh4 Qg5 18. Ndf3 Rxf3 
            19. Nxf3 Qg6 20. Qb3+ Kh8 21. Qxb7 Rf8 22. Nh4 Qg5 23. Qh1 Rxf2 24. Kxf2 Nf5 25. Qxh3 Qe3+ 
            26. Kg2 { White wins on time. } 1-0

            """;
    @Test
    void parsesChessComMetadata() {
        ParsedGame parsed = PgnParser.parse(SAMPLE_CHESS_COM_PGN);

        assertThat(parsed.result()).isEqualTo("1-0");
        assertThat(parsed.whiteName()).isEqualTo("S-Bondrea");
        assertThat(parsed.blackName()).isEqualTo("Rookin-Good");
        assertThat(parsed.whiteElo()).isEqualTo(1430);
        assertThat(parsed.blackElo()).isEqualTo(1400);
        assertThat(parsed.playedAt()).isEqualTo(OffsetDateTime.parse("2026-08-30T16:37:47Z"));
        assertThat(parsed.ecoCode()).isEqualTo("B21");
        assertThat(parsed.timeControl()).isEqualTo("900+10");
        assertThat(parsed.moveCount()).isEqualTo(27);
    }

    @Test
    void parsesLichessMetadata() {
        ParsedGame parsed = PgnParser.parse(SAMPLE_LICHESS_PGN);

        assertThat(parsed.result()).isEqualTo("1-0");
        assertThat(parsed.whiteName()).isEqualTo("VipStef");
        assertThat(parsed.blackName()).isEqualTo("MsAs");
        assertThat(parsed.whiteElo()).isEqualTo(1267);
        assertThat(parsed.blackElo()).isEqualTo(1269);
        assertThat(parsed.playedAt()).isEqualTo(OffsetDateTime.parse("2026-08-31T11:37:23Z"));
        assertThat(parsed.ecoCode()).isEqualTo("C64");
        assertThat(parsed.timeControl()).isEqualTo("300+0");
        assertThat(parsed.moveCount()).isEqualTo(26);
    }

    @Test
    void handlesMissingTags() {
        String minimal = """
                [Result "1-0"]

                1. e4 e5 1-0
                """;

        ParsedGame parsed = PgnParser.parse(minimal);

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

    @Test
    void rejectsTextWithoutMoves() {
        assertThatThrownBy(() -> PgnParser.parse("acesta nu este un PGN"))
                .isInstanceOf(InvalidPgnException.class)
                .hasMessageContaining("doesn't contain any moves");
    }

    @Test
    void rejectsIllegalMoves() {
        String pgn = """
                [Result "1-0"]

                1. e4 e5 2. Qh9 1-0
                """;

        assertThatThrownBy(() -> PgnParser.parse(pgn))
            .isInstanceOf(InvalidPgnException.class);
    }
}
