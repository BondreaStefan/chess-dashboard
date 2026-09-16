package com.bond.chess_dashboard.coach;

import com.bond.chess_dashboard.coach.dto.CoachResponse;

class CoachMapper {

    private CoachMapper() {

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
