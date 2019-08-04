package com.github.stanislawtokarski.rest.config;

public class PaymentsApiContext {
    private final String paymentsApiUrl;

    public PaymentsApiContext(String paymentsApiUrl) {
        this.paymentsApiUrl = paymentsApiUrl;
    }

    public String getPaymentsApiUrl() {
        return paymentsApiUrl;
    }
}
