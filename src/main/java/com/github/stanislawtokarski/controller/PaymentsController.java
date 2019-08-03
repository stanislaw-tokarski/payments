package com.github.stanislawtokarski.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stanislawtokarski.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.exception.AccountNotFoundException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.ErrorResponse;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.service.PaymentsService;
import spark.Route;

import java.math.BigDecimal;
import java.util.UUID;

import static org.eclipse.jetty.http.HttpStatus.CONFLICT_409;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;

public class PaymentsController {

    private final PaymentsService paymentsService;
    private ObjectMapper mapper = new ObjectMapper();

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    public Route getAccount() {
        return (request, response) -> {
            try {
                UUID id = UUID.fromString(request.params(":id"));
                return paymentsService.fetchAccount(id);
            } catch (AccountNotFoundException e) {
                return new ErrorResponse(NOT_FOUND_404, "Account with given ID does not exist");
            }
        };
    }

    public Route addAccount() {
        return (request, response) -> {
            try {
                Account account = new Account(UUID.fromString(request.params(":id")), getRandomInitialBalance());
                paymentsService.addAccount(account);
                return account;
            } catch (AccountAlreadyExistsException e) {
                return new ErrorResponse(CONFLICT_409, "Account with given ID already exists");
            }
        };
    }

    public Route generateAccount() {
        return (request, response) -> {
            Account account = new Account(UUID.randomUUID(), getRandomInitialBalance());
            paymentsService.addAccount(account);
            return account;
        };
    }

    public Route transfer() {
        return (request, response) -> {
            Transaction transaction = mapper.readValue(request.body(), Transaction.class);
            try {
                paymentsService.transfer(transaction);
            } catch (AccountNotFoundException e) {
                return new ErrorResponse(NOT_FOUND_404, "Account with given ID does not exist");
            }
            return transaction;
        };
    }

    private BigDecimal getRandomInitialBalance() {
        Long random = 1L + (long) (Math.random() * (99L));
        return BigDecimal.valueOf(random);
    }
}
