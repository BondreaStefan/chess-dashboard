package com.bond.chess_dashboard.common.exception;

public class InvalidPgnException extends RuntimeException{

    public InvalidPgnException(String message){
        
        super(message);
    }

    public InvalidPgnException(String message, Throwable cause) {
        
        super(message, cause);
    }   
    
}
