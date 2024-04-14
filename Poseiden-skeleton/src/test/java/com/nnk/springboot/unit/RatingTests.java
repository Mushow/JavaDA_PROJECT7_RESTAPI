package com.nnk.springboot.unit;

import com.nnk.springboot.domain.CustomUserDetails;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.RatingService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class RatingTests {

	@Autowired
	private MockMvc webTestClient;

	@MockBean
	private RatingService service;

	@BeforeEach
	public void setupAuth() {
		CustomUserDetails userDetails = new CustomUserDetails(new User(1, "user1", "password", "USER", "Test User"));
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	@Test
	public void displayAllRatings() throws Exception {
		Rating firstRating = new Rating();
		firstRating.setId(1);
		firstRating.setFitchRating(100);
		firstRating.setMoodysRating(200);
		firstRating.setSandPRating(300);
		firstRating.setOrderNumber(100);

		Rating secondRating = new Rating();
		secondRating.setId(2);
		secondRating.setFitchRating(400);
		secondRating.setMoodysRating(500);
		secondRating.setSandPRating(600);
		secondRating.setOrderNumber(200);

		List<Rating> listedRatings = new ArrayList<>(Arrays.asList(firstRating, secondRating));

		Mockito.when(service.findAll()).thenReturn(listedRatings);

		MvcResult response = webTestClient.perform(get("/rating/list"))
				.andExpect(status().isOk()).andReturn();

		String responseBody = response.getResponse().getContentAsString();

		Assertions.assertTrue(responseBody.contains("100"));
		Assertions.assertTrue(responseBody.contains("400"));
		Assertions.assertTrue(responseBody.contains("200"));
		Assertions.assertTrue(responseBody.contains("500"));

		Mockito.verify(service, Mockito.times(1)).findAll();
	}

	@Test
	public void openAddRatingForm() throws Exception {
		webTestClient.perform(get("/rating/add"))
				.andExpect(status().isOk());
	}

	@Test
	public void handleInvalidRatingSubmission() throws Exception {
		Rating emptyRating = new Rating();

		MvcResult result = webTestClient.perform(post("/rating/validate").flashAttr("rating", emptyRating))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Moodys rating is mandatory"));
		Assertions.assertTrue(content.contains("Sand rating is mandatory"));
		Assertions.assertTrue(content.contains("Fitch rating is mandatory"));
		Assertions.assertTrue(content.contains("Order number is mandatory"));

		Mockito.verify(service, Mockito.never()).save(any());
	}

	@Test
	public void confirmRatingSubmission() throws Exception {
		Rating completeRating = new Rating();
		completeRating.setFitchRating(100);
		completeRating.setMoodysRating(200);
		completeRating.setSandPRating(300);
		completeRating.setOrderNumber(100);

		Mockito.when(service.save(any())).thenReturn(null);

		webTestClient.perform(post("/rating/validate").flashAttr("rating", completeRating))
				.andExpect(redirectedUrl("/rating/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void updateRatingNotFound() throws Exception {
		Mockito.when(service.findById(anyInt())).thenReturn(Optional.empty());

		webTestClient.perform(get("/rating/update/{id}", 1))
				.andExpect(redirectedUrl("/rating/list?error=true"));

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void editRatingForm() throws Exception {
		Rating foundRating = new Rating();
		foundRating.setId(1);
		foundRating.setFitchRating(100);
		foundRating.setMoodysRating(200);
		foundRating.setSandPRating(300);
		foundRating.setOrderNumber(100);

		Mockito.when(service.findById(anyInt())).thenReturn(Optional.of(foundRating));

		webTestClient.perform(get("/rating/update/{id}", 1))
				.andExpect(status().isOk());

		Mockito.verify(service, Mockito.times(1)).findById(anyInt());
	}

	@Test
	public void displayUpdateErrorForInvalidRating() throws Exception {
		Rating ratingWithoutDetails = new Rating();
		ratingWithoutDetails.setId(1);

		MvcResult result = webTestClient.perform(post("/rating/update/{id}", 1).flashAttr("rating", ratingWithoutDetails))
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString();

		Assertions.assertTrue(content.contains("Moodys rating is mandatory"));
		Assertions.assertTrue(content.contains("Sand rating is mandatory"));
		Assertions.assertTrue(content.contains("Fitch rating is mandatory"));
		Assertions.assertTrue(content.contains("Order number is mandatory"));

		Mockito.verify(service, Mockito.never()).save(any());
	}

	@Test
	public void validateRatingUpdateSubmission() throws Exception {
		Rating detailedRating = new Rating();
		detailedRating.setId(1);
		detailedRating.setFitchRating(100);
		detailedRating.setMoodysRating(200);
		detailedRating.setSandPRating(300);
		detailedRating.setOrderNumber(100);

		Mockito.when(service.save(any())).thenReturn(null);

		webTestClient.perform(post("/rating/update/{id}", 1).flashAttr("rating", detailedRating))
				.andExpect(redirectedUrl("/rating/list"));

		Mockito.verify(service, Mockito.times(1)).save(any());
	}

	@Test
	public void removeRating() throws Exception {
		Mockito.doNothing().when(service).deleteById(anyInt());

		webTestClient.perform(get("/rating/delete/{id}", 1))
				.andExpect(redirectedUrl("/rating/list"));

		Mockito.verify(service, Mockito.times(1)).deleteById(anyInt());
	}
}
