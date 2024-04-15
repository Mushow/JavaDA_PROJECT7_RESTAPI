package com.nnk.springboot.integration;

import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.services.RatingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc(addFilters=false)
@ActiveProfiles("test")
@Sql("../../../../scripts/rating.sql")
public class RatingServiceIT {

    @Autowired
    private RatingService ratingService;

    @Test
    public void ratingIT() {
        List<Rating> ratings = ratingService.findAll();

        Rating rating1 = ratings.get(0);
        Rating rating2 = ratings.get(1);

        Assertions.assertEquals(1, rating1.getMoodysRating());
        Assertions.assertEquals(2, rating1.getSandPRating());
        Assertions.assertEquals(3, rating1.getFitchRating());
        Assertions.assertEquals(10, rating1.getOrderNumber());
        Assertions.assertEquals(4, rating2.getMoodysRating());
        Assertions.assertEquals(5, rating2.getSandPRating());
        Assertions.assertEquals(6, rating2.getFitchRating());
        Assertions.assertEquals(20, rating2.getOrderNumber());

        Assertions.assertEquals(2, ratings.size());

        Rating rating = new Rating();
        rating.setMoodysRating(7);
        rating.setSandPRating(8);
        rating.setFitchRating(9);
        rating.setOrderNumber(30);

        Rating ratingResponse = ratingService.save(rating);
        Assertions.assertNotNull(ratingResponse.getId());
        Assertions.assertEquals(rating.getSandPRating(), ratingResponse.getSandPRating());

        Optional<Rating> optionalRating = ratingService.findById(3);

        Assertions.assertTrue(optionalRating.isPresent());
        Assertions.assertEquals(9, optionalRating.get().getFitchRating());
        Assertions.assertEquals(30, optionalRating.get().getOrderNumber());

        ratingService.deleteById(3);

        optionalRating = ratingService.findById(3);

        Assertions.assertTrue(optionalRating.isEmpty());
    }

}