package com.bond.chess_dashboard.game;

enum GameResult {
    WIN,
    LOSS,
    DRAW,
    UNKNOWN;

    static GameResult from(String pgnResult, Color studentColor){
        return switch(pgnResult){
            case "1-0" -> studentColor == Color.WHITE ? WIN : LOSS;
            case "0-1" -> studentColor == Color.BLACK ? WIN : LOSS;
            case "1/2-1/2" -> DRAW;
            default -> UNKNOWN;
        };
    }
}
