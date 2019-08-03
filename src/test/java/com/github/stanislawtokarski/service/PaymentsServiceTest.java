package com.github.stanislawtokarski.service;

import com.github.stanislawtokarski.exception.AccountAlreadyExistsException;
import com.github.stanislawtokarski.exception.AccountNotFoundException;
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
    void shouldCorrectlyPerformMoneyTransfer() {
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
    void shouldNotTransferMoneyWhenAccountBalanceIsInsufficient() {
        //given
        Account origin = new Account(UUID.randomUUID(), new BigDecimal("100.0"));
        Account destination = new Account(UUID.randomUUID(), new BigDecimal("10.0"));
        paymentsService.addAccount(origin);
        paymentsService.addAccount(destination);
        Transaction transaction = new Transaction(new BigDecimal("110.0"), origin.getId(), destination.getId());

        //when
        paymentsService.transfer(transaction);

        //then
        Account originAfterTransfer = paymentsService.fetchAccount(origin.getId());
        Account destinationAfterTransfer = paymentsService.fetchAccount(destination.getId());
        assertEquals(originAfterTransfer.getBalance(), new BigDecimal("100.0"));
        assertEquals(destinationAfterTransfer.getBalance(), new BigDecimal("10.0"));
    }

    @Test
    void shouldNotTransferMoneyFromNonExistingAccount() {
        //given
        Account destination = new Account(UUID.randomUUID(), new BigDecimal("0.1"));
        paymentsService.addAccount(destination);
        Transaction transaction = new Transaction(new BigDecimal("0.1"), UUID.randomUUID(), destination.getId());

        //when

        //then
        assertThrows(AccountNotFoundException.class, () -> paymentsService.transfer(transaction));
    }

    @Test
    void shouldNotTransferMoneyToNonExistingAccount() {
        //given
        Account origin = new Account(UUID.randomUUID(), new BigDecimal("0.1"));
        paymentsService.addAccount(origin);
        Transaction transaction = new Transaction(new BigDecimal("0.1"), origin.getId(), UUID.randomUUID());

        //when

        //then
        assertThrows(AccountNotFoundException.class, () -> paymentsService.transfer(transaction));
    }

    @Test
    void shouldNotAddAccountToDatastoreIfItAlreadyExists() {
        //given
        Account account = new Account(UUID.randomUUID(), new BigDecimal("1000.0"));
        Account duplicatedAccount = new Account(account.getId(), new BigDecimal("0.0"));
        paymentsService.addAccount(account);

        //when

        //then
        assertThrows(AccountAlreadyExistsException.class, () -> paymentsService.addAccount(duplicatedAccount));
    }

    @Test
    void shouldNotFetchDataOfNonExistingAccount() {
        //given

        //when

        //then
        assertThrows(AccountNotFoundException.class, () -> paymentsService.fetchAccount(UUID.randomUUID()));
    }
}