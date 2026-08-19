package com.bond.chess_dashboard.student;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface StudentRepository extends JpaRepository<Student, Long> {
    
    boolean existsByEmail(String email);

    List<Student> findByCoachId(Long coachId);
    
}
