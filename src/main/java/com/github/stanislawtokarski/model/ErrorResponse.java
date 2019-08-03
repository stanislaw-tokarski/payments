package com.github.stanislawtokarski.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponse {
    private final int status;
    private final String message;

    public ErrorResponse(
            @JsonProperty("status") int status,
            @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
    }
}
