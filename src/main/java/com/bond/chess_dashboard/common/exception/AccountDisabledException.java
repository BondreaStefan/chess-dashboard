package com.bond.chess_dashboard.common.exception;

public class AccountDisabledException extends RuntimeException{
    
    public AccountDisabledException(String message) {
        
        super(message);
    }
}
