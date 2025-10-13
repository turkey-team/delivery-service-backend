package com.sparta.delivery.backend.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
	private int rate;

	@Schema(description = "리뷰 이미지 URL", example = "https://example.com/image.jpg")
	private String imageUrl;

}