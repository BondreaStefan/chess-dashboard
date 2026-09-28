package com.bond.chess_dashboard.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.game.dto.StudentStatsResponse;

@RestController 
@RequestMapping("/api/v1/stats")
public class GameStatsController {
    
    private final GameStatsService gameStatsService;

    public GameStatsController(GameStatsService gameStatsService) {
        this.gameStatsService = gameStatsService;
    }

    @GetMapping
    public ResponseEntity<StudentStatsResponse> getStats(
        @RequestParam Long studentId,  
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        return new ResponseEntity<>(gameStatsService.getStatsByStudentId(studentId, coach), HttpStatus.OK);
    }
}
