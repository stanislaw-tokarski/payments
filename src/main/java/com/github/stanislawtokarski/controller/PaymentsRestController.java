package com.github.stanislawtokarski.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.model.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.model.exception.AccountNotFoundException;
import com.github.stanislawtokarski.model.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.service.PaymentsService;
import com.github.stanislawtokarski.util.HttpResponseBuilder;
import org.apache.http.entity.ContentType;
import spark.Route;

import java.math.BigDecimal;
import java.util.UUID;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.eclipse.jetty.http.HttpStatus.*;

public class PaymentsRestController {

    private static final String APPLICATION_JSON = String.valueOf(ContentType.APPLICATION_JSON);

    private final PaymentsService paymentsService;
    private final ObjectMapper mapper = new ObjectMapper();

    public PaymentsRestController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    public Route getAccount() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            try {
                final UUID id = UUID.fromString(request.params(":id"));
                final Account account = paymentsService.fetchAccount(id);
                response.status(OK_200);
                return HttpResponseBuilder.ofAccount(account);
            } catch (AccountNotFoundException e) {
                response.status(NOT_FOUND_404);
                return HttpResponseBuilder.accountDoesNotExist();
            }
        };
    }

    public Route addAccount() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            try {
                final UUID id = UUID.fromString(request.params(":id"));
                final Account account = new Account(id, BigDecimal.valueOf(100L));
                paymentsService.addAccount(account);
                response.status(CREATED_201);
                return HttpResponseBuilder.ofAccount(account);
            } catch (AccountAlreadyExistsException e) {
                response.status(CONFLICT_409);
                return HttpResponseBuilder.accountAlreadyExists();
            }
        };
    }

    public Route generateAccount() {
        return (request, response) -> {
            final Account account = new Account(UUID.randomUUID(), BigDecimal.valueOf(100L));
            paymentsService.addAccount(account);
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            response.status(CREATED_201);
            return HttpResponseBuilder.ofAccount(account);
        };
    }

    public Route transfer() {
        return (request, response) -> {
            response.header(CONTENT_TYPE, APPLICATION_JSON);
            final Transaction transaction = mapper.readValue(request.body(), Transaction.class);
            try {
                paymentsService.transfer(transaction);
                response.status(OK_200);
                return HttpResponseBuilder.ofTransaction(transaction);
            } catch (AccountNotFoundException e) {
                response.status(NOT_FOUND_404);
                return HttpResponseBuilder.accountDoesNotExist();
            } catch (NotEnoughFundsException e) {
                response.status(BAD_REQUEST_400);
                return HttpResponseBuilder.notEnoughFunds();
            }
        };
    }
}
