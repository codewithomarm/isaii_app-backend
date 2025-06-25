package com.isaiiapp.backend.auth.v1.exception;

public class AccountLockedException extends AuthException {

    public AccountLockedException(String message) {
        super(message);
    }
}
