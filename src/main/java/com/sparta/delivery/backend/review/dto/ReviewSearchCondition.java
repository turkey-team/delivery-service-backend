package com.sparta.delivery.backend.review.dto;

import java.time.Instant;

import lombok.Getter;

@Getter
public class ReviewSearchCondition {

	private Integer minRate;
	private Integer maxRate;
	private String context;

	private Instant startDate;
	private Instant endDate;

}
