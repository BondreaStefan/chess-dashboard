package com.bond.chess_dashboard.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.GameSummaryResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDetailResponse> createGame(@Valid @RequestBody CreateGameRequest request) {
        GameDetailResponse gameDetailResponse = gameService.createGame(request);
        return new ResponseEntity<>(gameDetailResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailResponse> getGame(@PathVariable Long id) {
        GameDetailResponse gameDetailResponse = gameService.getGameById(id);
        return new ResponseEntity<>(gameDetailResponse, HttpStatus.OK);
    }

    @GetMapping(params = "studentId")
    public ResponseEntity<Page<GameSummaryResponse>> getGamesByStudentId(
        @RequestParam Long studentId,
        @PageableDefault(sort = "playedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(gameService.getGamesByStudentId(studentId, pageable), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
