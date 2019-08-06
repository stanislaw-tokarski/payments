package com.github.stanislawtokarski.model.exception;

public class AccountAlreadyExistsException extends IllegalArgumentException {
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}
