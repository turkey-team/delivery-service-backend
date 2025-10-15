package com.sparta.delivery.backend.review.repository;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class ReviewRepositorySearchConditionDto {

	private Integer minRate;
	private Integer maxRate;
	private String context;

	private Instant startDate;
	private Instant endDate;

}