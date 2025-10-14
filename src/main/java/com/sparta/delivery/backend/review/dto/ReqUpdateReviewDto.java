package com.sparta.delivery.backend.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ReqUpdateReviewDto", description = "리뷰 수정 요청 DTO")
public class ReqUpdateReviewDto {

	@Schema(description = "리뷰 내용", example = "음식이 너무 맛있었어요!")
	private String context;

	@Schema(description = "리뷰 평점 (1~5)", example = "3")
	@NotNull(message = "리뷰 평점은 필수입니다.")
	@Min(value = 1, message = "리뷰 평점은 최소 1점입니다.")
	@Max(value = 5, message = "리뷰 평점은 최대 5점입니다.")
	private int rate;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

}