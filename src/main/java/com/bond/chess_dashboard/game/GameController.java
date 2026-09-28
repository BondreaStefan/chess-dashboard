package com.bond.chess_dashboard.game;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.GameSummaryResponse;
import com.bond.chess_dashboard.game.dto.ImportGamesRequest;
import com.bond.chess_dashboard.game.dto.ImportGamesResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
    
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameDetailResponse> createGame(
        @Valid @RequestBody CreateGameRequest request,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        GameDetailResponse gameDetailResponse = gameService.createGame(request, coach);
        return new ResponseEntity<>(gameDetailResponse, HttpStatus.CREATED);
    }

    @PostMapping("/import")
    public ResponseEntity<ImportGamesResponse> importGames(
        @Valid @RequestBody ImportGamesRequest request,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        return new ResponseEntity<>(gameService.importGames(request, coach), HttpStatus.OK);
    }

    @PostMapping(value = "/import/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportGamesResponse> importGamesFromFile(
        @RequestParam Long studentId,
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal AuthenticatedCoach coach) {

        if(file.isEmpty()) {
            throw new InvalidPgnException("Uploaded file is empty");
        }

        String pgn;
        try {
            pgn = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InvalidPgnException("Could not read uploaded file", e);
        }

        ImportGamesResponse response = gameService.importGames(new ImportGamesRequest(studentId, pgn), coach);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameDetailResponse> getGame(@PathVariable Long id,  @AuthenticationPrincipal AuthenticatedCoach coach) {
        GameDetailResponse gameDetailResponse = gameService.getGameById(id, coach);
        return new ResponseEntity<>(gameDetailResponse, HttpStatus.OK);
    }

    @GetMapping(params = "studentId")
    public ResponseEntity<Page<GameSummaryResponse>> getGamesByStudentId(
        @RequestParam Long studentId,
        @PageableDefault(sort = "playedAt", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        return new ResponseEntity<>(gameService.getGamesByStudentId(studentId, pageable, coach), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id,  @AuthenticationPrincipal AuthenticatedCoach coach) {
        gameService.deleteGame(id, coach);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
