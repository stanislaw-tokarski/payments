package com.github.stanislawtokarski;

import com.github.stanislawtokarski.controller.PaymentsController;
import com.github.stanislawtokarski.model.AccountsDatastore;
import com.github.stanislawtokarski.service.PaymentsService;

import static spark.Spark.*;

public class PaymentsApplicationRunner {
    public static void main(String[] args) {
        AccountsDatastore accountsDatastore = new AccountsDatastore();
        PaymentsService paymentsService = new PaymentsService(accountsDatastore);
        PaymentsController paymentsController = new PaymentsController(paymentsService);

        path("/accounts", () -> {
            post("/", paymentsController.generateAccount());
            post("/:id", paymentsController.addAccount());
            get("/:id", paymentsController.getAccount());
        });
        path("/transactions", () -> {
            post("/", paymentsController.transfer());
        });
    }
}
