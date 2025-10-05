package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.delivery.backend.review.entity.Review;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(name = "ResViewReviewDto", description = "리뷰 조회용 DTO")
public class ResViewReviewDto {

	@Schema(description = "리뷰 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
	private UUID id;

	@Schema(description = "리뷰 작성자(고객) UUID", example = "660e8400-e29b-41d4-a716-446655440001")
	private UUID customerId;

	@Schema(description = "리뷰 대상 매장 UUID", example = "770e8400-e29b-41d4-a716-446655440002")
	private UUID storeId;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

	@Schema(description = "리뷰 내용", example = "음식이 맛있었어요!")
	private String context;

	@Schema(description = "리뷰 평점 (1~5)", example = "5")
	private int rate;

	@QueryProjection
	public ResViewReviewDto(UUID id, UUID customerId, UUID storeId,
		String imageUrl, String context, int rate) {
		this.id = id;
		this.customerId = customerId;
		this.storeId = storeId;
		this.imageUrl = imageUrl;
		this.context = context;
		this.rate = rate;
	}

	public static ResViewReviewDto of(Review review) {
		ResViewReviewDto dto = new ResViewReviewDto();
		dto.id = review.getId();
		dto.customerId = review.getCustomer().getId();
		dto.storeId = review.getStore().getId();
		dto.imageUrl = review.getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();

		return dto;
	}

}
