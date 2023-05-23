package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.MoneyTransferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class MoneyTransferService {

    public void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount) {

        // Validate the input parameters
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyTransferException("Incorrect amount!");
        }

        if (fromAccount.getAccountId().compareTo(toAccount.getAccountId()) == 0) {
            throw new MoneyTransferException("Accounts must be different!");
        }

        log.info("Money transfer started: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());

        // Sort accounts by account ID to avoid deadlocks
        Account account1 =null;
        Account account2 =null;

        if (fromAccount.getAccountId().compareTo(toAccount.getAccountId()) > 0) {
            account1 = fromAccount;
            account2 = toAccount;
        } else {
            account1 = toAccount;
            account2 = fromAccount;
        }

        // Lock accounts in order to avoid concurrent access
        synchronized (account1) {
            synchronized (account2) {
                BigDecimal fromAccountBalance = fromAccount.getBalance();
                if (fromAccountBalance.compareTo(amount) < 0) {
                    throw new MoneyTransferException("Insufficient balance!");
                }
                fromAccount.setBalance(fromAccountBalance.subtract(amount));

                BigDecimal toAccountBalance = toAccount.getBalance();
                toAccount.setBalance(toAccountBalance.add(amount));
            }
        }

        log.info("Money transfer finished: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
    }
}
