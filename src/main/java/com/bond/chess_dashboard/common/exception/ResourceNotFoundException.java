package com.bond.chess_dashboard.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceType, Object id) {
        
        super("%s with id %s not found".formatted(resourceType, id));
    }
    
}
