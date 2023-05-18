# DWS Challenge
Given simple REST service with some very basic functionality - to add and read an
account.

The task is to *add functionality for a transfer of money between accounts*. Transfers should be
specified by providing:
- accountFrom id
- accountTo id
- amount to transfer between accounts
The amount to transfer should always be a positive number. It should not be possible for an account to end
up with negative balance (we do not support overdrafts!)
Whenever a transfer is made, a notification should be sent to both account holders, with a message
containing id of the other account and amount transferred.
- For this purpose please use the NotificationService interface
- Do NOT provide implementation for this service - it is assumed another colleague would implement it.
- Do not use the provided (simple) implementation in your tests - it is provided for the main application
to run. In your tests you should mock this service.


## Task Implementation Details

- Endpoint implementation in the [com.dws.challenge.web.MoneyTransferController](./src/main/java/com/dws/challenge/web/MoneyTransferController.java) class which is covered by [MoneyTransferControllerTest](./src/test/java/com/dws/challenge/web/MoneyTransferControllerTest.java)

- Service implementation in the [com.dws.challenge.service.MoneyTransferService](./src/main/java/com/dws/challenge/service/MoneyTransferService.java) which is covered by [MoneyTransferControllerTest](./src/test/java/com/dws/challenge/web/MoneyTransferControllerTest.java)







