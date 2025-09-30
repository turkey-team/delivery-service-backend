package com.sparta.delivery.backend.review.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;

	@GetMapping("/stores/{storeId}/reviews")
	public Page<ReviewViewDto> getReviews(@PathVariable UUID storeId, ReviewSearchCondition condition,
		Pageable pageable) {
		return reviewService.getReviews(storeId, condition, pageable);
	}

	@GetMapping("/stores/{storeId}/reviews/{reviewId}")
	public ReviewViewDto getReview(@PathVariable UUID storeId, @PathVariable UUID reviewId) {
		return reviewService.getReview(storeId, reviewId);
	}

}
