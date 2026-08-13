package com.bond.chess_dashboard.common.exception;

public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String resourceType, String field, Object value) {
        
        super("%s with %s %s already exists".formatted(resourceType, field, value));
    }
    
}
