package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private AccountsService accountsService;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Before
	public void prepareMockMvc() {
		this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

		// Reset the existing accounts before each test.
		accountsService.getAccountsRepository().clearAccounts();
	}

	@Test
	public void createAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		Account account = accountsService.getAccount("Id-123");
		assertThat(account.getAccountId()).isEqualTo("Id-123");
		assertThat(account.getBalance()).isEqualByComparingTo("1000");
	}

	@Test
	public void createDuplicateAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"balance\":1000}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBalance() throws Exception {
		this.mockMvc.perform(
				post("/v1/accounts").contentType(MediaType.APPLICATION_JSON).content("{\"accountId\":\"Id-123\"}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNoBody() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountNegativeBalance() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":-1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void createAccountEmptyAccountId() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
	}

	@Test
	public void getAccount() throws Exception {
		String uniqueAccountId = "Id-" + System.currentTimeMillis();
		Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
		this.accountsService.createAccount(account);
		this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId)).andExpect(status().isOk())
				.andExpect(content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
	}

	// Code added for Dev Challenge
	@Test
	public void transferMoney() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-345\",\"balance\":1000}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-345\",\"transferAmount\":100}"))
				.andExpect(status().isOk());

		Account fromAccount = accountsService.getAccount("Id-123");
		assertThat(fromAccount.getBalance()).isEqualTo("900");
		Account toAccount = accountsService.getAccount("Id-345");
		assertThat(toAccount.getBalance()).isEqualTo("1100");

	}

	@Test
	public void transferMoneyOverDraft() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-789\",\"balance\":200}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-345\",\"balance\":500}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\":\"Id-789\",\"accountToId\":\"Id-345\",\"transferAmount\":300}"))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void transferMoneyNoToAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-789\",\"balance\":200}")).andExpect(status().isCreated());

		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-345\",\"balance\":500}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\":\"Id-789\",\"transferAmount\":300}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void transferMoneyAccountNoAmount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-567\",\"balance\":200}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\":\"Id-567\",\"accountToId\":\"Id-341\"}"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferMoneyAccountNofromAccount() throws Exception {
		this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
				.content("{\"accountId\":\"Id-567\",\"balance\":200}")).andExpect(status().isCreated());

		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountToId\":\"Id-341\"}"))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	public void transferMoneyAccountNotExist() throws Exception {
		
		this.mockMvc
				.perform(put("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
						.content("{\"accountFromId\":\"Id-891\",\"accountToId\":\"Id-141\",\"transferAmount\":300}"))
				.andExpect(status().isInternalServerError());
		
	}
}
