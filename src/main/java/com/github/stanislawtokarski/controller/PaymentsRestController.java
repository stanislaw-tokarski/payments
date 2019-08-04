package com.github.stanislawtokarski.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stanislawtokarski.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.exception.AccountNotFoundException;
import com.github.stanislawtokarski.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.ErrorResponse;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.service.PaymentsService;
import spark.Route;

import java.math.BigDecimal;
import java.util.UUID;

import static org.eclipse.jetty.http.HttpStatus.*;

public class PaymentsRestController {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    private final PaymentsService paymentsService;
    private ObjectMapper mapper = new ObjectMapper();

    public PaymentsRestController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    public Route getAccount() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            try {
                UUID id = UUID.fromString(request.params(":id"));
                response.status(OK_200);
                return mapper.writeValueAsString(paymentsService.fetchAccount(id));
            } catch (AccountNotFoundException e) {
                response.status(NOT_FOUND_404);
                return mapper.writeValueAsString(new ErrorResponse(
                        NOT_FOUND_404,
                        "Account with given ID does not exist"));
            }
        };
    }

    public Route addAccount() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            try {
                Account account = new Account(UUID.fromString(request.params(":id")), BigDecimal.valueOf(100L));
                response.status(CREATED_201);
                paymentsService.addAccount(account);
                return mapper.writeValueAsString(account);
            } catch (AccountAlreadyExistsException e) {
                response.status(CONFLICT_409);
                return mapper.writeValueAsString(new ErrorResponse(
                        CONFLICT_409,
                        "Account with given ID already exists"));
            }
        };
    }

    public Route generateAccount() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            Account account = new Account(UUID.randomUUID(), BigDecimal.valueOf(100L));
            paymentsService.addAccount(account);
            response.status(CREATED_201);
            return mapper.writeValueAsString(account);
        };
    }

    public Route transfer() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            Transaction transaction = mapper.readValue(request.body(), Transaction.class);
            try {
                paymentsService.transfer(transaction);
                response.status(OK_200);
                return mapper.writeValueAsString(transaction);
            } catch (AccountNotFoundException e) {
                response.status(NOT_FOUND_404);
                return mapper.writeValueAsString(new ErrorResponse(
                        NOT_FOUND_404,
                        "Account with given ID does not exist"));
            } catch (NotEnoughFundsException e) {
                response.status(BAD_REQUEST_400);
                return mapper.writeValueAsString(new ErrorResponse(
                        BAD_REQUEST_400,
                        "Payment cannot be processed because of insufficient funds amount"));
            }
        };
    }
}
