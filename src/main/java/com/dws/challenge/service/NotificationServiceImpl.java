package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Current class out of scope of the current task. It is needed for testing HTTP Rest client github action.
 */
@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService{
    @Override
    public void notifyAboutTransfer(Account account, String transferDescription) {
        log.info("Notification about transfer: account={}, transferDescription={}", account, transferDescription);
    }
}
