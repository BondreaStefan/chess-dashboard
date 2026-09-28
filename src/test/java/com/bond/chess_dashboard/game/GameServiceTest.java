package com.bond.chess_dashboard.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.ImportGamesRequest;
import com.bond.chess_dashboard.game.dto.ImportGamesResponse;
import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    private static final AuthenticatedCoach COACH = new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    private static final String LICHESS_PGN = """
        [Event "rated blitz game"]
        [Site "https://lichess.org/z5wCkUFp"]
        [Date "2026.08.31"]
        [White "VipStef"]
        [Black "MsAs"]
        [Result "1-0"]

        1. e4 e5 2. Nf3 Nc6 3. Bb5 Bc5 1-0
        """;
    
    @Mock
    private GameRepository gameRepository;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private GameService gameService;

    @Test
    void deducesColorFromChessComUsername() {
        String pgn = """
            [Event "Live Chess"]
            [Site "Chess.com"]
            [Date "2026.08.30"]
            [White "S-Bondrea"]
            [Black "Rookin-Good"]
            [Result "1-0"]

            1. e4 c5 2. d4 cxd4 1-0
            """;
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "S-Bondrea", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, pgn, null);

        when(studentService.getStudentById(1L, COACH)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request, COACH);

        assertThat(response.studentColor()).isEqualTo(Color.WHITE);
        assertThat(response.studentResult()).isEqualTo(GameResult.WIN);
        assertThat(response.opponentName()).isEqualTo("Rookin-Good");
    }

    @Test
    void deducesColorFromLichessUsername() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "MsAs", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, null);

        when(studentService.getStudentById(1L, COACH)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request, COACH);

        assertThat(response.studentColor()).isEqualTo(Color.BLACK);
        assertThat(response.studentResult()).isEqualTo(GameResult.LOSS);
        assertThat(response.opponentName()).isEqualTo("VipStef");
    }

    @Test
    void usesRequestedColorWhenProvided() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "MsAs", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, Color.WHITE);

        when(studentService.getStudentById(1L, COACH)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request, COACH);

        assertThat(response.studentColor()).isEqualTo(Color.WHITE);
        assertThat(response.studentResult()).isEqualTo(GameResult.WIN);
        assertThat(response.opponentName()).isEqualTo("MsAs");
    }

    @Test
    void throwsWhenStudentNotInGame() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "differentUsername", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, null);

        when(studentService.getStudentById(1L, COACH)).thenReturn(student);

        assertThatThrownBy(() -> gameService.createGame(request, COACH))
                .isInstanceOf(InvalidPgnException.class)
                .hasMessageContaining("Cannot determine student's color");
        
        verify(gameRepository, never()).save(any());
    }

    @Test
    void throwsWhenStudentDoesNotExist() {
        CreateGameRequest request = new CreateGameRequest(1L, "orice", null);

        when(studentService.getStudentById(1L, COACH))
            .thenThrow(new ResourceNotFoundException("Student", 1L));

        assertThatThrownBy(() -> gameService.createGame(request, COACH))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(gameRepository, never()).save(any());
    }

    @Test
    void throwsWhenListingGamesForNonExistentStudent() {
        when(studentService.getStudentById(999L, COACH))
                .thenThrow(new ResourceNotFoundException("Student", 999L));

        assertThatThrownBy(() -> gameService.getGamesByStudentId(999L, Pageable.unpaged(), COACH))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(gameRepository, never()).findByStudentId(any(), any());
    }

    @Test
    void importsOnlyGamesWhereStudentPlayed() {
        String pgn = """
            [Event "One"]
            [White "S-Bondrea"]
            [Black "Opponent"]
            [Result "1-0"]

            1. e4 e5 1-0

            [Event "Two"]
            [White "PlayerA"]
            [Black "PlayerB"]
            [Result "0-1"]

            1. d4 d5 0-1

            [Event "Three"]
            [White "Opponent"]
            [Black "S-Bondrea"]
            [Result "1-0"]

            1. c4 c5 1-0
            """;

        StudentResponse student = new StudentResponse(
            1L, "Andrei", "Ionescu", "andrei@example.com",
            null, null, "S-Bondrea", null, null);

        ImportGamesRequest request = new ImportGamesRequest(1L, pgn);

        when(studentService.getStudentById(1L, COACH)).thenReturn(student);

        ImportGamesResponse response = gameService.importGames(request, COACH);

        assertThat(response.imported()).isEqualTo(2);
        assertThat(response.skipped()).isEqualTo(1);

        ArgumentCaptor<List<Game>> captor = ArgumentCaptor.captor();
        verify(gameRepository).saveAll(captor.capture());

        List<Game> saved = captor.getValue();
        assertThat(saved).extracting(Game::getStudentColor)
            .containsExactly(Color.WHITE, Color.BLACK);
        assertThat(saved).extracting(Game::getStudentResult)
            .containsExactly(GameResult.WIN, GameResult.LOSS);
    }

    @Test
    void doesNotReturnGameOfAnotherCoachStudent() {
        Game game = new Game(7L, GameSource.MANUAL, "pgn", Color.WHITE, GameResult.WIN);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(game));
        when(studentService.getStudentById(7L, COACH))
                .thenThrow(new ResourceNotFoundException("Student", 7L));

        assertThatThrownBy(() -> gameService.getGameById(5L, COACH))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}