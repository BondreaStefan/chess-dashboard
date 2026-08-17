package com.bond.chess_dashboard.coach;

import org.springframework.stereotype.Service;
import com.bond.chess_dashboard.coach.dto.CreateCoachRequest;
import com.bond.chess_dashboard.coach.dto.UpdateCoachRequest;
import org.springframework.transaction.annotation.Transactional;
import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.common.exception.DuplicateResourceException;
import java.util.List;

@Service
public class CoachService {
    
    private final CoachRepository coachRepository;

    public CoachService(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    @Transactional
    public CoachResponse createCoach(CreateCoachRequest request) {

        if(coachRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Coach", "email", request.email());
        }
        Coach saved = coachRepository.save(CoachMapper.toEntity(request));
        return CoachMapper.toResponse(saved);
    }

    private Coach findCoachById(Long id) {
        return coachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", id));
    }

    @Transactional(readOnly = true)
    public CoachResponse getCoachById(Long id) {
        return CoachMapper.toResponse(findCoachById(id));
    }

    @Transactional(readOnly = true)
    public List<CoachResponse> getAllCoaches() {
        List<Coach> coaches = coachRepository.findAll();
        return coaches.stream()
                .map(CoachMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteCoach(Long id) {
        Coach coach = findCoachById(id);
        coachRepository.delete(coach);
    }

    @Transactional
    public CoachResponse updateCoach(Long id, UpdateCoachRequest request) {
        Coach coach = findCoachById(id);
        coach.setFirstName(request.firstName());
        coach.setLastName(request.lastName());
        return CoachMapper.toResponse(coach);
    }

}
