package com.github.stanislawtokarski.model.exception;

public class AccountNotFoundException extends IllegalArgumentException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
