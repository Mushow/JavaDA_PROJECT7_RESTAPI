package com.nnk.springboot.unit;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.BidListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class BidTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private BidListService service;

	@BeforeEach
	public void setupAuth() {
		CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	@WithMockUser(username="user1")
	public void shouldListBids() throws Exception {
		BidList firstBid = new BidList();
		firstBid.setBidListId(1);
		firstBid.setAccount("Account #1");
		firstBid.setType("Type #1");
		firstBid.setBidQuantity(5.2);

		BidList secondBid = new BidList();
		secondBid.setBidListId(2);
		secondBid.setAccount("Account #2");
		secondBid.setType("Type #2");
		secondBid.setBidQuantity(5.2);

		List<BidList> allBids = new ArrayList<>(Arrays.asList(firstBid, secondBid));

		Mockito.when(service.findAll()).thenReturn(allBids);

		MvcResult response = mvc.perform(get("/bidList/list"))
				.andExpect(status().isOk())
				.andReturn();

		String responseBody = response.getResponse().getContentAsString();

		Assertions.assertTrue(responseBody.contains("Account #1"));
		Assertions.assertTrue(responseBody.contains("Account #2"));
		Assertions.assertTrue(responseBody.contains("Type #1"));
		Assertions.assertTrue(responseBody.contains("Type #2"));

		Mockito.verify(service, Mockito.times(1)).findAll();
	}

	@Test
	public void shouldNavigateToAddBidForm() throws Exception {
		mvc.perform(get("/bidList/add"))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldShowErrorsOnInvalidBidSave() throws Exception {
		BidList invalidBid = new BidList();

		MvcResult result = mvc.perform(post("/bidList/validate").flashAttr("bidList", invalidBid))
				.andExpect(status().isOk())
				.andReturn();

		String body = result.getResponse().getContentAsString();

		Assertions.assertTrue(body.contains("Account is mandatory."));
		Assertions.assertTrue(body.contains("Type is mandatory."));

		Mockito.verify(service, Mockito.never()).save(any());
	}

	@Test
	public void shouldSaveValidBid() throws Exception {
		BidList validBid = new BidList();
		validBid.setAccount("Account #1");
		validBid.setType("Type #1");
		validBid.setBidQuantity(5.2);

		Mockito.when(service.save(any())).thenReturn(null);

		mvc.perform(post("/bidList/validate").flashAttr("bidList", validBid))
				.andExpect(redirectedUrl("/bidList/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void shouldHandleUpdateBidNotFound() throws Exception {
		Mockito.when(service.findById(anyInt())).thenReturn(Optional.empty());

		mvc.perform(get("/bidList/update/{id}", 1))
				.andExpect(redirectedUrl("/bidList/list?error=true"));

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void shouldUpdateBid() throws Exception {
		BidList bidToUpdate = new BidList();
		bidToUpdate.setBidListId(1);
		bidToUpdate.setAccount("Account #1");
		bidToUpdate.setType("Type #1");
		bidToUpdate.setBidQuantity(5.2);

		Mockito.when(service.findById(anyInt())).thenReturn(Optional.of(bidToUpdate));

		mvc.perform(get("/bidList/update/{id}", 1))
				.andExpect(status().isOk());

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void shouldShowUpdateErrorOnInvalidData() throws Exception {
		BidList invalidUpdateBid = new BidList();
		invalidUpdateBid.setBidListId(1);

		MvcResult result = mvc.perform(post("/bidList/update/{id}", 1).flashAttr("bidList", invalidUpdateBid))
				.andExpect(status().isOk())
				.andReturn();

		String body = result.getResponse().getContentAsString();

		Assertions.assertTrue(body.contains("Account is mandatory."));
		Assertions.assertTrue(body.contains("Type is mandatory."));

		Mockito.verify(service, Mockito.never()).save(any());
	}

	@Test
	public void shouldDeleteBid() throws Exception {
		Mockito.doNothing().when(service).deleteById(anyInt());

		mvc.perform(get("/bidList/delete/{id}", 1))
				.andExpect(redirectedUrl("/bidList/list"));

		Mockito.verify(service, Mockito.times(1)).deleteById(anyInt());
	}
}
