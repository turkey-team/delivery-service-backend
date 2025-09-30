package com.sparta.delivery.backend.review.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.delivery.backend.review.entity.Review;

import lombok.Getter;

@Getter
public class ReviewDeleteResponseDto {

	private UUID reviewId;
	private String context;
	private int rate;
	private String imageUrl;

	private LocalDateTime createdAt;
	private LocalDateTime deletedAt;

	public static ReviewDeleteResponseDto of(Review review) {
		ReviewDeleteResponseDto dto = new ReviewDeleteResponseDto();
		dto.reviewId = review.getId();
		dto.imageUrl = review.getImage().getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();
		dto.createdAt = review.getCreatedAt();
		dto.deletedAt = review.getDeletedAt();

		return dto;
	}

}
