package com.sparta.delivery.backend.review.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.delivery.backend.review.dto.ReviewRegisterDto;
import com.sparta.delivery.backend.review.dto.ReviewResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.service.ReviewService;
import com.sparta.delivery.backend.user.entity.User;

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

	@PostMapping("/stores/{storeId}/review")
	public ReviewResponseDto writeReview(@PathVariable UUID storeId, ReviewRegisterDto registerDto,
		UUID orderId, User user) {
		// 추후 Authentication UserDetails로 바꾸기
		return reviewService.registerReview(registerDto, storeId, orderId, user);
	}

}
