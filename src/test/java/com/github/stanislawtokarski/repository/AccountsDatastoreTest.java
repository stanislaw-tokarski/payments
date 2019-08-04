package com.github.stanislawtokarski.repository;

import com.github.stanislawtokarski.exception.AccountNotFoundException;
import com.github.stanislawtokarski.exception.NotEnoughFundsException;
import com.github.stanislawtokarski.model.Account;
import com.github.stanislawtokarski.model.Transaction;
import com.github.stanislawtokarski.service.PaymentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountsDatastoreTest {

    private final int ACCOUNTS_NUMBER = 1000;
    private final int TRANSACTIONS_NUMBER = 1000000;
    private final BigDecimal INITIAL_ACCOUNT_BALANCE = new BigDecimal(100);
    private final UUID[] accountsIds = new UUID[ACCOUNTS_NUMBER];

    private AccountsDatastore accountsDatastore = new AccountsDatastore();
    private PaymentsService paymentsService = new PaymentsService(accountsDatastore);
    private Random random = new Random();

    @BeforeEach
    void setup() {
        IntStream
                .range(0, ACCOUNTS_NUMBER)
                .forEach(index -> {
                    final UUID id = UUID.randomUUID();
                    accountsIds[index] = id;
                    paymentsService.addAccount(new Account(id, INITIAL_ACCOUNT_BALANCE));
                });
    }

    @Test
    void shouldExecutePaymentsConcurrently() throws InterruptedException {
        //given
        final BigDecimal initialFundsAmount = sumUpFundsOnAllAccounts();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        //when
        executorService.invokeAll(prepareRandomPayments());

        //then
        final BigDecimal fundsAmountAfterPayments = sumUpFundsOnAllAccounts();
        assertEquals(initialFundsAmount, fundsAmountAfterPayments);
    }

    private List<Callable<Void>> prepareRandomPayments() {
        List<Callable<Void>> randomPayments = new ArrayList<>(TRANSACTIONS_NUMBER);

        IntStream.range(0, TRANSACTIONS_NUMBER)
                .forEach(action -> randomPayments.add(() -> {
                    Transaction transaction = new Transaction(
                            new BigDecimal(random.nextInt(9) + 1),
                            accountsIds[random.nextInt(ACCOUNTS_NUMBER)],
                            accountsIds[random.nextInt(ACCOUNTS_NUMBER)]);
                    try {
                        paymentsService.transfer(transaction);
//                        System.out.println(String.format("%s: Transferred %s from %s to %s",
//                                Thread.currentThread().getName(),
//                                transaction.getAmount(),
//                                transaction.getOriginAccountId(),
//                                transaction.getDestinationAccountId()));
                    } catch (NotEnoughFundsException | AccountNotFoundException e) {
//                        System.out.println(String.format("%s: %s",
//                                Thread.currentThread().getName(),
//                                e.getMessage()));
                    }
                    return null;
                }));

        return randomPayments;
    }

    private BigDecimal sumUpFundsOnAllAccounts() {
        return accountsDatastore
                .getAccounts()
                .values()
                .stream()
                .map(Account::getBalance)
                .reduce(BigDecimal::add)
                .get();
    }
}