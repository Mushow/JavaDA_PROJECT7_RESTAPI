package com.nnk.springboot.unit;

import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.TradeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")

public class TradeTests {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TradeService service;

	@BeforeEach
	public void setupAuth() {
		CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void displayAllTrades() throws Exception {
		Trade firstTrade = new Trade();
		firstTrade.setTradeId(1);
		firstTrade.setAccount("Account Test");
		firstTrade.setType("Type Test");
		firstTrade.setBuyQuantity(100.0);

		Trade secondTrade = new Trade();
		secondTrade.setTradeId(2);
		secondTrade.setAccount("Account Test 2");
		secondTrade.setType("Type Test 2");
		secondTrade.setBuyQuantity(200.0);

		List<Trade> allTrades = new ArrayList<>(Arrays.asList(firstTrade, secondTrade));

		Mockito.when(service.findAll()).thenReturn(allTrades);

		MvcResult response = mockMvc.perform(get("/trade/list"))
				.andExpect(status().isOk()).andReturn();

		String responseBody = response.getResponse().getContentAsString();

		Assertions.assertTrue(responseBody.contains("Account Test"));
		Assertions.assertTrue(responseBody.contains("Account Test 2"));
		Assertions.assertTrue(responseBody.contains("Type Test"));
		Assertions.assertTrue(responseBody.contains("Type Test 2"));

		Mockito.verify(service, Mockito.times(1)).findAll();
	}

	@Test
	public void initiateTradeAddition() throws Exception {
		mockMvc.perform(get("/trade/add"))
				.andExpect(status().isOk());
	}

	@Test
	public void validateTradeCreationWithError() throws Exception {
		Trade incompleteTrade = new Trade();

		MvcResult result = mockMvc.perform(post("/trade/validate").flashAttr("trade", incompleteTrade))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Account is mandatory"));
		Assertions.assertTrue(content.contains("Type is mandatory"));
		Assertions.assertTrue(content.contains("Buy Quantity is mandatory"));

		Mockito.verify(service, Mockito.times(0)).save(any());
	}

	@Test
	public void successfullyValidateTradeCreation() throws Exception {
		Trade validTrade = new Trade();
		validTrade.setAccount("Account Test");
		validTrade.setType("Type Test");
		validTrade.setBuyQuantity(100.0);

		Mockito.when(service.save(any())).thenReturn(null);

		mockMvc.perform(post("/trade/validate").flashAttr("trade", validTrade))
				.andExpect(redirectedUrl("/trade/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void errorDuringTradeUpdate() throws Exception {
		Mockito.when(service.findById(anyInt())).thenReturn(Optional.empty());

		mockMvc.perform(get("/trade/update/{id}", 1))
				.andExpect(redirectedUrl("/trade/list?error=true"));

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void updateTradeDetails() throws Exception {
		Trade existingTrade = new Trade();
		existingTrade.setTradeId(1);
		existingTrade.setAccount("Account Test");
		existingTrade.setType("Type Test");
		existingTrade.setBuyQuantity(100.0);

		Mockito.when(service.findById(anyInt())).thenReturn(Optional.of(existingTrade));

		mockMvc.perform(get("/trade/update/{id}", 1))
				.andExpect(status().isOk());

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void validateTradeUpdateWithError() throws Exception {
		Trade invalidTrade = new Trade();
		invalidTrade.setTradeId(1);

		MvcResult result = mockMvc.perform(post("/trade/update/{id}", 1).flashAttr("trade", invalidTrade))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Account is mandatory"));
		Assertions.assertTrue(content.contains("Type is mandatory"));
		Assertions.assertTrue(content.contains("Buy Quantity is mandatory"));

		Mockito.verify(service, Mockito.times(0)).save(any());
	}

	@Test
	public void confirmTradeUpdate() throws Exception {
		Trade updatedTrade = new Trade();
		updatedTrade.setTradeId(1);
		updatedTrade.setAccount("Account Test");
		updatedTrade.setType("Type Test");
		updatedTrade.setBuyQuantity(100.0);

		Mockito.when(service.save(any())).thenReturn(null);

		mockMvc.perform(post("/trade/update/{id}", 1).flashAttr("trade", updatedTrade))
				.andExpect(redirectedUrl("/trade/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void removeTrade() throws Exception {
		Mockito.doNothing().when(service).deleteById(anyInt());

		mockMvc.perform(get("/trade/delete/{id}", 1))
				.andExpect(redirectedUrl("/trade/list"));

		Mockito.verify(service, Mockito.times(1)).deleteById(anyInt());
	}
}
