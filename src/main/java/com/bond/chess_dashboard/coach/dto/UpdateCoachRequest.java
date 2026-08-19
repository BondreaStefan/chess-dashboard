package com.bond.chess_dashboard.coach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdateCoachRequest(
    @NotBlank(message = "{firstName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String firstName,

    @NotBlank(message = "{lastName.required}")
    @Size(max = 100, message = "{name.tooLong}")
    String lastName

) {}
