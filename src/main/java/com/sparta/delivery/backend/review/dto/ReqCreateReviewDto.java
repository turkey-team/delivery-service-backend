package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class ReqCreateReviewDto {

	private String context;
	private int rate;
	private String imageUrl;

	private UUID orderId;

}
