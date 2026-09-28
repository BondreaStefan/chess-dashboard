package com.bond.chess_dashboard.student;

import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;


class StudentMapper {
    
    private StudentMapper() {

    }
    
    static Student toEntity(CreateStudentRequest request, Long coachId) {
        Student student = new Student(
            request.firstName(),
            request.lastName(),
            request.email(),
            coachId
        );
        student.setLichessUsername(request.lichessUsername());
        student.setChessComUsername(request.chessComUsername());
        return student;
    }

    static StudentResponse toResponse(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getFirstName(),
            student.getLastName(),
            student.getEmail(),
            student.getCoachId(),
            student.getLichessUsername(),
            student.getChessComUsername(),
            student.getCreatedAt(),
            student.getUpdatedAt()
        );
    }
}
