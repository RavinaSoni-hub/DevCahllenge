package com.db.awmd.challenge.repository;

import java.math.BigDecimal;

import com.db.awmd.challenge.domain.Account;

public interface MoneyTransferRepository {

	//Code added for Dev challege
	  boolean transferMoney(Account fromAccount , Account toAccount , BigDecimal transferAmount);

}
