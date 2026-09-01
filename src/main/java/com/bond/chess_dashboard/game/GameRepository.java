package com.bond.chess_dashboard.game;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

interface GameRepository extends JpaRepository<Game, Long> {

    boolean existsBySourceAndExternalId(GameSource source, String externalId);

    Page<Game> findByStudentId(Long studentId, Pageable pageable);
    
}
