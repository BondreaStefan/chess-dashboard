package com.bond.chess_dashboard.student;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;
import com.bond.chess_dashboard.student.dto.UpdateStudentRequest;
import org.springframework.transaction.annotation.Transactional;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.CoachService;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.exception.DuplicateResourceException;

@Service
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final CoachService coachService;

    public StudentService(StudentRepository studentRepository, CoachService coachService) {
        this.studentRepository = studentRepository;
        this.coachService = coachService;
    }

    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request, AuthenticatedCoach coach) {

        if(studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Student", "email", request.email());
        }

        if(request.lichessUsername() != null && studentRepository.existsByLichessUsername(request.lichessUsername())) {
            throw new DuplicateResourceException("Student", "lichessUsername", request.lichessUsername());
        }
    
        if(request.chessComUsername() != null && studentRepository.existsByChessComUsername(request.chessComUsername())) {
            throw new DuplicateResourceException("Student", "chessComUsername", request.chessComUsername());
        }

        Student saved = studentRepository.save(StudentMapper.toEntity(request, coach.id()));
        return StudentMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id, AuthenticatedCoach coach) {
        return StudentMapper.toResponse(findStudentFor(id, coach));
    }

    private Student findStudentFor(Long id, AuthenticatedCoach coach) {
        Optional<Student> student = coach.role() == Role.ADMIN
                ? studentRepository.findById(id)
                : studentRepository.findByIdAndCoachId(id, coach.id());
        return student.orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudents(AuthenticatedCoach coach) {
        List<Student> students = coach.role() == Role.ADMIN
                ? studentRepository.findAll()
                : studentRepository.findByCoachId(coach.id());
        return students.stream()
                .map(StudentMapper::toResponse)
                .toList();
    }
    
    @Transactional
    public void deleteStudent(Long id, AuthenticatedCoach coach) {
        Student student = findStudentFor(id, coach);
        studentRepository.delete(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request, AuthenticatedCoach coach) {
        Student student = findStudentFor(id, coach);

        if(request.lichessUsername() != null && !request.lichessUsername().equals(student.getLichessUsername()) 
                && studentRepository.existsByLichessUsername(request.lichessUsername())) {
            throw new DuplicateResourceException("Student", "lichessUsername", request.lichessUsername());
        }
    
        if(request.chessComUsername() != null && !request.chessComUsername().equals(student.getChessComUsername()) 
                && studentRepository.existsByChessComUsername(request.chessComUsername())) {
            throw new DuplicateResourceException("Student", "chessComUsername", request.chessComUsername());
        }

        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setLichessUsername(request.lichessUsername());
        student.setChessComUsername(request.chessComUsername());

        return StudentMapper.toResponse(student);
    }

    @Transactional
    public StudentResponse assignCoach(Long studentId, Long targetCoachId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));

        if (targetCoachId != null && !coachService.coachExists(targetCoachId)) {
            throw new ResourceNotFoundException("Coach", targetCoachId);
        }

        student.setCoachId(targetCoachId);
        return StudentMapper.toResponse(student);
    }

}
