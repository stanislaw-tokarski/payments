package com.github.stanislawtokarski.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final BigDecimal amount;
    private final UUID originAccountId;
    private final UUID destinationAccountId;

    public Transaction(
            @JsonProperty("amount") BigDecimal amount,
            @JsonProperty("originId") UUID originAccountId,
            @JsonProperty("destinationId") UUID destinationAccountId) {
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

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", originAccountId=" + originAccountId +
                ", destinationAccountId=" + destinationAccountId +
                '}';
    }
}
