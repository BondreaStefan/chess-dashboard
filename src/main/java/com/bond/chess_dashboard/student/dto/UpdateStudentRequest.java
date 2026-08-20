package com.bond.chess_dashboard.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public record UpdateStudentRequest(
    @NotBlank(message = "{firstName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String firstName,

    @NotBlank(message = "{lastName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String lastName,

    @Size(max = 100, message = "{username.tooLong}")
    String lichessUsername,

    @Size(max = 100, message = "{username.tooLong}")
    String chessComUsername
) {}