package com.bond.chess_dashboard.game;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.bond.chess_dashboard.common.exception.InvalidPgnException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.game.dto.CreateGameRequest;
import com.bond.chess_dashboard.game.dto.GameDetailResponse;
import com.bond.chess_dashboard.game.dto.GameSummaryResponse;
import com.bond.chess_dashboard.game.dto.ImportGamesRequest;
import com.bond.chess_dashboard.game.dto.ImportGamesResponse;
import com.bond.chess_dashboard.student.StudentService;
import com.bond.chess_dashboard.student.dto.StudentResponse;

import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {
    
    private final GameRepository gameRepository;
    private final StudentService studentService;

    public GameService(GameRepository gameRepository, StudentService studentService) {
        this.gameRepository = gameRepository;
        this.studentService = studentService;
    }

    @Transactional
    public GameDetailResponse createGame(CreateGameRequest request) {
       
        StudentResponse student = studentService.getStudentById(request.studentId());

        ParsedGame parsed = PgnParser.parse(request.pgn());

        Color color = matchColor(parsed, student);

        if(color == null && request.studentColor() == null) {
            throw new InvalidPgnException("Cannot determine student's color; specify studentColor explicitly");
        }

        color = request.studentColor() != null ? request.studentColor() : color;

        GameResult result = GameResult.from(parsed.result(), color);

        Game game = new Game(request.studentId(), GameSource.MANUAL, parsed.pgn(), color, result);
        game.applyMetadata(parsed);

        return GameMapper.toDetailResponse(gameRepository.save(game));
    }

    @Transactional
    public ImportGamesResponse importGames(ImportGamesRequest request) {
        StudentResponse student = studentService.getStudentById(request.studentId());

        List<ParsedGame> parsedGames = PgnParser.parseAll(request.pgn());
        List<Game> toSave = new ArrayList<>();
        int skippedCount = 0;

        for (ParsedGame parsed : parsedGames) {
            Color color = matchColor(parsed, student);
            if (color == null) {
                skippedCount++;
                continue;
            }

            GameResult result = GameResult.from(parsed.result(), color);
            Game game = new Game(request.studentId(), GameSource.MANUAL, parsed.pgn(), color, result);
            game.applyMetadata(parsed);
            toSave.add(game);
        }

        gameRepository.saveAll(toSave);
        return new ImportGamesResponse(toSave.size(), skippedCount);
    }

    @Transactional(readOnly = true)
    public GameDetailResponse getGameById(Long id) {
        return GameMapper.toDetailResponse(findGameById(id));
    }

    @Transactional
    public void deleteGame(Long id) {
        Game game = findGameById(id);
        gameRepository.delete(game);
    }

    @Transactional(readOnly = true)
    public Page<GameSummaryResponse> getGamesByStudentId(Long studentId, Pageable pageable) {
        if (!studentService.studentExists(studentId)) {
            throw new ResourceNotFoundException("Student", studentId);
        }
        Page<Game> games = gameRepository.findByStudentId(studentId, pageable);
        return games.map(GameMapper::toSummaryResponse);
    }

    private Game findGameById(Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game", id));
    }

    private Color matchColor(ParsedGame parsed, StudentResponse student) {
        if (matches(parsed.whiteName(), student.lichessUsername()) || matches(parsed.whiteName(), student.chessComUsername())) {
            return Color.WHITE;
        }
        if (matches(parsed.blackName(), student.lichessUsername()) || matches(parsed.blackName(), student.chessComUsername())) {
            return Color.BLACK;
        }
        return null;
    }

    private static boolean matches(String pgnName, String username) {
        return pgnName != null && username != null && pgnName.equalsIgnoreCase(username);
    }
}
