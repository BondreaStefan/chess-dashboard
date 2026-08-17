package com.bond.chess_dashboard.coach;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.coach.dto.CreateCoachRequest;
import com.bond.chess_dashboard.coach.dto.UpdateCoachRequest;
import com.bond.chess_dashboard.common.exception.DuplicateResourceException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;



@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachService coachService;
    
    @Test
    void throwsWhenEmailAlreadyExists() {
        CreateCoachRequest request = new CreateCoachRequest("Ion", "Popescu", "ion@example.com");
        when(coachRepository.existsByEmail("ion@example.com")).thenReturn(true);

        assertThatThrownBy(() -> coachService.createCoach(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ion@example.com");

        verify(coachRepository, never()).save(any());
    }

    @Test
    void throwsWhenUpdatingNonExistentCoach(){
        UpdateCoachRequest request = new UpdateCoachRequest("Ion", "Popescu");
        when(coachRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> coachService.updateCoach(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createsCoachWhenEmailIsAvailable(){
        CreateCoachRequest request = new CreateCoachRequest("Ion", "Popescu", "ion@example.com");
        when(coachRepository.save(any(Coach.class))).thenAnswer(inv -> inv.getArgument(0));
        when(coachRepository.existsByEmail("ion@example.com")).thenReturn(false);

        CoachResponse response = coachService.createCoach(request);
        assertThat(response.firstName()).isEqualTo("Ion");
        assertThat(response.lastName()).isEqualTo("Popescu");
        assertThat(response.email()).isEqualTo("ion@example.com");  
    }
}
