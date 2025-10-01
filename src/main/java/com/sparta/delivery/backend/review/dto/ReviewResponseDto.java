package com.sparta.delivery.backend.review.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.review.entity.Review;

import lombok.Getter;

@Getter
public class ReviewResponseDto {

	private UUID reviewId;
	private String context;
	private int rate;
	private String imageUrl;

	private Instant createdAt;

	public static ReviewResponseDto of(Review review) {
		ReviewResponseDto dto = new ReviewResponseDto();
		dto.reviewId = review.getId();
		dto.imageUrl = review.getImage().getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();
		dto.createdAt = review.getCreatedAt();

		return dto;
	}

}
