package com.bond.chess_dashboard.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bond.chess_dashboard.auth.dto.AuthResponse;
import com.bond.chess_dashboard.auth.dto.LoginRequest;
import com.bond.chess_dashboard.auth.dto.RegisterRequest;
import com.bond.chess_dashboard.coach.CoachService;
import com.bond.chess_dashboard.coach.Role;
import com.bond.chess_dashboard.coach.dto.CoachCredentials;
import com.bond.chess_dashboard.coach.dto.CoachResponse;
import com.bond.chess_dashboard.common.exception.AccountDisabledException;
import com.bond.chess_dashboard.common.exception.InvalidCredentialsException;


@Service 
public class AuthService {
    
    private static final String INVALID_CREDENTIALS = "Invalid email or password";

    private final CoachService coachService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CoachService coachService, PasswordEncoder passwordEncoder, JwtService jwtService)
    {
        this.coachService = coachService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String hash = passwordEncoder.encode(request.password());
        CoachResponse coach = coachService.register(request.firstName(), request.lastName(), request.email(), hash);

        String token = jwtService.generateToken(coach.email(), Role.COACH);
        return new AuthResponse(token, coach.id(), coach.email(), Role.COACH);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        CoachCredentials credentials = coachService.findCredentialsByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS));

        if(!passwordEncoder.matches(request.password(), credentials.passwordHash())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }

        if(!credentials.enabled()) {
            throw new AccountDisabledException("Account is disabled");
        }
        
        String token = jwtService.generateToken(credentials.email(), credentials.role());

        return new AuthResponse(token, credentials.id(), credentials.email(), credentials.role());
    }
}
