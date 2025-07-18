package com.isaiiapp.backend.auth.v1.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class AccountLockedException extends BadCredentialsException {

    public AccountLockedException(String message) {
        super(message);
    }
}
