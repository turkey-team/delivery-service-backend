package com.sparta.delivery.backend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDto {

	private String context;
	private int rate;
	private String imageUrl;

}
