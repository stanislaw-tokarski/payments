package com.github.stanislawtokarski;

import com.github.stanislawtokarski.controller.PaymentsRestController;
import com.github.stanislawtokarski.repository.AccountsDatastore;
import com.github.stanislawtokarski.service.PaymentsService;

import static spark.Spark.*;

public class PaymentsApplicationRunner {

    private static final String ACCOUNTS = "/accounts";
    private static final String CREATE = "/create";
    private static final String ID = "/:id";
    private static final String PAYMENTS = "/payments";

    public static void main(String[] args) {
        AccountsDatastore accountsDatastore = new AccountsDatastore();
        PaymentsService paymentsService = new PaymentsService(accountsDatastore);
        PaymentsRestController paymentsController = new PaymentsRestController(paymentsService);

        path(ACCOUNTS, () -> {
            post(CREATE, paymentsController.generateAccount());
            post(CREATE + ID, paymentsController.addAccount());
            get(ID, paymentsController.getAccount());
        });
        path(PAYMENTS, () -> post("", paymentsController.transfer()));
    }
}
