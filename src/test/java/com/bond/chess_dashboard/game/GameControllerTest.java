package com.bond.chess_dashboard.game;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;


import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.bond.chess_dashboard.auth.JwtService;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.config.SecurityConfig;
import com.bond.chess_dashboard.common.config.WebConfig;
import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.GameSummaryResponse;

@WebMvcTest(GameController.class)
@Import({WebConfig.class, SecurityConfig.class})
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @MockitoBean 
    private JwtService jwtService;

    private static final AuthenticatedCoach COACH = new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Test
    void returns201WhenGameIsCreated() throws Exception {
        String body = """
        {
            "studentId": 1,
            "pgn": "[Result \\"1-0\\"]\\n\\n1. e4 e5 1-0"
        }
        """;

        GameDetailResponse response = new GameDetailResponse(1L, 1L, GameSource.MANUAL,
             null, Color.WHITE, GameResult.WIN, OffsetDateTime.parse("2026-08-30T16:37:47Z"),
              "Rookin-Good", "1-0", "B21", "900+10",
               1430, 1400, 27,
                "[Result \\\"1-0\\\"]",
                 null, null);

        when(gameService.createGame(any(CreateGameRequest.class), eq(COACH))).thenReturn(response);

        mockMvc.perform(post("/api/v1/games")
                        .with(asCoach())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.studentColor").value("WHITE"))
                .andExpect(jsonPath("$.studentResult").value("WIN"));

        verify(gameService).createGame(any(CreateGameRequest.class), eq(COACH));
    }
    
    @Test
    void returns400WhenStudentIdIsMissing() throws Exception {
        String body = """
        {
            "pgn": "[Result \\"1-0\\"]\\n\\n1. e4 e5 1-0"
        }
        """;

        mockMvc.perform(post("/api/v1/games")
                        .with(asCoach())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.studentId").exists());

        verifyNoInteractions(gameService);
    }

    @Test
    void returns400WhenPgnIsInvalid() throws Exception {
        String body = """
        {
            "studentId": 1,
            "pgn": "[Result \\"1-0\\"]\\n\\n1. e4 e5 1-0"
        }
        """;

        when(gameService.createGame(any(CreateGameRequest.class), eq(COACH)))
                .thenThrow(new InvalidPgnException("Failed to parse PGN: Invalid move"));

        mockMvc.perform(post("/api/v1/games")
                        .with(asCoach())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Failed to parse PGN: Invalid move"));

        verify(gameService).createGame(any(CreateGameRequest.class), eq(COACH));
    }

    @Test
    void returns404WhenGameDoesNotExist() throws Exception {
        when(gameService.getGameById(1L, COACH))
            .thenThrow(new ResourceNotFoundException("Game", 1L));

        mockMvc.perform(get("/api/v1/games/1")
                        .with(asCoach()))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsPagedSummariesForStudent() throws Exception {
        GameSummaryResponse summary = new GameSummaryResponse(
            1L, 1L, GameSource.MANUAL,
            Color.WHITE, GameResult.WIN,
            OffsetDateTime.parse("2026-08-30T16:37:47Z"),
            "Rookin-Good", "1-0", "B21", "900+10",
            1430, 1400, 27);

        Page<GameSummaryResponse> page =
            new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1);

        when(gameService.getGamesByStudentId(eq(1L), any(Pageable.class), eq(COACH))).thenReturn(page);

        mockMvc.perform(get("/api/v1/games").param("studentId", "1")
                        .with(asCoach()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].opponentName").value("Rookin-Good"))
                .andExpect(jsonPath("$.content[0].studentResult").value("WIN"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.number").value(0));
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/games").param("studentId", "1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(gameService);
    }

    @Test
    void rejectsEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "games.pgn", "text/plain", new byte[0]);

        mockMvc.perform(multipart("/api/v1/games/import/file")
                        .file(file)
                        .param("studentId", "1")
                        .with(asCoach()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(gameService);
    }

    private static RequestPostProcessor asCoach() {
        return authentication(new UsernamePasswordAuthenticationToken(
                COACH, null, List.of(new SimpleGrantedAuthority("ROLE_COACH"))));
    }
}
