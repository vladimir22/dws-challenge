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

    private Lock lock = new ReentrantLock();


    public void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new MoneyTransferException("Incorrect amount!");
        }

        lock.lock();
        try {
            BigDecimal fromAccountBalance = fromAccount.getBalance();
            if (fromAccountBalance.compareTo(amount) < 0) {
                throw new MoneyTransferException("Insufficient balance!");
            }

            BigDecimal toAccountBalance = toAccount.getBalance();

            fromAccount.setBalance(fromAccountBalance.subtract(amount));
            toAccount.setBalance(toAccountBalance.add(amount));

            log.info("Transferred amount '{}' from account {} to account {}", amount, fromAccount.getAccountId(), toAccount.getAccountId());
        } finally {
            lock.unlock();
        }

    }
}
