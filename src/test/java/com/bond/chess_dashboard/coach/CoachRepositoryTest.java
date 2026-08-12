package com.bond.chess_dashboard.coach;

import com.bond.chess_dashboard.TestcontainersConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)

class CoachRepositoryTest {
    @Autowired
    private CoachRepository coachRepository;

    @Test
    void savesCoachAndGeneratesId() {
        Coach coach = new Coach("Ion", "Popescu", "ion@example.com");

        Coach saved = coachRepository.saveAndFlush(coach);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
