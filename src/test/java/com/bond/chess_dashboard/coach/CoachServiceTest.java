package com.bond.chess_dashboard.coach;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.coach.dto.UpdateCoachRequest;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import java.util.Optional;



@ExtendWith(MockitoExtension.class)
class CoachServiceTest {

    @Mock
    private CoachRepository coachRepository;

    @InjectMocks
    private CoachService coachService;

    @Test
    void throwsWhenUpdatingNonExistentCoach(){
        UpdateCoachRequest request = new UpdateCoachRequest("Ion", "Popescu");
        when(coachRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> coachService.updateCoach(1L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
