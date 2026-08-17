package com.bond.chess_dashboard.coach.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UpdateCoachRequest(
    @NotBlank(message = "{coach.firstName.required}")
    @Size(max = 100, message = "{coach.name.tooLong}")
    String firstName,

    @NotBlank(message = "{coach.lastName.required}")
    @Size(max = 100, message = "{coach.name.tooLong}")
    String lastName

) {}
