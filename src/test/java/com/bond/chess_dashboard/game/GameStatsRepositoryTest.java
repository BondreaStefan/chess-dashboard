package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.bond.chess_dashboard.TestcontainersConfiguration;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class GameStatsRepositoryTest {

    @Autowired
    private GameStatsRepository gameStatsRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Game game(Long studentId, Color color, GameResult result) {
        return new Game(studentId, GameSource.MANUAL, "[Result \"1-0\"]", color, result);
    }

    private Long insertStudent(String email) {
        return ((Number) entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO student (first_name, last_name, email)
                        VALUES ('Test', 'Student', :email)
                        RETURNING id
                        """)
                .setParameter("email", email)
                .getSingleResult()).longValue();
    }

    private Game gameAt(Long studentId, OffsetDateTime playedAt, Platform platform, TimeClass timeClass) {
        Game game = new Game(studentId, GameSource.MANUAL, "pgn", Color.WHITE, GameResult.WIN);
        game.applyMetadata(new ParsedGame(
                "1-0", "White", "Black", 1500, 1500, playedAt,
                null, "180+0", 20, "pgn", platform, timeClass));
        return game;
    }

    @Test
    void countsResultsByColor() {
        Long student = insertStudent("a@example.com");
        Long other = insertStudent("b@example.com");

        gameRepository.save(game(student, Color.WHITE, GameResult.WIN));
        gameRepository.save(game(student, Color.WHITE, GameResult.LOSS));
        gameRepository.save(game(student, Color.BLACK, GameResult.DRAW));
        gameRepository.save(game(student, Color.BLACK, GameResult.WIN));
        gameRepository.save(game(other, Color.WHITE, GameResult.WIN));

        gameRepository.flush();

        StudentStatsProjection stats = gameStatsRepository.findStatsByStudentId(student);

        assertThat(stats.getTotal()).isEqualTo(4);
        assertThat(stats.getWins()).isEqualTo(2);
        assertThat(stats.getLosses()).isEqualTo(1);
        assertThat(stats.getDraws()).isEqualTo(1);
        assertThat(stats.getGamesAsWhite()).isEqualTo(2);
        assertThat(stats.getWinsAsWhite()).isEqualTo(1);
        assertThat(stats.getGamesAsBlack()).isEqualTo(2);
        assertThat(stats.getWinsAsBlack()).isEqualTo(1);
    }

    @Test
    void returnsZerosForStudentWithoutGames() {
        StudentStatsProjection stats = gameStatsRepository.findStatsByStudentId(999L);

        assertThat(stats.getTotal()).isZero();
        assertThat(stats.getWins()).isZero();
    }

    @Test
    void countsRecentGamesPerStudent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-11-07T12:00:00Z");
        Long active = insertStudent("a@example.com");
        Long withoutGames = insertStudent("b@example.com");

        gameRepository.saveAll(List.of(
                gameAt(active, now.minusDays(2), Platform.CHESS_COM, TimeClass.BLITZ), // last 7 & 14 days
                gameAt(active, now.minusDays(10), Platform.CHESS_COM, TimeClass.BLITZ), // last 14 days
                gameAt(active, now.minusDays(30), Platform.CHESS_COM, TimeClass.BLITZ) // outside the window
        ));
        gameRepository.flush();

        List<StudentActivityProjection> result = gameStatsRepository.findActivity(
                List.of(active, withoutGames), now.minusDays(14), now.minusDays(7));

        assertThat(result).hasSize(1);
        StudentActivityProjection activity = result.getFirst();
        assertThat(activity.getStudentId()).isEqualTo(active);
        assertThat(activity.getGamesLast14Days()).isEqualTo(2);
        assertThat(activity.getGamesLast7Days()).isEqualTo(1);
        assertThat(activity.getLastPlayedAt()).isEqualTo(now.minusDays(2).toInstant());
    }

    @Test
    void groupsGamesByPlatformAndTimeClass() {
        OffsetDateTime now = OffsetDateTime.parse("2026-11-07T12:00:00Z");
        Long student = insertStudent("c@example.com");
        Long otherStudent = insertStudent("d@example.com");

        gameRepository.saveAll(List.of(
                gameAt(student, now.minusDays(2), Platform.CHESS_COM, TimeClass.BLITZ),
                gameAt(student, now.minusDays(4), Platform.CHESS_COM, TimeClass.BLITZ),
                gameAt(student, now.minusDays(5), Platform.CHESS_COM, TimeClass.BLITZ),
                gameAt(student, now.minusDays(3), Platform.CHESS_COM, TimeClass.RAPID),
                gameAt(student, now.minusDays(60), Platform.LICHESS, TimeClass.BULLET),
                gameAt(student, now.minusDays(1), Platform.OTHER, TimeClass.UNKNOWN),
                gameAt(otherStudent, now.minusDays(1), Platform.LICHESS, TimeClass.BLITZ)));
        gameRepository.flush();

        List<StudentCombinationProjection> result = gameStatsRepository.findCombinations(List.of(student),
                now.minusDays(14));

        assertThat(result).hasSize(3);

        assertThat(result)
                .filteredOn(c -> c.getTimeClass() == TimeClass.BLITZ)
                .singleElement()
                .satisfies(c -> {
                    assertThat(c.getStudentId()).isEqualTo(student);
                    assertThat(c.getPlatform()).isEqualTo(Platform.CHESS_COM);
                    assertThat(c.getRecentGames()).isEqualTo(3);
                    assertThat(c.getLastPlayedAt()).isEqualTo(now.minusDays(2).toInstant());
                });

        assertThat(result)
                .filteredOn(c -> c.getTimeClass() == TimeClass.RAPID)
                .singleElement()
                .satisfies(c -> {
                    assertThat(c.getPlatform()).isEqualTo(Platform.CHESS_COM);
                    assertThat(c.getRecentGames()).isEqualTo(1);
                });

        assertThat(result)
                .filteredOn(c -> c.getTimeClass() == TimeClass.BULLET)
                .singleElement()
                .satisfies(c -> {
                    assertThat(c.getPlatform()).isEqualTo(Platform.LICHESS);
                    assertThat(c.getRecentGames()).isZero();
                    assertThat(c.getLastPlayedAt()).isEqualTo(now.minusDays(60).toInstant());
                });
    }
}
