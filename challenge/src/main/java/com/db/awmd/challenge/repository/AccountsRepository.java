package com.db.awmd.challenge.repository;

import java.math.BigDecimal;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountMoneyTransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);
  
  void clearAccounts();
  
//Code added for Dev challege
  boolean transferMoney(Account fromAccount , Account toAccount , BigDecimal transferAmount);

}
