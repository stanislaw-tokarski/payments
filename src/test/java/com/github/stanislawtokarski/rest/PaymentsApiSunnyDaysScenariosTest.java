package com.github.stanislawtokarski.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.rest.config.PaymentsApiContext;
import com.github.stanislawtokarski.rest.config.PaymentsApiContextAsParameterExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.eclipse.jetty.http.HttpStatus.CREATED_201;
import static org.eclipse.jetty.http.HttpStatus.OK_200;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(PaymentsApiContextAsParameterExtension.class)
class PaymentsApiSunnyDaysScenariosTest extends PaymentsApiTest {

    @Test
    void shouldAddNewAccount(PaymentsApiContext context) {
        when()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH)
                .then()
                .assertThat()
                .statusCode(CREATED_201)
                .body("balance", equalTo(100));
    }

    @Test
    void shouldAddNewAccountWithPredefinedId(PaymentsApiContext context) {
        String id = "128eb157-a517-4eb6-920b-2f7a571b73af";
        when()
                .post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH + SLASH + id)
                .then()
                .assertThat()
                .statusCode(CREATED_201)
                .body("id", equalTo(id))
                .body("balance", equalTo(100));
    }

    @Test
    void shouldReturnDetailsOfExistingAccount(PaymentsApiContext context) {
        String id = "f9977178-7189-4cf8-9e4b-545a0f096954";

        given().post(context.getPaymentsApiUrl() + CREATE_ACCOUNT_PATH + SLASH + id);

        when().get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + id)
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(id))
                .body("balance", equalTo(100));
    }

    @Test
    void shouldTransferMoneyCorrectly(PaymentsApiContext context) throws JsonProcessingException {
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
        BigDecimal amount = new BigDecimal("10.0");
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
                .statusCode(OK_200)
                .body("amount", equalTo(amount))
                .body("originAccountId", equalTo(originAccount.getId()))
                .body("destinationAccountId", equalTo(destinationAccount.getId()));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + originAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(originAccount.getId()))
                .body("balance", equalTo(originAccount.getBalance().subtract(amount)));
        when()
                .get(context.getPaymentsApiUrl() + GET_ACCOUNT_PATH + SLASH + destinationAccount.getId())
                .then()
                .assertThat()
                .statusCode(OK_200)
                .body("id", equalTo(destinationAccount.getId()))
                .body("balance", equalTo(destinationAccount.getBalance().add(amount)));
    }
}
