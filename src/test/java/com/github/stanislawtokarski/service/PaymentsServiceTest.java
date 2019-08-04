package com.github.stanislawtokarski.service;

import com.github.stanislawtokarski.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.exception.AccountNotFoundException;
import com.github.stanislawtokarski.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.AccountsDatastore;
import com.github.stanislawtokarski.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentsServiceTest {

    private AccountsDatastore accountsDatastore = new AccountsDatastore();
    private PaymentsService paymentsService = new PaymentsService(accountsDatastore);

    @Test
    void shouldCorrectlyPerformMoneyTransfer() throws NotEnoughFundsException {
        //given
        Account origin = new Account(UUID.randomUUID(), new BigDecimal("100.0"));
        Account destination = new Account(UUID.randomUUID(), new BigDecimal("10.0"));
        paymentsService.addAccount(origin);
        paymentsService.addAccount(destination);
        Transaction transaction = new Transaction(new BigDecimal("50.0"), origin.getId(), destination.getId());

        //when
        paymentsService.transfer(transaction);

        //then
        Account originAfterTransfer = paymentsService.fetchAccount(origin.getId());
        Account destinationAfterTransfer = paymentsService.fetchAccount(destination.getId());
        assertEquals(originAfterTransfer.getBalance(), new BigDecimal("50.0"));
        assertEquals(destinationAfterTransfer.getBalance(), new BigDecimal("60.0"));
    }

    @Test
    void shouldNotTransferMoneyWhenAccountBalanceIsInsufficient() throws NotEnoughFundsException {
        //given
        Account origin = new Account(UUID.randomUUID(), new BigDecimal("100.0"));
        Account destination = new Account(UUID.randomUUID(), new BigDecimal("10.0"));
        paymentsService.addAccount(origin);
        paymentsService.addAccount(destination);
        Transaction transaction = new Transaction(new BigDecimal("110.0"), origin.getId(), destination.getId());

        //when
        assertThrows(NotEnoughFundsException.class, () -> paymentsService.transfer(transaction));

        //then
        Account originAfterTransfer = paymentsService.fetchAccount(origin.getId());
        Account destinationAfterTransfer = paymentsService.fetchAccount(destination.getId());
        assertEquals(originAfterTransfer.getBalance(), new BigDecimal("100.0"));
        assertEquals(destinationAfterTransfer.getBalance(), new BigDecimal("10.0"));
    }

    @Test
    void shouldNotTransferMoneyFromNonExistingAccount() {
        Account destination = new Account(UUID.randomUUID(), new BigDecimal("0.1"));
        paymentsService.addAccount(destination);
        Transaction transaction = new Transaction(new BigDecimal("0.1"), UUID.randomUUID(), destination.getId());

        assertThrows(AccountNotFoundException.class, () -> paymentsService.transfer(transaction));
    }

    @Test
    void shouldNotTransferMoneyToNonExistingAccount() {
        Account origin = new Account(UUID.randomUUID(), new BigDecimal("0.1"));
        paymentsService.addAccount(origin);
        Transaction transaction = new Transaction(new BigDecimal("0.1"), origin.getId(), UUID.randomUUID());

        assertThrows(AccountNotFoundException.class, () -> paymentsService.transfer(transaction));
    }

    @Test
    void shouldNotAddAccountToDatastoreIfItAlreadyExists() {
        Account account = new Account(UUID.randomUUID(), new BigDecimal("1000.0"));
        Account duplicatedAccount = new Account(account.getId(), new BigDecimal("0.0"));
        paymentsService.addAccount(account);

        assertThrows(AccountAlreadyExistsException.class, () -> paymentsService.addAccount(duplicatedAccount));
    }

    @Test
    void shouldNotFetchDataOfNonExistingAccount() {
        assertThrows(AccountNotFoundException.class, () -> paymentsService.fetchAccount(UUID.randomUUID()));
    }
}