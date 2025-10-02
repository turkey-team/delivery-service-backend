package com.sparta.delivery.backend.review.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.review.entity.Review;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ReqDeleteReviewDto {

	private UUID reviewId;
	private String context;
	private int rate;
	private String imageUrl;

	private Instant createdAt;
	private Instant deletedAt;
	private Long deletedBy;

	public static ReqDeleteReviewDto of(Review review) {
		ReqDeleteReviewDto dto = new ReqDeleteReviewDto();
		dto.reviewId = review.getId();
		dto.imageUrl = review.getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();
		dto.createdAt = review.getCreatedAt();
		dto.deletedAt = review.getDeletedAt();
		dto.deletedBy = review.getDeletedBy();

		return dto;
	}

}
