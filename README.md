# DWS Challenge
## Task Description
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

- Endpoint implementation in the [MoneyTransferController.java](./src/main/java/com/dws/challenge/web/MoneyTransferController.java) class which is covered by tests in the [MoneyTransferControllerTest](./src/test/java/com/dws/challenge/web/MoneyTransferControllerTest.java)

- Service implementation in the [MoneyTransferService.java](./src/main/java/com/dws/challenge/service/MoneyTransferService.java) which is covered by tests in the [MoneyTransferControllerTest.java](./src/test/java/com/dws/challenge/service/MoneyTransferServiceTest.java)


## How To Run

Application cannot be started because NotificationService interface is *not* implemented, but it is possible to build application and run tests with mocked NotificationService bean.

Example:

```sh
git clone https://github.com/vladimir22/dws-challenge.git
cd dws-challenge
gradle build
gradle test
```


## TODO
Before deploying this project into PROD I would pay attention on the next:
- Implement NotificationService to send notifications in async mode.
- Consider to avoid of using BigDecimal type for *amount* parameter because floating point numbers are not precise and may cause calculation problems.
- Extend API by adding endpoints to check current balance and get list of last transfers.
- Introduce HTTP response body in a JSON format.
- Introduce UUID per each money transfer to have better logging and clear API response.
- Enhance Exception Handling, add message localization.
- Implement Security.
- Enhance Logging.
- Use persistence (Transactional DB) instead of in-memory Map.

## Github Actions CI/CD
I had a chance to play with Github Actions CI/CD and created a simple workflow to build and test this project.

The [build-gradle.yaml](./.github/workflows/build-gradle.yaml) contains the workflow definition. All the results are available in the [Actions](https://github.com/vladimir22/dws-challenge/actions) tab.

The set of links below refer to examples of the test reports:
- SonarLint report is available [here](https://github.com/vladimir22/dws-challenge/actions/runs/5066895224/jobs/9097287259).
- Unit tests report is available [here](https://github.com/vladimir22/dws-challenge/actions/runs/5066895224/jobs/9097286593).
- Spotbugs report is available [here](https://github.com/vladimir22/dws-challenge/actions/runs/5066895224/jobs/9097287656).
- Intellij HTTP Client report is available [here](https://github.com/vladimir22/dws-challenge/actions/runs/5069408889/jobs/9102992064) (report example has been taken from the [PR-1](https://github.com/vladimir22/dws-challenge/pull/1) which contains implementation of *NotificationService* to run properly integration tests described in the [test-requests.http](https://github.com/vladimir22/dws-challenge/blob/main/HTTP/test-requests.http) file)






