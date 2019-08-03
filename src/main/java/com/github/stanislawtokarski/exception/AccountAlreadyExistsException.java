package com.github.stanislawtokarski.exception;

public class AccountAlreadyExistsException extends IllegalArgumentException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
