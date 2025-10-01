package com.sparta.delivery.backend.review.dto;

import lombok.Getter;

@Getter
public class ReqCreateReviewDto {

	private String context;
	private int rate;
	private String imageUrl;

}
