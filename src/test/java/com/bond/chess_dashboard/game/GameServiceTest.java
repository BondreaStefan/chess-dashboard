package com.bond.chess_dashboard.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.student.dto.StudentResponse;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

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

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request);

        assertThat(response.studentColor()).isEqualTo(Color.WHITE);
        assertThat(response.studentResult()).isEqualTo(GameResult.WIN);
        assertThat(response.opponentName()).isEqualTo("Rookin-Good");
    }

    @Test
    void deducesColorFromLichessUsername() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "MsAs", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, null);

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request);

        assertThat(response.studentColor()).isEqualTo(Color.BLACK);
        assertThat(response.studentResult()).isEqualTo(GameResult.LOSS);
        assertThat(response.opponentName()).isEqualTo("VipStef");
    }

    @Test
    void usesRequestedColorWhenProvided() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "MsAs", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, Color.WHITE);

        when(studentService.getStudentById(1L)).thenReturn(student);
        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));

        GameDetailResponse response = gameService.createGame(request);

        assertThat(response.studentColor()).isEqualTo(Color.WHITE);
        assertThat(response.studentResult()).isEqualTo(GameResult.WIN);
        assertThat(response.opponentName()).isEqualTo("MsAs");
    }

    @Test
    void throwsWhenStudentNotInGame() {
        StudentResponse student = new StudentResponse(1L, "Andrei", "Ionescu",
        "andrei@example.com" , null, null, "differentUsername", null, null);

        CreateGameRequest request = new CreateGameRequest(1L, LICHESS_PGN, null);

        when(studentService.getStudentById(1L)).thenReturn(student);

        assertThatThrownBy(() -> gameService.createGame(request))
                .isInstanceOf(InvalidPgnException.class)
                .hasMessageContaining("Cannot determine student's color");
        
        verify(gameRepository, never()).save(any());
    }

    @Test
    void throwsWhenStudentDoesNotExist() {
        CreateGameRequest request = new CreateGameRequest(1L, "orice", null);

        when(studentService.getStudentById(1L))
            .thenThrow(new ResourceNotFoundException("Student", 1L));

        assertThatThrownBy(() -> gameService.createGame(request))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(gameRepository, never()).save(any());
    }

    @Test
    void throwsWhenListingGamesForNonExistentStudent() {
        when(studentService.studentExists(999L)).thenReturn(false);

        assertThatThrownBy(() -> gameService.getGamesByStudentId(999L, Pageable.unpaged()))
            .isInstanceOf(ResourceNotFoundException.class);

        verify(gameRepository, never()).findByStudentId(any(), any());
    }
}