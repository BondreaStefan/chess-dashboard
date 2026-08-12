package com.bond.chess_dashboard.coach.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record CreateCoachRequest(
    @NotBlank(message = "{coach.firstName.required}")
    @Size(max = 100, message = "{coach.name.tooLong}")
    String firstName,

    @NotBlank(message = "{coach.lastName.required}")
    @Size(max = 100, message = "{coach.name.tooLong}")
    String lastName,

    @NotBlank(message = "{coach.email.required}")
    @Size(max = 255, message = "{coach.email.tooLong}")
    @Email(message = "{coach.email.invalid}")
    String email
) {}
