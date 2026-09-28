package com.bond.chess_dashboard.game;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
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
            rate(projection.getWinsAsBlack(), projection.getGamesAsBlack())
        );    
    }

    private static double rate(long wins, long total) {
        return total == 0 ? 0.0 : Math.round((double) wins / total * 1000) / 10.0;
    }
}
