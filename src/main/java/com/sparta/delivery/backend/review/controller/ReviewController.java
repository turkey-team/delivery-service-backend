package com.sparta.delivery.backend.review.controller;

import java.util.List;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.review.dto.ReqCreateReviewDto;
import com.sparta.delivery.backend.review.dto.ReqUpdateReviewDto;
import com.sparta.delivery.backend.review.dto.ResDeleteReviewDto;
import com.sparta.delivery.backend.review.dto.ResResultReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.repository.ReviewRepositorySearchConditionDto;
import com.sparta.delivery.backend.review.service.ReviewService;
import com.sparta.delivery.backend.security.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "Review-Controller", description = "리뷰 관련 API")
public class ReviewController {

	private final ReviewService reviewService;

	@Operation(summary = "매장 리뷰 목록 조회",
		description = "주어진 매장 ID에 해당하는 리뷰 목록을 조회합니다. "
			+ "페이지네이션(Pageable)과 검색 조건(ReviewRepositorySearchConditionDto)을 지원합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ResViewReviewDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 매장 없음")
	})
	@GetMapping("/stores/{storeId}/reviews")
	public List<ResViewReviewDto> getReviews(
		@Parameter(description = "리뷰를 조회할 매장의 UUID") @PathVariable UUID storeId,
		@ParameterObject @ModelAttribute ReviewRepositorySearchConditionDto condition,
		@Parameter(description = "페이지네이션 정보") @ParameterObject
		@PageableDefault(page = 0, size = 10)
		Pageable pageable) {
		return reviewService.getReviews(storeId, condition, pageable);
	}

	@Operation(summary = "특정 리뷰 조회",
		description = "주어진 매장 ID와 리뷰 ID에 해당하는 리뷰 상세 정보를 조회합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 조회 성공",
			content = @Content(schema = @Schema(implementation = ResViewReviewDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 매장 없음")
	})
	@GetMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResViewReviewDto getReview(
		@Parameter(description = "리뷰가 속한 매장의 UUID") @PathVariable UUID storeId,
		@Parameter(description = "조회할 리뷰의 UUID") @PathVariable UUID reviewId) {
		return reviewService.getReview(storeId, reviewId);
	}

	// url은 추후 의논 후 변경
	@Operation(summary = "내가 작성한 리뷰 목록 조회",
		description = "주어진 고객 ID에 해당하는 고객 본인이 작성한 리뷰 목록을 조회합니다. "
			+ "페이지네이션(Pageable)과 검색 조건(ReviewRepositorySearchConditionDto)을 지원합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공",
			content = @Content(schema = @Schema(implementation = ResViewReviewDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "고객 또는 리뷰 없음")
	})
	@GetMapping("/customer/{customerId}/reviews")
	public Page<ResViewReviewDto> getMyReviews(
		@Parameter(description = "리뷰를 조회할 고객의 UUID") @PathVariable UUID customerId,
		@Parameter(description = "검색 조건 DTO") ReviewRepositorySearchConditionDto condition,
		@Parameter(description = "페이지네이션 정보") Pageable pageable) {
		// UserDetails를 통해 customerId와 UserDetails 객체 안의 customerId 일치하는지 확인
		return reviewService.getMyReviews(customerId, condition, pageable);
	}

	@Operation(summary = "리뷰 작성",
		description = "주어진 매장 ID에 대해 고객이 리뷰를 작성합니다. 주문 완료 상태여야 작성 가능합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "리뷰 작성 성공",
			content = @Content(schema = @Schema(implementation = ResResultReviewDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "매장 또는 주문 없음")
	})
	@PostMapping("/stores/{storeId}/review")
	public ResResultReviewDto writeReview(
		@Parameter(description = "리뷰를 작성할 매장의 UUID") @PathVariable UUID storeId,
		@Parameter(description = "리뷰 작성 요청 DTO") @RequestBody ReqCreateReviewDto registerDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return reviewService.registerReview(registerDto, storeId, userId);
	}

	@Operation(summary = "리뷰 수정",
		description = "주어진 매장 ID와 리뷰 ID에 해당하는 리뷰를 고객 본인이 수정합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 수정 성공",
			content = @Content(schema = @Schema(implementation = ResResultReviewDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 매장 없음")
	})
	@PutMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResResultReviewDto updateReview(
		@Parameter(description = "리뷰가 속한 매장의 UUID") @PathVariable UUID storeId,
		@Parameter(description = "수정할 리뷰의 UUID") @PathVariable UUID reviewId,
		@Parameter(description = "리뷰 수정 요청 DTO") @RequestBody ReqUpdateReviewDto updateDto,
		@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return reviewService.updateReview(updateDto, reviewId, userId);
	}

	@Operation(summary = "리뷰 삭제",
		description = "주어진 매장 ID와 리뷰 ID에 해당하는 리뷰를 고객 본인이 삭제합니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "리뷰 삭제 성공",
			content = @Content(schema = @Schema(implementation = ResDeleteReviewDto.class))),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 매장 없음")
	})
	@DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResDeleteReviewDto deleteReview(
		@Parameter(description = "리뷰가 속한 매장의 UUID") @PathVariable UUID storeId,
		@Parameter(description = "삭제할 리뷰의 UUID") @PathVariable UUID reviewId,
		@Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl userDetails) {
		Long userId = userDetails.getId();
		return reviewService.deleteReview(reviewId, userId);
	}

}