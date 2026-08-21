package com.bond.chess_dashboard.student;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import com.bond.chess_dashboard.TestcontainersConfiguration;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void setsCoachIdToNullWhenCoachIsDeleted() {
        Long coachId = (Long) entityManager.getEntityManager()
                .createNativeQuery("""
                        INSERT INTO coach (first_name, last_name, email)
                        VALUES ('Ion', 'Popescu', 'ion@example.com')
                        RETURNING id
                        """)
                .getSingleResult();

        Student student = new Student("Andrei", "Ionescu", "andrei@example.com", coachId);
        Student saved = studentRepository.saveAndFlush(student);

        entityManager.getEntityManager()
                .createNativeQuery("DELETE FROM coach WHERE id = :id")
                .setParameter("id", coachId)
                .executeUpdate();

        entityManager.clear();

        Student reloaded = studentRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getCoachId()).isNull();
    }
}
