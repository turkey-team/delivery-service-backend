package com.sparta.delivery.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateReviewDto {

	private String context;
	private int rate;
	private String imageUrl;

}
