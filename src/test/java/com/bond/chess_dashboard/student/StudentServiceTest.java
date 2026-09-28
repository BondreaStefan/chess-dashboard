package com.bond.chess_dashboard.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.auth.dto.AuthenticatedCoach;
import com.bond.chess_dashboard.coach.CoachService;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.common.exception.DuplicateResourceException;
import com.bond.chess_dashboard.common.exception.ResourceNotFoundException;
import com.bond.chess_dashboard.student.dto.CreateStudentRequest;
import com.bond.chess_dashboard.student.dto.StudentResponse;
import com.bond.chess_dashboard.student.dto.UpdateStudentRequest;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {
    
    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CoachService coachService;

    @InjectMocks
    private StudentService studentService;

    private static final AuthenticatedCoach COACH = new AuthenticatedCoach(1L, "coach@example.com", Role.COACH);

    @Test
    void allowsUpdateWhenLichessUsernameIsUnchanged() {
        Student existing = new Student("Andrei", "Ionescu", "andrei@example.com", 1L);
        existing.setLichessUsername("andrei_chess");

        UpdateStudentRequest request = new UpdateStudentRequest(
            "Andrei-Mihai", "Ionescu", "andrei_chess", null);

        when(studentRepository.findByIdAndCoachId(1L, 1L)).thenReturn(Optional.of(existing));

        StudentResponse response = studentService.updateStudent(1L, request, COACH);

        assertThat(response.firstName()).isEqualTo("Andrei-Mihai");
        assertThat(response.lichessUsername()).isEqualTo("andrei_chess");

        verify(studentRepository, never()).existsByLichessUsername(any());
    }

    @Test
    void throwsWhenUpdatingToExistingUsername(){
        Student secondStudent = new Student("Ion", "Popescu", "ion@example.com", 1L);
        secondStudent.setLichessUsername("ion_chess");
        
        UpdateStudentRequest request = new UpdateStudentRequest("Ion", "Popescu",
        "andrei_chess", null);

        when(studentRepository.findByIdAndCoachId(2L, 1L)).thenReturn(Optional.of(secondStudent));
        when(studentRepository.existsByLichessUsername("andrei_chess")).thenReturn(true);

        assertThatThrownBy(() -> studentService.updateStudent(2L, request, COACH))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("lichessUsername");

    }

    @Test
    void assignsCurrentCoachToNewStudent() {
        CreateStudentRequest request = new CreateStudentRequest("Ion", "Popescu",
        "ion@example.com", null, null);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentResponse response = studentService.createStudent(request, COACH);

        assertThat(response.coachId()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Ion");
    }

    @Test
    void listsOnlyOwnStudentsForCoach() {
        when(studentRepository.findByCoachId(1L)).thenReturn(List.of());

        studentService.getStudents(COACH);

        verify(studentRepository).findByCoachId(1L);
        verify(studentRepository, never()).findAll();
    }

    @Test
    void listsAllStudentsForAdmin() {
        AuthenticatedCoach admin = new AuthenticatedCoach(9L, "admin@example.com", Role.ADMIN);
        when(studentRepository.findAll()).thenReturn(List.of());

        studentService.getStudents(admin);

        verify(studentRepository).findAll();
        verify(studentRepository, never()).findByCoachId(any());
    }

    @Test
    void doesNotFindStudentOfAnotherCoach() {
        when(studentRepository.findByIdAndCoachId(5L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getStudentById(5L, COACH))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void adminFindsAnyStudent() {
        Student student = new Student("Ion", "Popescu", "ion@example.com", 7L);
        AuthenticatedCoach admin = new AuthenticatedCoach(9L, "admin@example.com", Role.ADMIN);
        when(studentRepository.findById(5L)).thenReturn(Optional.of(student));

        assertThat(studentService.getStudentById(5L, admin)).isNotNull();
        verify(studentRepository, never()).findByIdAndCoachId(any(), any());
    }
}
