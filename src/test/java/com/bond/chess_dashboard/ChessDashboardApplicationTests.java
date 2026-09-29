package com.bond.chess_dashboard;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@TestPropertySource(properties = "jwt.secret=KN4tIfAHm5KV7tFxmqELkr0d48nL6ilHsey386BlYeE=")
public class ChessDashboardApplicationTests {

	@Test
	void contextLoads() {
	}

}
