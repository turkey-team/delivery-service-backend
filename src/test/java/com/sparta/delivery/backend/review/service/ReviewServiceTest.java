package com.sparta.delivery.backend.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.review.dto.ReviewDeleteResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewUpdateDto;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepositoryCustom; // ReviewRepositoryImpl의 인터페이스

	// ===== 리뷰 수정 테스트 =====
	@Test
	void testUpdateReview() {
		UUID reviewId = UUID.randomUUID();

		Image image = Image.builder()
			.imageUrl("test.jpg")
			.build();

		Review existingReview = Review.builder()
			.context("기존 리뷰")
			.rate(3)
			.image(image)
			.build();
		//existingReview.setId(reviewId);

		ReviewUpdateDto updateDto = new ReviewUpdateDto("수정된 리뷰", 5, image.getId());

		// Mock 동작 정의
		when(reviewRepositoryCustom.findById(reviewId)).thenReturn(Optional.of(existingReview));
		// save 호출 검증 제거

		// 서비스 호출
		ReviewResponseDto result = reviewService.updateReview(updateDto, reviewId);

		// 검증
		assertNotNull(result);
		assertEquals("수정된 리뷰", result.getContext());
		assertEquals(5, result.getRate());

		verify(reviewRepositoryCustom, times(1)).findById(reviewId);
		// save 검증 제거
	}

	@Test
	void testDeleteReview() {
		UUID reviewId = UUID.randomUUID();
		Long currentUserId = 1L;

		Image image = Image.builder()
			.imageUrl("test.jpg")
			.build();

		Review existingReview = Review.builder()
			.context("삭제할 리뷰")
			.rate(4)
			.image(image)
			.build();
		//existingReview.setId(reviewId);

		// Mock 동작 정의
		when(reviewRepositoryCustom.findById(reviewId)).thenReturn(Optional.of(existingReview));

		// 서비스 호출
		ReviewDeleteResponseDto result = reviewService.deleteReview(reviewId, currentUserId);

		// 검증
		assertNotNull(result);
		assertEquals(reviewId, result.getReviewId());
		// Soft delete 필드 검증
		// assertNotNull(existingReview.getDeletedAt());

		verify(reviewRepositoryCustom, times(1)).findById(reviewId);
	}

	@Test
	void testFindReviews_withPageableAndFilters() {
		// ===== 1. 테스트 데이터 준비 =====
		UUID storeId = UUID.randomUUID();
		UUID reviewId1 = UUID.randomUUID();
		UUID reviewId2 = UUID.randomUUID();

		ReviewViewDto review1 = new ReviewViewDto(reviewId1, UUID.randomUUID(), storeId,
			"http://img1.com", "좋아요", 5);
		ReviewViewDto review2 = new ReviewViewDto(reviewId2, UUID.randomUUID(), storeId,
			"http://img2.com", "보통", 3);

		List<ReviewViewDto> mockReviews = Arrays.asList(review1, review2);

		ReviewSearchCondition condition = new ReviewSearchCondition();
		/*condition.setMinRate(3);
		condition.setMaxRate(5);
		condition.setContext("");*/

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("createdAt")));

		// ===== 2. Mockito 동작 정의 =====
		when(reviewRepositoryCustom.findReviews(storeId, condition, pageable))
			.thenReturn(new PageImpl<>(mockReviews, pageable, mockReviews.size()));

		// ===== 3. Service 호출 =====
		Page<ReviewViewDto> resultPage = reviewService.getReviews(storeId, condition, pageable);

		// ===== 4. 결과 검증 =====
		// 정렬 테스트는 모든 entity 정상적으로 매핑될때(mock에서는 정렬 test 불가능)
		assertNotNull(resultPage);
		assertEquals(2, resultPage.getContent().size());
		assertEquals(5, resultPage.getContent().get(0).getRate());
		assertEquals(3, resultPage.getContent().get(1).getRate());

		// ===== 5. Repository 호출 검증 =====
		verify(reviewRepositoryCustom, times(1)).findReviews(storeId, condition, pageable);
	}

}