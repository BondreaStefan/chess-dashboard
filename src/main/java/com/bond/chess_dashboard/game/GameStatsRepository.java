package com.bond.chess_dashboard.game;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface GameStatsRepository extends JpaRepository<Game, Long> {

    @Query(value = """
            SELECT
              COUNT(*) AS total,
              COUNT(*) FILTER (WHERE student_result = 'WIN')  AS wins,
              COUNT(*) FILTER (WHERE student_result = 'LOSS') AS losses,
              COUNT(*) FILTER (WHERE student_result = 'DRAW') AS draws,
              COUNT(*) FILTER (WHERE student_color = 'WHITE') AS games_as_white,
              COUNT(*) FILTER (WHERE student_color = 'WHITE' AND student_result = 'WIN') AS wins_as_white,
              COUNT(*) FILTER (WHERE student_color = 'BLACK') AS games_as_black,
              COUNT(*) FILTER (WHERE student_color = 'BLACK' AND student_result = 'WIN') AS wins_as_black
            FROM game
            WHERE student_id = :studentId
            """, nativeQuery = true)
            
    StudentStatsProjection findStatsByStudentId(@Param("studentId") Long studentId);
    
}
