package com.sparta.delivery.backend.review.repository;

import java.time.Instant;

import lombok.Getter;

@Getter
public class ReviewRepositorySearchConditionDto {

	private Integer minRate;
	private Integer maxRate;
	private String context;

	private Instant startDate;
	private Instant endDate;

}