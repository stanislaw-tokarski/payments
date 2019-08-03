package com.github.stanislawtokarski.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final BigDecimal amount;
    private final UUID originAccountId;
    private final UUID destinationAccountId;

    public Transaction(BigDecimal amount, UUID originAccountId, UUID destinationAccountId) {
        this.amount = amount;
        this.originAccountId = originAccountId;
        this.destinationAccountId = destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public UUID getOriginAccountId() {
        return originAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }
}
