package com.bond.chess_dashboard.coach;

import com.bond.chess_dashboard.coach.dto.CreateCoachRequest;
import com.bond.chess_dashboard.coach.dto.CoachResponse;

class CoachMapper {

    private CoachMapper() {

    }
    
    static Coach toEntity(CreateCoachRequest request) {
        return new Coach(
            request.firstName(),
            request.lastName(),
            request.email()
        );
    }

    static CoachResponse toResponse(Coach coach) {
        return new CoachResponse(
            coach.getId(),
            coach.getFirstName(),
            coach.getLastName(),
            coach.getEmail(),
            coach.getCreatedAt(),
            coach.getUpdatedAt()
        );
    }

}
