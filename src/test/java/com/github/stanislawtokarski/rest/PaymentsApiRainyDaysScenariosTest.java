package com.github.stanislawtokarski.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.rest.config.PaymentsApiContext;
import com.github.stanislawtokarski.rest.config.PaymentsApiContextAsParameterExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.eclipse.jetty.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(PaymentsApiContextAsParameterExtension.class)
class PaymentsApiRainyDaysScenariosTest extends PaymentsApiTest {

    private static final String NON_EXISTING_ACCOUNT_ID = "f5ebb38b-36ac-4e77-b028-6ab326cb09db";

    @Test
    void shouldNotReturnDetailsOfNonExistingAccount(PaymentsApiContext context) {
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + NON_EXISTING_ACCOUNT_ID)
                .then()
                .assertThat()
                .statusCode(NOT_FOUND_404)
                .body("status", equalTo(NOT_FOUND_404))
                .body("message", equalTo("Account with given ID does not exist"));
    }

    @Test
    void shouldNotCreateAccountIfIdAlreadyInUse(PaymentsApiContext context) {
        String id = "8a1b7c6e-012c-4f87-b816-10ea309f10ef";
        given()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH + SLASH + id);

        when()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH + SLASH + id)
                .then()
                .assertThat()
                .statusCode(CONFLICT_409)
                .body("status", equalTo(CONFLICT_409))
                .body("message", equalTo("Account with given ID already exists"));
    }

    @Test
    void shouldNotTransferMoneyFromNonExistingAccount(PaymentsApiContext context) throws JsonProcessingException {
        Account destinationAccount = given()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH)
                .then()
                .extract()
                .as(Account.class);
        BigDecimal amount = new BigDecimal("10.0");
        Transaction transaction = new Transaction(
                amount,
                UUID.fromString(NON_EXISTING_ACCOUNT_ID),
                destinationAccount.getId());

        given()
                .body(mapper.writeValueAsString(transaction))
                .when()
                .post(context.getPaymentsApiUrl() + PAYMENTS_PATH)
                .then()
                .assertThat()
                .statusCode(NOT_FOUND_404)
                .body("status", equalTo(NOT_FOUND_404))
                .body("message", equalTo("Account with given ID does not exist"));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + destinationAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(destinationAccount.getId().toString()))
                .body("balance", equalTo(destinationAccount.getBalance().intValue()));
    }

    @Test
    void shouldNotTransferMoneyToNonExistingAccount(PaymentsApiContext context) throws JsonProcessingException {
        Account originAccount = when()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH)
                .then()
                .extract()
                .as(Account.class);
        BigDecimal amount = new BigDecimal("10.0");
        Transaction transaction = new Transaction(
                amount,
                originAccount.getId(),
                UUID.fromString(NON_EXISTING_ACCOUNT_ID));

        given()
                .body(mapper.writeValueAsString(transaction))
                .when()
                .post(context.getPaymentsApiUrl() + PAYMENTS_PATH)
                .then()
                .assertThat()
                .statusCode(NOT_FOUND_404)
                .body("status", equalTo(NOT_FOUND_404))
                .body("message", equalTo("Account with given ID does not exist"));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + originAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(originAccount.getId().toString()))
                .body("balance", equalTo(originAccount.getBalance().intValue()));
    }

    @Test
    void shouldNotTransferMoneyIfAmountExceedsFundsAvailableOnOriginAccount(PaymentsApiContext context) throws JsonProcessingException {
        Account originAccount = given()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH)
                .then()
                .extract()
                .as(Account.class);
        Account destinationAccount = given()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH)
                .then()
                .extract()
                .as(Account.class);
        BigDecimal amount = new BigDecimal("1000.0");
        Transaction transaction = new Transaction(
                amount,
                originAccount.getId(),
                destinationAccount.getId());

        given()
                .body(mapper.writeValueAsString(transaction))
                .when()
                .post(context.getPaymentsApiUrl() + PAYMENTS_PATH)
                .then()
                .assertThat()
                .statusCode(BAD_REQUEST_400)
                .body("status", equalTo(BAD_REQUEST_400))
                .body("message", equalTo("Payment cannot be processed because of insufficient funds amount"));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + originAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(originAccount.getId().toString()))
                .body("balance", equalTo(originAccount.getBalance().intValue()));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + destinationAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(destinationAccount.getId().toString()))
                .body("balance", equalTo(destinationAccount.getBalance().intValue()));
    }
}
