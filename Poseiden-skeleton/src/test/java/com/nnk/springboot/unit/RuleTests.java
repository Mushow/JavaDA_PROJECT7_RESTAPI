package com.nnk.springboot.unit;

import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.RuleNameService;
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
public class RuleTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private RuleNameService service;

	@BeforeEach
	public void setupAuth() {
		CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void shouldListAllRuleNames() throws Exception {
		RuleName ruleNameOne = new RuleName(1, "Name Test", "Description Test", "Json Test", "Template Test", "SQl String test", "SQL Part test");
		RuleName ruleNameTwo = new RuleName(2, "Name Test 2", "Description Test 2", "Json Test 2", "Template Test 2", "SQl String test 2", "SQL Part test 2");

		List<RuleName> ruleNames = new ArrayList<>(Arrays.asList(ruleNameOne, ruleNameTwo));

		Mockito.when(service.findAll()).thenReturn(ruleNames);

		MvcResult response = mockMvc.perform(get("/ruleName/list"))
				.andExpect(status().isOk())
				.andReturn();

		String responseBody = response.getResponse().getContentAsString();

		Assertions.assertTrue(responseBody.contains("Name Test"));
		Assertions.assertTrue(responseBody.contains("Name Test 2"));
		Assertions.assertTrue(responseBody.contains("Description Test"));
		Assertions.assertTrue(responseBody.contains("Description Test 2"));

		Mockito.verify(service, Mockito.times(1)).findAll();
	}

	@Test
	public void shouldOpenAddRuleNameForm() throws Exception {
		mockMvc.perform(get("/ruleName/add"))
				.andExpect(status().isOk());
	}

	@Test
	public void shouldShowValidationErrorOnRuleSave() throws Exception {
		RuleName invalidRuleName = new RuleName();

		MvcResult result = mockMvc.perform(post("/ruleName/validate").flashAttr("ruleName", invalidRuleName))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Name is mandatory"));
		Assertions.assertTrue(content.contains("Description is mandatory"));
		Assertions.assertTrue(content.contains("Json is mandatory"));
		Assertions.assertTrue(content.contains("Template is mandatory"));
		Assertions.assertTrue(content.contains("SQL string is mandatory"));
		Assertions.assertTrue(content.contains("SQL part is mandatory"));

		Mockito.verify(service, Mockito.times(0)).save(any());
	}

	@Test
	public void shouldValidateAndRedirectAfterRuleSave() throws Exception {
		RuleName validRuleName = new RuleName(1, "Name Test", "Description Test", "Json Test", "Template Test", "SQl String test", "SQL Part test");

		Mockito.when(service.save(any())).thenReturn(null);

		mockMvc.perform(post("/ruleName/validate").flashAttr("ruleName", validRuleName))
				.andExpect(redirectedUrl("/ruleName/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void shouldHandleNotFoundWhenUpdatingRuleName() throws Exception {
		Mockito.when(service.findById(anyInt())).thenReturn(Optional.empty());

		mockMvc.perform(get("/ruleName/update/{id}", 1))
				.andExpect(redirectedUrl("/ruleName/list?error=true"));

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void shouldUpdateRuleName() throws Exception {
		RuleName existingRuleName = new RuleName(1, "Name Test", "Description Test", "Json Test", "Template Test", "SQl String test", "SQL Part test");

		Mockito.when(service.findById(anyInt())).thenReturn(Optional.of(existingRuleName));

		mockMvc.perform(get("/ruleName/update/{id}", 1))
				.andExpect(status().isOk());

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void shouldShowUpdateValidationError() throws Exception {
		RuleName incompleteRuleName = new RuleName();
		incompleteRuleName.setId(1);

		MvcResult result = mockMvc.perform(post("/ruleName/update/{id}", 1).flashAttr("ruleName", incompleteRuleName))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Name is mandatory"));
		Assertions.assertTrue(content.contains("Description is mandatory"));
		Assertions.assertTrue(content.contains("Json is mandatory"));
		Assertions.assertTrue(content.contains("Template is mandatory"));
		Assertions.assertTrue(content.contains("SQL string is mandatory"));
		Assertions.assertTrue(content.contains("SQL part is mandatory"));

		Mockito.verify(service, Mockito.times(0)).save(any());
	}

	@Test
	public void shouldUpdateAndRedirectRuleName() throws Exception {
		RuleName completeRuleName = new RuleName(1, "Name Test", "Description Test", "Json Test", "Template Test", "SQl String test", "SQL Part test");

		Mockito.when(service.save(any())).thenReturn(null);

		mockMvc.perform(post("/ruleName/update/{id}", 1).flashAttr("ruleName", completeRuleName))
				.andExpect(redirectedUrl("/ruleName/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void shouldDeleteRuleName() throws Exception {
		Mockito.doNothing().when(service).deleteById(anyInt());

		mockMvc.perform(get("/ruleName/delete/{id}", 1))
				.andExpect(redirectedUrl("/ruleName/list"));

		Mockito.verify(service, Mockito.times(1)).deleteById(anyInt());
	}
}
