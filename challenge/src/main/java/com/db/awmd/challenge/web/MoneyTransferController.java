package com.db.awmd.challenge.web;

import java.math.BigDecimal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AccountMoneyTransferRequest;
import com.db.awmd.challenge.exception.OverdraftsAccountException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;
import com.db.awmd.challenge.service.MoneyTransferService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/accounts/transfer")
@Slf4j
public class MoneyTransferController {
	
	private final AccountsService accountsService;
	private final MoneyTransferService moneyTransferService;
	private final EmailNotificationService emailNotificationService;
	
	
	@Autowired
	public MoneyTransferController(AccountsService accountsService , MoneyTransferService moneyTransferService) {
		this.accountsService = accountsService;
		this.moneyTransferService = moneyTransferService;
		this.emailNotificationService = new EmailNotificationService();
	}

		@PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<Object> transferMoney(
				@RequestBody @Valid AccountMoneyTransferRequest accountMoneyTransferRequest) {

			log.info("Money transfer initiated from account:{} to account:{}",
					accountMoneyTransferRequest.getAccountFromId(), accountMoneyTransferRequest.getAccountToId());
			try {
				Account fromAccount = accountsService.getAccount(accountMoneyTransferRequest.getAccountFromId());
				Account toAccount = accountsService.getAccount(accountMoneyTransferRequest.getAccountToId());
				BigDecimal trasferAmount = accountMoneyTransferRequest.getTransferAmount();

				try {
					if (this.moneyTransferService.transferMoney(fromAccount, toAccount, trasferAmount)) {
						this.emailNotificationService.notifyAboutTransfer(fromAccount,
								trasferAmount + " amount transfered to " + toAccount.getAccountId());
						this.emailNotificationService.notifyAboutTransfer(toAccount,
								trasferAmount + " amount transfered from " + fromAccount.getAccountId());
						log.info("Money transfer completed sucessfully");
						return new ResponseEntity<>(HttpStatus.OK);
					}
				} catch (OverdraftsAccountException ode) {
					log.info("Overdraft!! Money transfer can not completed");
					return new ResponseEntity<>(ode.getMessage(), HttpStatus.BAD_REQUEST);
				}
			} catch (Exception e) {
				log.info("Money transfer can not completed");
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<>(HttpStatus.OK);
		}

}
