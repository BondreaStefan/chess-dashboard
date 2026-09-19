package com.bond.chess_dashboard.coach;

import org.springframework.stereotype.Service;
import com.bond.chess_dashboard.coach.dto.UpdateCoachRequest;
import org.springframework.transaction.annotation.Transactional;

import com.bond.chess_dashboard.coach.dto.CoachCredentials;
import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.common.exception.DuplicateResourceException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class CoachService {
    
    private final CoachRepository coachRepository;

    public CoachService(CoachRepository coachRepository) {
        this.coachRepository = coachRepository;
    }

    private Coach findCoachById(Long id) {
        return coachRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coach", id));
    }

    public boolean coachExists(Long id) {
        return coachRepository.existsById(id);
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

    @Transactional
    public CoachResponse register(String firstName, String lastName, String email, String passwordHash) {
        if (coachRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Coach", "email", email);
        }
        Coach coach = new Coach(firstName, lastName, email, passwordHash);
        return CoachMapper.toResponse(coachRepository.save(coach));
    }

    @Transactional(readOnly = true)
    public Optional<CoachCredentials> findCredentialsByEmail(String email) {
        return coachRepository.findByEmail(email)
                .map(c -> new CoachCredentials(c.getId(), c.getEmail(), c.getPasswordHash(), c.getRole(), c.isEnabled()));
    }
}
