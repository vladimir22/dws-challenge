package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.MoneyTransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class MoneyTransferServiceTest {

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private MoneyTransferService moneyTransferService;

    @MockBean
    private NotificationService notificationService;

    @BeforeEach
    void clearAccounts() {
        // Reset the existing accounts before each test.
        accountsService.getAccountsRepository().clearAccounts();
    }

    @ParameterizedTest
    @CsvSource({"1000,1000,1000", "1000,0,1000", "1000,1000,1"})
    void testTransferMoney_AllowedParams(BigDecimal fromAccountBalance, BigDecimal toAccountBalance, BigDecimal amount) {

        // Create accounts
        Account fromAccount = new Account("Id-123");
        fromAccount.setBalance(fromAccountBalance);
        accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-456");
        toAccount.setBalance(toAccountBalance);
        accountsService.createAccount(toAccount);

        // Transfer the amount
        moneyTransferService.transferMoney(fromAccount, toAccount, amount);

        // Check the balances
        assertEquals(fromAccount.getBalance(), fromAccountBalance.subtract(amount));
        assertEquals(toAccount.getBalance(), toAccountBalance.add(amount));
    }

    @ParameterizedTest
    @CsvSource({"0,0,-1", "0,1000,1000", "1000,1000,0", "1000,1000,10000"})
    void testTransferMoney_NotAllowedParams(BigDecimal fromAccountBalance, BigDecimal toAccountBalance, BigDecimal amount) {

        // Create accounts
        Account fromAccount = new Account("Id-123");
        fromAccount.setBalance(fromAccountBalance);
        accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-456");
        toAccount.setBalance(toAccountBalance);
        accountsService.createAccount(toAccount);

        // Transfer the amount throws exception
        assertThrows(MoneyTransferException.class, () -> moneyTransferService.transferMoney(fromAccount, toAccount, amount));
    }

    @ParameterizedTest
    @CsvSource({"1000,1000,1,1000"})
    void testTransferMoney_ConcurrentTransfer(BigDecimal fromAccountBalance, BigDecimal toAccountBalance, BigDecimal amount, Integer numberOfTreads) throws InterruptedException {

        // Create accounts
        Account fromAccount = new Account("Id-123");
        fromAccount.setBalance(fromAccountBalance);
        accountsService.createAccount(fromAccount);

        Account toAccount = new Account("Id-456");
        toAccount.setBalance(toAccountBalance);
        accountsService.createAccount(toAccount);

        // Submit forward money transfer requests in parallel
        CountDownLatch latch = new CountDownLatch(numberOfTreads * 2);
        ThreadPoolExecutor forwardExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfTreads);
        for (int i = 0; i < numberOfTreads; i++) {
            forwardExecutor.submit(() -> {
                moneyTransferService.transferMoney(fromAccount, toAccount, amount);
                latch.countDown();
            });
        }

        // Submit backward money transfer requests in parallel
        ThreadPoolExecutor backwardExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numberOfTreads);
        for (int i = 0; i < numberOfTreads; i++) {
            backwardExecutor.submit(() -> {
                moneyTransferService.transferMoney(toAccount, fromAccount, amount);
                latch.countDown();
            });
        }

        // Wait for all threads to finish
        forwardExecutor.shutdown();
        backwardExecutor.shutdown();
        assertTrue(latch.await(numberOfTreads, java.util.concurrent.TimeUnit.SECONDS));

        // Final balances equals to initial balances
        assertEquals(fromAccountBalance, fromAccount.getBalance(), "fromAccount balance is not equal to the initial (fromAccount.getBalance()=" + fromAccount.getBalance() + ", toAccount.getBalance()=" + toAccount.getBalance() + ")");
        assertEquals(toAccountBalance, toAccount.getBalance(), "toAccount balance is not equal to the initial (fromAccount.getBalance()=" + fromAccount.getBalance() + ", toAccount.getBalance()=" + toAccount.getBalance() + ")");
    }

}