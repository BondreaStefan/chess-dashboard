package com.bond.chess_dashboard.coach;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapTest {

    @Mock
    private CoachRepository coachRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminBootstrap bootstrap(String email, String password) {
        return new AdminBootstrap(coachRepository, passwordEncoder, email, password);
    }

    @Test
    void skipsWhenCredentialsAreNotConfigured() {
        bootstrap("", "").run(null);

        verifyNoInteractions(coachRepository, passwordEncoder);
    }

    @Test
    void skipsWhenAdminAlreadyExists() {
        when(coachRepository.existsByRole(Role.ADMIN)).thenReturn(true);

        bootstrap("admin@localhost", "secret-password").run(null);

        verify(coachRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void createsAdminWithHashedPassword() {
        when(coachRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        when(passwordEncoder.encode("secret-password")).thenReturn("hashed-value");

        bootstrap("admin@localhost", "secret-password").run(null);

        ArgumentCaptor<Coach> captor = ArgumentCaptor.forClass(Coach.class);
        verify(coachRepository).save(captor.capture());

        Coach saved = captor.getValue();
        assertThat(saved.getRole()).isEqualTo(Role.ADMIN);
        assertThat(saved.getEmail()).isEqualTo("admin@localhost");
        assertThat(saved.getPasswordHash()).isEqualTo("hashed-value");
    }
}
