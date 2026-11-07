package com.bond.chess_dashboard.game;

public enum TimeClass { 
    ULTRA_BULLET, 
    BULLET, 
    BLITZ, 
    RAPID, 
    CLASSICAL, 
    CORRESPONDENCE, 
    UNKNOWN;

    static TimeClass from(String timeControl, Platform platform) {
        if(timeControl == null || timeControl.isEmpty() || platform == null) {
            return UNKNOWN;
        }
        if((timeControl.contains("/")) || (timeControl.equals("-") && platform == Platform.LICHESS)) {
            return CORRESPONDENCE;
        }

        String[] parts = timeControl.split("\\+");
        int base;
        int increment;
        try {
            base = Integer.parseInt(parts[0]);
            increment = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
        } catch (NumberFormatException e) {
            return UNKNOWN;
        }
        int totalTime = base + 40 * increment;

        return switch (platform) {
            case LICHESS, OTHER -> lichessClass(totalTime);
            case CHESS_COM -> chessComClass(totalTime);
        };
    }

    private static TimeClass lichessClass(int totalTime) {
        if(totalTime < 30) {
            return ULTRA_BULLET;
        } else if(totalTime < 180) {
            return BULLET;
        } else if(totalTime < 480) {
            return BLITZ;
        } else if(totalTime < 1500) {
            return RAPID;
        } else {
            return CLASSICAL;
        }
    }
    
    private static TimeClass chessComClass(int totalTime) {
        if(totalTime < 180) {
            return BULLET;
        } else if(totalTime < 600) {
            return BLITZ;
        } else {
            return RAPID;
        }
    }
}

