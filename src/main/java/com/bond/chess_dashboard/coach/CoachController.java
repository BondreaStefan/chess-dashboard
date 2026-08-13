package com.bond.chess_dashboard.coach;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.bond.chess_dashboard.coach.dto.CreateCoachRequest;
import com.bond.chess_dashboard.coach.dto.CoachResponse;
import org.springframework.http.HttpStatus;
import java.util.List;


@RestController
@RequestMapping("/api/v1/coaches")
public class CoachController {
    
    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    @PostMapping
    public ResponseEntity<CoachResponse> createCoach(@Valid @RequestBody CreateCoachRequest request){
        CoachResponse coachResponse = coachService.createCoach(request);
        return new ResponseEntity<>(coachResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CoachResponse>> listCoaches(){
        List<CoachResponse> coaches = coachService.getAllCoaches();
        return new ResponseEntity<>(coaches, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachResponse> getCoach(@PathVariable Long id){
        CoachResponse coachResponse = coachService.getCoachById(id);
        return new ResponseEntity<>(coachResponse, HttpStatus.OK);
    }

}
