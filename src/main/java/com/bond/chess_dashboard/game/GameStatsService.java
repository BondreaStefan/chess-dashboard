package com.bond.chess_dashboard.game;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.game.dto.PlatformBreakdown;
import com.bond.chess_dashboard.game.dto.RatingPoint;
import com.bond.chess_dashboard.game.dto.StudentActivity;
import com.bond.chess_dashboard.game.dto.StudentStatsResponse;
import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@Service
public class GameStatsService {

    private final GameStatsRepository gameStatsRepository;
    private final StudentService studentService;

    public GameStatsService(GameStatsRepository gameStatsRepository, StudentService studentService) {
        this.gameStatsRepository = gameStatsRepository;
        this.studentService = studentService;
    }

    @Transactional(readOnly = true)
    public StudentStatsResponse getStatsByStudentId(Long studentId, AuthenticatedCoach coach) {
        StudentResponse student = studentService.getStudentById(studentId, coach);

        StudentStatsProjection projection = gameStatsRepository.findStatsByStudentId(student.id());

        return new StudentStatsResponse(
                projection.getTotal(),
                projection.getWins(),
                projection.getLosses(),
                projection.getDraws(),
                rate(projection.getWins(), projection.getTotal()),
                projection.getGamesAsWhite(),
                projection.getWinsAsWhite(),
                rate(projection.getWinsAsWhite(), projection.getGamesAsWhite()),
                projection.getGamesAsBlack(),
                projection.getWinsAsBlack(),
                rate(projection.getWinsAsBlack(), projection.getGamesAsBlack()));
    }

    /**
     * Returns recent activity for the given students.
     * No ownership check is performed: callers must pass ids obtained from an
     * authorized source, such as {@code StudentService.getStudents(coach)}.
     */

    @Transactional(readOnly = true)
    public List<StudentActivity> getActivity(List<Long> studentIds, Instant since14, Instant since7) {
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return gameStatsRepository
                .findActivity(studentIds, toOffset(since14), toOffset(since7))
                .stream()
                .map(p -> new StudentActivity(
                        p.getStudentId(), p.getGamesLast14Days(), p.getGamesLast7Days(), p.getLastPlayedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PlatformBreakdown> getPlatformBreakdown(List<Long> studentIds, Instant since14) {
        if (studentIds.isEmpty()) {
            return List.of();
        }
        return gameStatsRepository
                .findCombinations(studentIds, toOffset(since14))
                .stream()
                .map(p -> new PlatformBreakdown(
                        p.getStudentId(), p.getPlatform(), p.getTimeClass(), p.getRecentGames(), p.getLastPlayedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RatingPoint> getRatingHistory(Long studentId, Platform platform, TimeClass timeClass, Instant since) {
        return gameStatsRepository
                .findRatingHistory(studentId, platform.name(), timeClass.name(), toOffset(since))
                .stream()
                .map(p -> new RatingPoint(p.getPlayedAt(), p.getRating()))
                .toList();
    }

    private static OffsetDateTime toOffset(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC);
    }

    private static double rate(long wins, long total) {
        return total == 0 ? 0.0 : Math.round((double) wins / total * 1000) / 10.0;
    }
}
