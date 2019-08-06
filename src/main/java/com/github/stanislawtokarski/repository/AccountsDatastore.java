package com.github.stanislawtokarski.repository;

import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.model.exception.AccountNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountsDatastore {

    private final Map<UUID, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        final UUID accountId = account.getId();
        if (accounts.containsKey(accountId)) {
            throw new AccountAlreadyExistsException(String.format("Account %s already exists", accountId.toString()));
        }
        accounts.put(accountId, account);
    }

    public Account fetchAccount(UUID accountId) throws AccountNotFoundException {
        if (!accounts.containsKey(accountId)) {
            throw new AccountNotFoundException(String.format("Account %s not found", accountId.toString()));
        }
        return accounts.get(accountId);
    }

    public void overwriteAccount(Account account) {
        accounts.replace(account.getId(), account);
    }

    //Visible for testing
    Map<UUID, Account> getAccounts() {
        return accounts;
    }
}
