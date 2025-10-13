package com.sparta.delivery.backend.review.repository;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRepositorySearchConditionDto {

	private Integer minRate;
	private Integer maxRate;
	private String context;

	private Instant startDate;
	private Instant endDate;

}