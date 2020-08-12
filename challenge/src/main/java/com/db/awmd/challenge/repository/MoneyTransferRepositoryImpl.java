package com.db.awmd.challenge.repository;

import java.math.BigDecimal;

import org.springframework.stereotype.Repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.OverdraftsAccountException;

@Repository
public class MoneyTransferRepositoryImpl  implements MoneyTransferRepository{

	
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
