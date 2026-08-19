package com.bond.chess_dashboard.student.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateStudentRequest(
    @NotBlank(message = "{firstName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String firstName,

    @NotBlank(message = "{lastName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String lastName,

    @NotBlank(message = "{email.required}")
    @Size(max = 255, message = "{email.tooLong}")
    @Email(message = "{email.invalid}")
    String email,

    Long coachId,

    @Size(max = 100, message = "{username.tooLong}")
    String lichessUsername,

    @Size(max = 100, message = "{username.tooLong}")
    String chessComUsername
) {}