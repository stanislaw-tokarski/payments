package com.github.stanislawtokarski.exception;

public class AccountNotFoundException extends IllegalArgumentException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
