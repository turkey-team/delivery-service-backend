package com.sparta.delivery.backend.review.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.review.dto.ReqCreateReviewDto;
import com.sparta.delivery.backend.review.dto.ReqDeleteReviewDto;
import com.sparta.delivery.backend.review.dto.ReqUpdateReviewDto;
import com.sparta.delivery.backend.review.dto.ResResultReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.repository.ReviewRepositorySearchConditionDto;
import com.sparta.delivery.backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/stores/{storeId}/reviews")
	public Page<ResViewReviewDto> getReviews(@PathVariable UUID storeId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		return reviewService.getReviews(storeId, condition, pageable);
	}

	@GetMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResViewReviewDto getReview(@PathVariable UUID storeId, @PathVariable UUID reviewId) {
		return reviewService.getReview(storeId, reviewId);
	}

	// url은 추후 의논 후 변경
	@GetMapping("/customer/{customerId}/reviews")
	public Page<ResViewReviewDto> getMyReviews(@PathVariable UUID customerId,
		ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		// UserDetails를 통해 customerId와 UserDetails 객체 안의 customerId 일치하는지 확인
		return reviewService.getMyReviews(customerId, condition, pageable);
	}

	@PostMapping("/stores/{storeId}/review")
	public ResResultReviewDto writeReview(@PathVariable UUID storeId, @RequestBody ReqCreateReviewDto registerDto,
		UUID orderId) {
		return reviewService.registerReview(registerDto, storeId, orderId);
	}

	@PutMapping("/stores/{storeId}/reviews/{reviewId}")
	public ResResultReviewDto updateReview(@PathVariable UUID storeId, @PathVariable UUID reviewId,
		@RequestBody ReqUpdateReviewDto updateDto) {
		return reviewService.updateReview(updateDto, reviewId, storeId);
	}

	@DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
	public ReqDeleteReviewDto deleteReview(@PathVariable UUID storeId,
		@PathVariable UUID reviewId) {
		return reviewService.deleteReview(reviewId, storeId);
	}

}
