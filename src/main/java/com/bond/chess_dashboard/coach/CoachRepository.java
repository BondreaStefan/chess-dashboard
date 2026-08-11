package com.bond.chess_dashboard.coach;

import org.springframework.data.jpa.repository.JpaRepository;

interface CoachRepository extends JpaRepository<Coach, Long> {
    
    boolean existsByEmail(String email);
    
}
