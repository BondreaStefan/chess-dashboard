package com.bond.chess_dashboard.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
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

    @NotBlank(message = "{password.required}")
    @Size(min = 8, max = 72, message = "{password.Length}")
    String password
) {}
