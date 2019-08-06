package com.github.stanislawtokarski.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

class PaymentsApiTest {
    static final String CREATE_ACCOUNT_PATH = "/accounts/create/";
    static final String GET_ACCOUNT_PATH = "/accounts/";
    static final String PAYMENTS_PATH = "/payments";
    final ObjectMapper mapper = new ObjectMapper();
}
