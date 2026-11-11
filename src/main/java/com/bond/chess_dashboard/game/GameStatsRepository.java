package com.bond.chess_dashboard.game;

import java.time.OffsetDateTime;
import java.util.List;

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

  @Query(value = """
      SELECT
        student_id                                    AS "studentId",
        COUNT(*) FILTER (WHERE played_at >= :since14) AS "gamesLast14Days",
        COUNT(*) FILTER (WHERE played_at >= :since7)  AS "gamesLast7Days",
        MAX(played_at)                                AS "lastPlayedAt"
      FROM game
      WHERE student_id IN (:studentIds)
      GROUP BY student_id
      """, nativeQuery = true)
  List<StudentActivityProjection> findActivity(
      @Param("studentIds") List<Long> studentIds,
      @Param("since14") OffsetDateTime since14,
      @Param("since7") OffsetDateTime since7);

  @Query(value = """
      SELECT
        student_id                                    AS "studentId",
        platform                                      AS "platform",
        time_class                                    AS "timeClass",
        COUNT(*) FILTER (WHERE played_at >= :since14) AS "recentGames",
        MAX(played_at)                                AS "lastPlayedAt"
      FROM game
      WHERE student_id IN (:studentIds)
        AND platform <> 'OTHER'
        AND time_class <> 'UNKNOWN'
      GROUP BY student_id, platform, time_class
      """, nativeQuery = true)
  List<StudentCombinationProjection> findCombinations(
      @Param("studentIds") List<Long> studentIds,
      @Param("since14") OffsetDateTime since14);

  @Query(value = """
      SELECT
        played_at AS "playedAt",
        CASE WHEN student_color = 'WHITE' THEN white_elo ELSE black_elo END AS "rating"
      FROM game
      WHERE student_id = :studentId
        AND platform = :platform
        AND time_class = :timeClass
        AND played_at >= :since
        AND CASE WHEN student_color = 'WHITE' THEN white_elo ELSE black_elo END IS NOT NULL
      ORDER BY played_at
      """, nativeQuery = true)
  List<RatingPointProjection> findRatingHistory(
      @Param("studentId") Long studentId,
      @Param("platform") String platform,
      @Param("timeClass") String timeClass,
      @Param("since") OffsetDateTime since);

}
