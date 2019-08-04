package com.github.stanislawtokarski;

import com.github.stanislawtokarski.controller.PaymentsRestController;
import com.github.stanislawtokarski.repository.AccountsDatastore;
import com.github.stanislawtokarski.service.PaymentsService;

import static spark.Spark.*;

public class PaymentsApplicationRunner {
    public static void main(String[] args) {
        AccountsDatastore accountsDatastore = new AccountsDatastore();
        PaymentsService paymentsService = new PaymentsService(accountsDatastore);
        PaymentsRestController paymentsController = new PaymentsRestController(paymentsService);

        path("/accounts", () -> {
            post("/create", paymentsController.generateAccount());
            post("/create/:id", paymentsController.addAccount());
            get("/:id", paymentsController.getAccount());
        });
        path("/payments", () -> post("", paymentsController.transfer()));
    }
}
