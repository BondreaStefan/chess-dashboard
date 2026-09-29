package com.bond.chess_dashboard.coach;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component 
class AdminBootstrap implements ApplicationRunner {
    
    private final CoachRepository coachRepository;
    private final PasswordEncoder passwordEncoder;
    private final String email;
    private final String password;
    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    public AdminBootstrap(CoachRepository coachRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.email:}") String email,
            @Value("${admin.password:}") String password) {
        
        this.coachRepository = coachRepository;
        this.passwordEncoder = passwordEncoder;
        this.email = email;
        this.password = password;

    }
    
    @Override
    public void run(ApplicationArguments args) {
        if (email.isBlank() || password.isBlank()) {
            log.info("Admin bootstrap skipped: ADMIN_EMAIL or ADMIN_PASSWORD not set");
            return;
        }

        if (coachRepository.existsByRole(Role.ADMIN)) {
            return;
        }

        Coach admin = Coach.admin("Admin", "User", email, passwordEncoder.encode(password));
        coachRepository.save(admin);
        log.info("Admin account created for {}", email);
    }
}
