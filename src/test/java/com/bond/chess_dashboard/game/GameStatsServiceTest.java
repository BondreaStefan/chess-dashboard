package com.bond.chess_dashboard.game;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.game.dto.StudentStatsResponse;
import com.bond.chess_dashboard.student.StudentService;

@ExtendWith(MockitoExtension.class)
class GameStatsServiceTest {

    @Mock
    private GameStatsRepository gameStatsRepository;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private GameStatsService gameStatsService;

    @Test
    void throwsWhenStudentDoesNotExist() {
        when(studentService.studentExists(999L)).thenReturn(false);

        assertThatThrownBy(() -> gameStatsService.getStatsByStudentId(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");

        verify(gameStatsRepository, never()).findStatsByStudentId(any());
    }

    @Test
    void computesWinRates() {
        StudentStatsProjection projection = mock(StudentStatsProjection.class);
        when(projection.getTotal()).thenReturn(3L);
        when(projection.getWins()).thenReturn(2L);
        when(projection.getLosses()).thenReturn(1L);
        when(projection.getDraws()).thenReturn(0L);
        when(projection.getGamesAsWhite()).thenReturn(2L);
        when(projection.getWinsAsWhite()).thenReturn(2L);
        when(projection.getGamesAsBlack()).thenReturn(1L);
        when(projection.getWinsAsBlack()).thenReturn(0L);

        when(studentService.studentExists(1L)).thenReturn(true);
        when(gameStatsRepository.findStatsByStudentId(1L)).thenReturn(projection);

        StudentStatsResponse response = gameStatsService.getStatsByStudentId(1L);

        assertThat(response.winRate()).isEqualTo(66.7);
        assertThat(response.winRateAsWhite()).isEqualTo(100.0);
        assertThat(response.winRateAsBlack()).isEqualTo(0.0);
    }

    @Test
    void returnsZeroRatesWhenNoGames() {
        StudentStatsProjection projection = mock(StudentStatsProjection.class);
        when(projection.getTotal()).thenReturn(0L);
        when(projection.getGamesAsWhite()).thenReturn(0L);
        when(projection.getGamesAsBlack()).thenReturn(0L);

        when(studentService.studentExists(1L)).thenReturn(true);
        when(gameStatsRepository.findStatsByStudentId(1L)).thenReturn(projection);

        StudentStatsResponse response = gameStatsService.getStatsByStudentId(1L);

        assertThat(response.winRate()).isZero();
        assertThat(response.winRateAsWhite()).isZero();
        assertThat(response.winRateAsBlack()).isZero();
    }
}
