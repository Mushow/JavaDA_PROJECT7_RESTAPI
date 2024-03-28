package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Moodys rating is mandatory")
    @PositiveOrZero(message = "Moodys rating must be positive.")
    @Column(name = "moodys_rating")
    private Integer moodysRating;

    @NotNull(message = "Sand rating is mandatory")
    @PositiveOrZero(message = "Sand rating must be positive.")
    @Column(name = "sand_p_rating")
    private Integer sandPRating;

    @NotNull(message = "Fitch rating is mandatory")
    @PositiveOrZero(message = "Fitch rating must be positive.")
    @Column(name = "fitch_rating")
    private Integer fitchRating;

    @NotNull(message = "Order number is mandatory")
    @PositiveOrZero(message = "Order number must be positive.")
    @Column(name = "order_number")
    private Integer orderNumber;

}
