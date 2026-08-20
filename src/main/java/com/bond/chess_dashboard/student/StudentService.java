package com.bond.chess_dashboard.student;

import java.util.List;
import org.springframework.stereotype.Service;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;
import com.bond.chess_dashboard.student.dto.UpdateStudentRequest;
import org.springframework.transaction.annotation.Transactional;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.coach.CoachService;
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
    public StudentResponse createStudent(CreateStudentRequest request) {

        if(studentRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Student", "email", request.email());
        }

        if((request.coachId() != null && !coachService.coachExists(request.coachId()))) {
            throw new ResourceNotFoundException("Coach", request.coachId());
        }

        if(request.lichessUsername() != null && studentRepository.existsByLichessUsername(request.lichessUsername())) {
            throw new DuplicateResourceException("Student", "lichessUsername", request.lichessUsername());
        }
    
        if(request.chessComUsername() != null && studentRepository.existsByChessComUsername(request.chessComUsername())) {
            throw new DuplicateResourceException("Student", "chessComUsername", request.chessComUsername());
        }

        Student saved = studentRepository.save(StudentMapper.toEntity(request));
        return StudentMapper.toResponse(saved);
    }

    private Student findStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        return StudentMapper.toResponse(findStudentById(id));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();
        return students.stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> getStudentsByCoachId(Long coachId) {
        if(!coachService.coachExists(coachId)) {
            throw new ResourceNotFoundException("Coach", coachId);
        }
        List<Student> students = studentRepository.findByCoachId(coachId);
        return students.stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Transactional
    public void deleteStudent(Long id) {
        Student student = findStudentById(id);
        studentRepository.delete(student);
    }

    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = findStudentById(id);

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
    public StudentResponse assignCoach(Long studentId, Long coachId) {
        Student student = findStudentById(studentId);

        if (coachId != null && !coachService.coachExists(coachId)) {
            throw new ResourceNotFoundException("Coach", coachId);
        }

        student.setCoachId(coachId);
        return StudentMapper.toResponse(student);
    }

}
