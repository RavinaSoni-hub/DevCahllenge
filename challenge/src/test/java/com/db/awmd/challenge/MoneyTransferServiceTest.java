package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.OverdraftsAccountException;
import com.db.awmd.challenge.service.MoneyTransferService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MoneyTransferServiceTest {

	@Autowired
	private MoneyTransferService moneyTransferService;

	@Test
	public void transferMoney() throws Exception {
		Account fromAccount = new Account("Id-123");
		fromAccount.setBalance(new BigDecimal(1000));
		Account toAccount = new Account("Id-123");
		toAccount.setBalance(new BigDecimal(1000));
		this.moneyTransferService.transferMoney(fromAccount, toAccount, new BigDecimal(300));

		assertThat(fromAccount.getBalance()).isEqualTo("700");
		assertThat(toAccount.getBalance()).isEqualTo("1300");
	}

	@Test
	public void addAccount_failsOnDuplicateId() throws Exception {
		Account fromAccount = new Account("Id-123");
		fromAccount.setBalance(new BigDecimal(100));
		Account toAccount = new Account("Id-123");
		toAccount.setBalance(new BigDecimal(1000));
		try {
			this.moneyTransferService.transferMoney(fromAccount, toAccount, new BigDecimal(300));
		} catch (OverdraftsAccountException oae) {
			assertThat(oae.getMessage()).contains("overdafts");
		}
	}

}
