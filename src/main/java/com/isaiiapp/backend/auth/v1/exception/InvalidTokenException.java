package com.isaiiapp.backend.auth.v1.exception;

public class InvalidTokenException extends AuthException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
