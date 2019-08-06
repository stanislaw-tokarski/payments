package com.github.stanislawtokarski.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.ErrorResponse;
import com.github.stanislawtokarski.model.Transaction;

import static org.eclipse.jetty.http.HttpStatus.*;

public class HttpResponseBuilder {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String notEnoughFunds() throws JsonProcessingException {
        return mapper.writeValueAsString(new ErrorResponse(
                BAD_REQUEST_400,
                "Payment cannot be processed because of insufficient funds amount"));
    }

    public static String accountDoesNotExist() throws JsonProcessingException {
        return mapper.writeValueAsString(new ErrorResponse(
                NOT_FOUND_404,
                "Account with given ID does not exist"));
    }

    public static String accountAlreadyExists() throws JsonProcessingException {
        return mapper.writeValueAsString(new ErrorResponse(
                CONFLICT_409,
                "Account with given ID already exists"));
    }

    public static String ofAccount(Account account) throws JsonProcessingException {
        return mapper.writeValueAsString(account);
    }

    public static String ofTransaction(Transaction transaction) throws JsonProcessingException {
        return mapper.writeValueAsString(transaction);
    }
}
