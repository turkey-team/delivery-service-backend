package com.sparta.delivery.backend.review.dto;

import java.time.LocalDate;

import lombok.Getter;

@Getter
public class ReviewSearchCondition {

	private Integer minRate;
	private Integer maxRate;
	private String context;

	private LocalDate startDate;
	private LocalDate endDate;

}
