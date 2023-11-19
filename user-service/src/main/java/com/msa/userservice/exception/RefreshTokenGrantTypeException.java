package com.msa.userservice.exception;

public class RefreshTokenGrantTypeException extends RuntimeException {
    public RefreshTokenGrantTypeException(String message) {
        super(message);
    }
}
