package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "ReqCreateReviewDto", description = "리뷰 작성 요청 DTO")
public class ReqCreateReviewDto {

	@Schema(description = "리뷰 내용", example = "음식이 맛있어요!")
	private String context;

	@Schema(description = "리뷰 평점(1~5)", example = "5")
	@NotNull(message = "리뷰 평점은 필수입니다.")
	@Min(value = 1, message = "리뷰 평점은 최소 1점입니다.")
	@Max(value = 5, message = "리뷰 평점은 최대 5점입니다.")
	private int rate;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

	@Schema(description = "리뷰와 연결된 주문 ID", example = "550e8400-e29b-41d4-a716-446655440000")
	@NotNull(message = "주문 ID는 필수입니다.")
	private UUID orderId;

}