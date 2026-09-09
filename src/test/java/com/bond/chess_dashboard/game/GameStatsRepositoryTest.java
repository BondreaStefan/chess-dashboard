package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;

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

    private static final Long STUDENT = 1L;
    private static final Long OTHER_STUDENT = 2L;

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


}
