
package com.nnk.springboot.unit;

import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.CurvePointService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class CurvePointTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CurvePointService curvePointService;

	@BeforeEach
	public void setupAuth() {
		CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void curvePointListTest() throws Exception {
		CurvePoint curvePoint1 = new CurvePoint();
		curvePoint1.setId(1);
		curvePoint1.setTerm(2.0);
		curvePoint1.setValue(3.0);

		CurvePoint curvePoint2 = new CurvePoint();
		curvePoint2.setId(2);
		curvePoint2.setTerm(2.0);
		curvePoint2.setValue(3.0);

		List<CurvePoint> curvePoints =  new ArrayList<>(Arrays.asList(curvePoint1, curvePoint2));

		Mockito.when(curvePointService.findAll()).thenReturn(curvePoints);

		MvcResult resultActions = mockMvc.perform(get("/curvePoint/list")).andExpectAll(
				status().isOk()).andReturn();

		String content = resultActions.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("2.0"));
		Assertions.assertTrue(content.contains("3.0"));

		Mockito.verify(curvePointService, Mockito.times(1)).findAll();
	}

	@Test
	public void curvePointAddTest() throws Exception {
		mockMvc.perform(get("/curvePoint/add")).andExpectAll(
				status().isOk());
	}

	@Test
	public void curvePointValidateErrorTest() throws Exception {
		CurvePoint curvePoint = new CurvePoint();

		Mockito.when(curvePointService.save(any())).thenReturn(null);

		MvcResult resultActions = mockMvc.perform(post("/curvePoint/validate").flashAttr("curvePoint", curvePoint))
				.andExpect(status().isOk()).andReturn();

		String content = resultActions.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Term is mandatory"));
		Assertions.assertTrue(content.contains("Value is mandatory"));

		Mockito.verify(curvePointService, Mockito.times(0)).save(any());
	}

	@Test
	public void curvePointValidateTest() throws Exception {
		CurvePoint curvePoint1 = new CurvePoint();
		curvePoint1.setTerm(2.0);
		curvePoint1.setValue(3.0);

		Mockito.when(curvePointService.save(any())).thenReturn(null);

		mockMvc.perform(post("/curvePoint/validate").flashAttr("curvePoint", curvePoint1))
				.andExpect(redirectedUrl("/curvePoint/list"));

		Mockito.verify(curvePointService, Mockito.times(1)).save(any());
	}

	@Test
	public void curvePointUpdateErrorTest() throws Exception {
		Mockito.when(curvePointService.findById(anyInt())).thenReturn(Optional.empty());

		mockMvc.perform(get("/curvePoint/update/{id}", 1))
				.andExpect(redirectedUrl("/curvePoint/list?error=true"));

		Mockito.verify(curvePointService, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void curvePointUpdateTest() throws Exception {
		CurvePoint curvePoint1 = new CurvePoint();
		curvePoint1.setId(1);
		curvePoint1.setTerm(2.0);
		curvePoint1.setValue(3.0);

		Mockito.when(curvePointService.findById(anyInt())).thenReturn(Optional.of(curvePoint1));

		mockMvc.perform(get("/curvePoint/update/{id}", 1))
				.andExpectAll(status().isOk());

		Mockito.verify(curvePointService, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void curvePointUpdateValidationErrorTest() throws Exception {
		CurvePoint curvePoint1 = new CurvePoint();
		curvePoint1.setId(1);

		Mockito.when(curvePointService.save(any())).thenReturn(null);

		MvcResult resultActions = mockMvc.perform(post("/curvePoint/update/{id}", 1).flashAttr("curvePoint", curvePoint1))
				.andExpect(status().isOk()).andReturn();

		String content = resultActions.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Term is mandatory"));
		Assertions.assertTrue(content.contains("Value is mandatory"));

		Mockito.verify(curvePointService, Mockito.times(0)).save(any());
	}

	@Test
	public void curvePointUpdateValidationTest() throws Exception {
		CurvePoint curvePoint1 = new CurvePoint();
		curvePoint1.setId(1);
		curvePoint1.setTerm(2.0);
		curvePoint1.setValue(3.0);

		Mockito.when(curvePointService.save(any())).thenReturn(null);

		mockMvc.perform(post("/curvePoint/update/{id}", 1).flashAttr("curvePoint", curvePoint1))
				.andExpect(redirectedUrl("/curvePoint/list"));

		Mockito.verify(curvePointService, Mockito.times(1)).save(any());
	}

	@Test
	public void curvePointDeleteTest() throws Exception {
		Mockito.doNothing().when(curvePointService).deleteById(anyInt());

		mockMvc.perform(get("/curvePoint/delete/{id}", 1)).andExpect(
				redirectedUrl("/curvePoint/list"));

		Mockito.verify(curvePointService, Mockito.times(1)).deleteById(anyInt());
	}

}