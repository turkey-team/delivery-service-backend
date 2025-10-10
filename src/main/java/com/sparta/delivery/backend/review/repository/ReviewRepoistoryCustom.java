package com.sparta.delivery.backend.review.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.backend.review.dto.ResViewReviewDto;

public interface ReviewRepoistoryCustom {

	Page<ResViewReviewDto> findReviews(UUID storeId, ReviewRepositorySearchConditionDto condition, Pageable pageable);

	Page<ResViewReviewDto> findMyOwnReviews(UUID customerId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable);

}