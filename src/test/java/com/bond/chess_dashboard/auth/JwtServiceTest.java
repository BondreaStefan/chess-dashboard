package com.bond.chess_dashboard.auth;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.bond.chess_dashboard.coach.Role;

class JwtServiceTest {

    private static final String SECRET = "a-very-long-development-secret-key-for-tests-only-123456";
    private static final long ONE_HOUR = 3_600_000L;

    private final JwtService jwtService = new JwtService(SECRET, ONE_HOUR);

    @Test
    void generatesValidToken() {
        String token = jwtService.generateToken("coach@example.com", Role.COACH);

        assertThat(jwtService.isValid(token)).isTrue();
    }

    @Test
    void extractsEmailFromToken() {
        String token = jwtService.generateToken("coach@example.com", Role.COACH);

        assertThat(jwtService.extractEmail(token)).isEqualTo("coach@example.com");
    }

    @Test
    void extractsRoleFromToken() {
        String token = jwtService.generateToken("admin@example.com", Role.ADMIN);

        assertThat(jwtService.extractRole(token)).isEqualTo(Role.ADMIN);
    }

    @Test
    void rejectsTamperedToken() {
        String token = jwtService.generateToken("coach@example.com", Role.COACH);

        // schimbă un caracter din payload — semnătura nu mai corespunde
        String tampered = token.substring(0, 20) + "X" + token.substring(21);

        assertThat(jwtService.isValid(tampered)).isFalse();
    }

    @Test
    void rejectsExpiredToken() {
        JwtService shortLived = new JwtService(SECRET, -1000L);
        String token = shortLived.generateToken("coach@example.com", Role.COACH);

        assertThat(shortLived.isValid(token)).isFalse();
    }

    @Test
    void rejectsTokenSignedWithDifferentKey() {
        JwtService other = new JwtService(
                "a-completely-different-secret-key-for-testing-purposes-99", ONE_HOUR);
        String token = other.generateToken("coach@example.com", Role.COACH);

        assertThat(jwtService.isValid(token)).isFalse();
    }

    @Test
    void rejectsGarbage() {
        assertThat(jwtService.isValid("nu-este-un-token")).isFalse();
    }
}
