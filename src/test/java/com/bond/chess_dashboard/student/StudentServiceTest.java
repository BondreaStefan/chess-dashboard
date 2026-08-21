package com.bond.chess_dashboard.student;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bond.chess_dashboard.coach.CoachService;
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

    @Test
    void allowsUpdateWhenLichessUsernameIsUnchanged() {
        Student existing = new Student("Andrei", "Ionescu", "andrei@example.com", 1L);
        existing.setLichessUsername("andrei_chess");

        UpdateStudentRequest request = new UpdateStudentRequest(
            "Andrei-Mihai", "Ionescu", "andrei_chess", null);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));

        StudentResponse response = studentService.updateStudent(1L, request);

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

        when(studentRepository.findById(2L)).thenReturn(Optional.of(secondStudent));
        when(studentRepository.existsByLichessUsername("andrei_chess")).thenReturn(true);

        assertThatThrownBy(() -> studentService.updateStudent(2L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("lichessUsername");

    }

    @Test
    void throwsWhenCreatingWithNonExistentCoach(){
        CreateStudentRequest request = new CreateStudentRequest("Ion", "Popescu",
         "ion@example.com", 1L, null, null);

        when(coachService.coachExists(1L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.createStudent(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coach");
        
        verify(studentRepository, never()).save(any());
    }

    @Test
    void createsWhenCoachIsNull(){
        CreateStudentRequest request = new CreateStudentRequest("Ion", "Popescu",
         "ion@example.com", null, null, null);
        when(studentRepository.save(any(Student.class))).thenAnswer(inv -> inv.getArgument(0));

        StudentResponse response = studentService.createStudent(request);
        assertThat(response.firstName()).isEqualTo("Ion");
        assertThat(response.lastName()).isEqualTo("Popescu");
        assertThat(response.email()).isEqualTo("ion@example.com");
        assertThat(response.coachId()).isNull();  

        verify(coachService, never()).coachExists(any());
    }

    @Test
    void throwsWhenGetStudentsWithNonExistentCoach(){
        when(coachService.coachExists(1L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.getStudentsByCoachId(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Coach");
        
        verify(studentRepository, never()).findByCoachId(any());
    }
}
