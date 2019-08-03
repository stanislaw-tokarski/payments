package com.github.stanislawtokarski.model;

import com.github.stanislawtokarski.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.exception.AccountNotFoundException;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountsDatastore {

    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    public void addAccount(Account account) {
        final UUID accountId = account.getId();
        if (accounts.containsKey(accountId)) {
            throw new AccountAlreadyExistsException(String.format("Account %s already exists", accountId.toString()));
        }
        accounts.put(accountId, account);
    }

    public Account fetchAccount(UUID accountId) {
        final Account account = accounts.get(accountId);
        if (account == null) {
            throw new AccountNotFoundException(String.format("Account %s not found", accountId.toString()));
        }
        return account;
    }

    public void modifyAccount(Account account) {
        accounts.replace(account.getId(), account);
    }
}
