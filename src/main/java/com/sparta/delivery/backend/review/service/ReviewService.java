package com.sparta.delivery.backend.review.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;

	// review 단건 조회
	public ReviewViewDto getReview(UUID storeId, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.filter(r -> r.getStore().getId().equals(storeId))
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		return ReviewViewDto.of(review);
	}

	// reviews list 조회
	public Page<ReviewViewDto> getReviews(UUID storeId, ReviewSearchCondition condition, Pageable pageable) {
		return reviewRepository.findReviews(storeId, condition, pageable);
	}

	// review 등록

	// review 수정

	// review 삭제

}
