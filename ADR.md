### Account:

### Blockchain Transactions:
* Made two tables `AdminTransaction` and `UserTransaction` instead of individual table for each transaction (15 tx) -
  * [PRO] Fetching Tx history for an account sorted on time is far less complex when fetching from 15 tables (in other case)
  * [PRO] Code maintenance is hugely improved as only 2 schedulers needs to be run instead of 15.
  * [CON] Tx type needs to be stored.
* `AdminTransaction` and `UserTransaction` 2 different tables as they might get slightly different logic in scheduler.
