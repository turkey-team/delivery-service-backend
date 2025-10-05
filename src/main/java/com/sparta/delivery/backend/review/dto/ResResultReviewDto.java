package com.sparta.delivery.backend.review.dto;

import java.time.Instant;
import java.util.UUID;

import com.sparta.delivery.backend.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Schema(name = "ResResultReviewDto", description = "리뷰 등록/수정 결과 DTO")
public class ResResultReviewDto {

	@Schema(description = "리뷰 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID reviewId;

	@Schema(description = "리뷰 내용", example = "음식이 맛있었어요!")
	private String context;

	@Schema(description = "리뷰 평점 (1~5)", example = "5")
	private int rate;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

	@Schema(description = "리뷰 작성 시각", example = "2025-10-05T14:00:00Z")
	private Instant createdAt;

	@Schema(description = "리뷰 작성자 ID", example = "12345")
	private Long createdBy;

	@Schema(description = "리뷰 수정 시각", example = "2025-10-05T15:00:00Z")
	private Instant updatedAt;

	@Schema(description = "리뷰 수정자 ID", example = "12345")
	private Long updatedBy;

	public static ResResultReviewDto of(Review review) {
		ResResultReviewDto dto = new ResResultReviewDto();
		dto.reviewId = review.getId();
		dto.imageUrl = review.getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();
		dto.createdAt = review.getCreatedAt();
		dto.createdBy = review.getCreatedBy();
		dto.updatedAt = review.getUpdatedAt();
		dto.updatedBy = review.getUpdatedBy();

		return dto;
	}

}