package com.db.awmd.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.MoneyTransferRepository;

import lombok.Getter;

@Service
public class MoneyTransferService {

	@Getter
	  private final MoneyTransferRepository moneyTransferRepository;

	  @Autowired
	  public MoneyTransferService(MoneyTransferRepository moneyTransferRepository) {
	    this.moneyTransferRepository = moneyTransferRepository;
	  }
	
	  public boolean transferMoney(Account fromAccount , Account toAccount , BigDecimal transferAmount) {
		    return this.moneyTransferRepository.transferMoney(fromAccount,toAccount,transferAmount);
		  }
}
