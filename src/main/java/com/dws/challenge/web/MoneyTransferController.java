package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.MoneyTransferException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.MoneyTransferService;
import com.dws.challenge.service.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/transfer")
@Slf4j
@AllArgsConstructor
public class MoneyTransferController {

    public static final String AMOUNT_TRANSFERRED_SUCCESSFULLY = "Amount transferred successfully!";
    public static final String TRANSFERRED_AMOUNT_MESSAGE = "Transferred amount %s to account %s";
    public static final String RECEIVED_AMOUNT_MESSAGE = "Received amount %s from account %s";

    private final MoneyTransferService moneyTransferService;
    private final NotificationService notificationService;
    private final AccountsService accountsService;

    @PostMapping
    public ResponseEntity<String> transfer(@RequestParam @NotEmpty String fromAccountId,
                                                 @RequestParam @NotEmpty String toAccountId,
                                                 // N.B.: as I know amount is better to represent as BigInteger because floating point numbers are not precise
                                                 @RequestParam @Min(0) BigDecimal amount) {

        // Get the accounts
        Account fromAccount = accountsService.getAccount(fromAccountId);
        if (fromAccount == null) {
            return ResponseEntity.badRequest().body("Account with id " + fromAccountId + " does not exist");
        }
        Account toAccount = accountsService.getAccount(toAccountId);
        if (toAccount == null) {
            return ResponseEntity.badRequest().body("Account with id " + toAccountId + " does not exist");
        }

        // Transfer the amount
        moneyTransferService.transferMoney(fromAccount, toAccount, amount);

        // Notify the accounts, if notification fails, log the error and continue
        try {
            notificationService.notifyAboutTransfer(fromAccount, String.format(TRANSFERRED_AMOUNT_MESSAGE,amount, toAccount));
        }  catch (Exception e) {
            log.error("Notification error while transferring from account {} about the transfer", fromAccountId, e);
        }
        try {
            notificationService.notifyAboutTransfer(toAccount,  String.format(RECEIVED_AMOUNT_MESSAGE,amount, fromAccount));
        } catch (Exception e) {
            log.error("Notification error while transferring to account {} about the transfer", toAccountId, e);
        }

        return ResponseEntity.ok(AMOUNT_TRANSFERRED_SUCCESSFULLY);
    }

    @ExceptionHandler(MoneyTransferException.class)
    public ResponseEntity<String> handleExceptions(MoneyTransferException e)
    {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

}
