package com.bond.chess_dashboard.student;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.student.dto.AssignCoachRequest;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;
import com.bond.chess_dashboard.student.dto.UpdateStudentRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody CreateStudentRequest request, 
            @AuthenticationPrincipal AuthenticatedCoach coach) {
        StudentResponse studentResponse = studentService.createStudent(request, coach);
        return new ResponseEntity<>(studentResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> listStudents(@AuthenticationPrincipal AuthenticatedCoach coach) {
        List<StudentResponse> students = studentService.getStudents(coach);
        return new ResponseEntity<>(students, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudent(
        @PathVariable Long id,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        StudentResponse studentResponse = studentService.getStudentById(id, coach);
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(
        @PathVariable Long id,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        studentService.deleteStudent(id, coach);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
        @PathVariable Long id, 
        @Valid @RequestBody UpdateStudentRequest request,
        @AuthenticationPrincipal AuthenticatedCoach coach) {
        StudentResponse studentResponse = studentService.updateStudent(id, request, coach);
        return new ResponseEntity<>(studentResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}/coach")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StudentResponse> assignCoach(@PathVariable Long id, @Valid @RequestBody AssignCoachRequest request) {
        return new ResponseEntity<>(studentService.assignCoach(id, request.coachId()), HttpStatus.OK);
    }

}
