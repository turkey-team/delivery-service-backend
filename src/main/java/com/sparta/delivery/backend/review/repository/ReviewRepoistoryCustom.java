package com.sparta.delivery.backend.review.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;

public interface ReviewRepoistoryCustom {

	Page<ReviewViewDto> findReviews(UUID storeId, ReviewSearchCondition condition, Pageable pageable);

	Page<ReviewViewDto> findMyOwnReviews(UUID customerId, ReviewSearchCondition condition, Pageable pageable);

}
