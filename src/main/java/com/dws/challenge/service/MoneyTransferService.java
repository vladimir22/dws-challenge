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

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyTransferException("Incorrect amount!");
        }

        log.info("Money transfer started: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());

        synchronized (fromAccount) {
            BigDecimal fromAccountBalance = fromAccount.getBalance();
            if (fromAccountBalance.compareTo(amount) < 0) {
                throw new MoneyTransferException("Insufficient balance!");
            }
            fromAccount.setBalance(fromAccountBalance.subtract(amount));
            log.info("Withdraw money: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
        }

        synchronized (toAccount) {
            BigDecimal toAccountBalance = toAccount.getBalance();
            toAccount.setBalance(toAccountBalance.add(amount));
            log.info("Deposit money: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
        }

        log.info("Money transfer finished: amount='{}', fromAccount={}, toAccount={}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
    }
}
