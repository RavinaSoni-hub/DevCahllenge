package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountMoneyTransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.OverdraftsAccountException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

	private final Map<String, Account> accounts = new ConcurrentHashMap<>();

	@Override
	public void createAccount(Account account) throws DuplicateAccountIdException {
		Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
		if (previousAccount != null) {
			throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
		}
	}

	@Override
	public Account getAccount(String accountId) {
		return accounts.get(accountId);
	}

	@Override
	public void clearAccounts() {
		accounts.clear();
	}

	// Code added for Dev challege
	@Override
	public synchronized boolean transferMoney(Account fromAccount, Account toAccount, BigDecimal transferAmount) {

		if (fromAccount.getBalance().subtract(transferAmount).compareTo(BigDecimal.ZERO) > 0) {
			fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
			toAccount.setBalance(toAccount.getBalance().add(transferAmount));
			return true;
		} else
			throw new OverdraftsAccountException("Transfer of " + transferAmount + " not possible ,"
					+ fromAccount.getAccountId() + " going overdafts ");

	}

}
