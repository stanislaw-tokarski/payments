package com.github.stanislawtokarski.service;

import com.github.stanislawtokarski.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.repository.AccountsDatastore;
import com.github.stanislawtokarski.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentsService {

    private final AccountsDatastore accounts;

    public PaymentsService(AccountsDatastore accounts) {
        this.accounts = accounts;
    }

    public synchronized void transfer(Transaction transaction) throws NotEnoughFundsException {
        UUID originAccountId = transaction.getOriginAccountId();
        UUID destinationAccountId = transaction.getDestinationAccountId();
        BigDecimal amount = transaction.getAmount();
        final Account origin = fetchAndSubtract(originAccountId, amount);
        accounts.overwriteAccount(origin);
        final Account destination = fetchAndAdd(destinationAccountId, amount);
        accounts.overwriteAccount(destination);
    }

    public void addAccount(Account account) {
        accounts.addAccount(account);
    }

    public Account fetchAccount(UUID id) {
        return accounts.fetchAccount(id);
    }

    private Account fetchAndSubtract(UUID id, BigDecimal amount) throws NotEnoughFundsException {
        final Account processed = accounts.fetchAccount(id);
        processed.subtract(amount);
        return processed;
    }

    private Account fetchAndAdd(UUID id, BigDecimal amount) {
        final Account processed = accounts.fetchAccount(id);
        processed.add(amount);
        return processed;
    }
}
