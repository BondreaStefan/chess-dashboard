package com.bond.chess_dashboard.game.dto;

import com.bond.chess_dashboard.game.Color;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateGameRequest(
    @NotNull(message = "{studentId.required}")
    Long studentId,

    @NotBlank(message = "{pgn.required}")
    String pgn,

    Color studentColor
) {}    

