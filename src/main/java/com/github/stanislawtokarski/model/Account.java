package com.github.stanislawtokarski.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.stanislawtokarski.exception.NotEnoughFundsException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final UUID id;
    private BigDecimal balance;

    public Account(
            @JsonProperty("id") UUID id,
            @JsonProperty("balance") BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public void add(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void subtract(BigDecimal amount) throws NotEnoughFundsException {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
        } else {
            throw new NotEnoughFundsException(String.format(
                    "Cannot subtract %s from %s account because of insufficient balance",
                    amount.toString(),
                    id.toString()));
        }
    }
}
