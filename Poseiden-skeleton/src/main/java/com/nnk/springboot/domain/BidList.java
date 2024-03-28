package com.nnk.springboot.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "bid_list")
public class BidList {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer bidListId;


    @NotBlank(message = "Account is mandatory.")
    private String account;

    @NotBlank(message = "Type is mandatory.")
    private String type;

    @Column(name = "bid_quantity")
    @PositiveOrZero(message = "bidQuantity should be positive.")
    private Double bidQuantity;

    @Column(name = "ask_quantity")
    @PositiveOrZero(message = "askQuantity should be positive.")
    private Double askQuantity;

    @PositiveOrZero(message = "bid should be a positive.")
    private Double bid;

    @PositiveOrZero(message = "ask should be a positive.")
    private Double ask;

    private String benchmark;

    private Date bidListDate;

    private String commentary;

    private String security;

    private String status;

    private String trader;

    private String book;

    @Column(name = "creation_name")
    private String creationName;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "revision_name")
    private String revisionName;

    @Column(name = "revision_date")
    private Timestamp revisionDate;

    @Column(name = "deal_name")
    private String dealName;

    @Column(name = "deal_type")
    private String dealType;

    @Column(name = "source_list_id")
    private String sourceListId;

    private String side;

}
