package com.github.stanislawtokarski.service;

import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.model.exception.AccountNotFoundException;
import com.github.stanislawtokarski.model.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.repository.AccountsDatastore;

import java.math.BigDecimal;
import java.util.UUID;

public class PaymentsService {

    private final AccountsDatastore accounts;

    public PaymentsService(AccountsDatastore accounts) {
        this.accounts = accounts;
    }

    public void transfer(Transaction transaction) throws NotEnoughFundsException, AccountNotFoundException {
        UUID originAccountId = transaction.getOriginAccountId();
        UUID destinationAccountId = transaction.getDestinationAccountId();
        BigDecimal amount = transaction.getAmount();
        synchronized (accounts) {
            final Account origin = fetchAccount(originAccountId);
            final Account destination = fetchAccount(destinationAccountId);
            origin.subtract(amount);
            destination.add(amount);
            accounts.overwriteAccount(origin);
            accounts.overwriteAccount(destination);
        }
    }

    public void addAccount(Account account) {
        accounts.addAccount(account);
    }

    public Account fetchAccount(UUID id) throws AccountNotFoundException {
        return accounts.fetchAccount(id);
    }
}
