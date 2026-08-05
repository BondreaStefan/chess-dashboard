package com.bond.chess_dashboard;

import org.springframework.boot.SpringApplication;

public class TestChessDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.from(ChessDashboardApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
