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

import com.sparta.delivery.backend.review.dto.ReviewDeleteResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewRegisterDto;
import com.sparta.delivery.backend.review.dto.ReviewResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewUpdateDto;
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

	// url은 추후 의논 후 변경
	@GetMapping("/customer/{customerId}/reviews")
	public Page<ReviewViewDto> getMyReviews(@PathVariable UUID customerId, ReviewSearchCondition condition,
		Pageable pageable) {
		// UserDetails를 통해 customerId와 UserDetails 객체 안의 customerId 일치하는지 확인
		return reviewService.getMyReviews(customerId, condition, pageable);
	}

	@PostMapping("/stores/{storeId}/review")
	public ReviewResponseDto writeReview(@PathVariable UUID storeId, @RequestBody ReviewRegisterDto registerDto,
		UUID orderId, User user) {
		// 추후 User를 Authentication UserDetails로 바꾸기
		return reviewService.registerReview(registerDto, storeId, orderId, user);
	}

	@PutMapping("/stores/{storeId}/reviews/{reviewId}")
	public ReviewResponseDto updateReview(@PathVariable UUID storeId, @PathVariable UUID reviewId,
		@RequestBody ReviewUpdateDto updateDto) {
		// review에 있는 customerID랑 Authentication UserDetails에 있는 customerId 일치 확인
		return reviewService.updateReview(updateDto, reviewId);
	}

	@DeleteMapping("/stores/{storeId}/reviews/{reviewId}")
	public ReviewDeleteResponseDto deleteReview(@PathVariable UUID storeId,
		@PathVariable UUID reviewId, Long currentUserId) {
		// 추후 currentUserId Authentication UserDetails로 바꾸기
		// review에 있는 customerID랑 Authentication UserDetails에 있는 customerId 일치 확인
		return reviewService.deleteReview(reviewId, currentUserId);
	}

}
