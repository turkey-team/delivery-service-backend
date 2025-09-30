package com.sparta.delivery.backend.review.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.review.dto.ReviewDeleteResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewRegisterDto;
import com.sparta.delivery.backend.review.dto.ReviewResponseDto;
import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewUpdateDto;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.user.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	//private final CustomerRepository customerRepository;
	//private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;
	//private final OrderRepository orderRepository;

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

	// 내가 작성한 reviews list 조회
	public Page<ReviewViewDto> getMyReviews(UUID customerId, ReviewSearchCondition condition, Pageable pageable) {
		return reviewRepository.findMyOwnReviews(customerId, condition, pageable);
	}

	// review 등록
	@Transactional
	public ReviewResponseDto registerReview(ReviewRegisterDto registerDto, UUID storeId,
		UUID orderId, User user) {
		/*Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(
			() -> new IllegalArgumentException("해당 User를 찾을 수 없습니다.")
		);

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 Order를 찾을 수 없습니다."));

		if (!order.getOrderStatus().equals(OrderStatus.SUCCESS)) {
			throw new IllegalArgumentException("배송 완료된 주문만 리뷰 작성 가능");
		}

		if (!order.getCustomer().getId().equals(customer.getId())) {
			throw new IllegalStateException("주문한 고객만 리뷰 작성 가능");
		}

		Store store = storeRepository.findById(storeId).orElseThrow(
			() -> new IllegalArgumentException("해당 Store를 찾을 수 없습니다.")
		);*/

		Image image = null;
		if (registerDto.getImageId() != null) {
			image = imageRepository.findById(registerDto.getImageId())
				.orElseThrow(() -> new IllegalArgumentException("해당 Image를 찾을 수 없습니다."));
		}

		/*Review review = Review.builder()
			.customer(customer)
			.store(store).image(image)
			.context(registerDto.getContext())
			.rate(registerDto.getRate())
			.build();

		reviewRepository.save(review);

		return ReviewResponseDto.of(review);*/

		return null;
	}

	// review 수정
	@Transactional
	public ReviewResponseDto updateReview(ReviewUpdateDto dto, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		review.update(dto.getContext(), dto.getRate());

		return ReviewResponseDto.of(review);
	}

	// review 삭제
	@Transactional
	public ReviewDeleteResponseDto deleteReview(UUID reviewId, Long currentUserId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		review.softDelete(currentUserId);

		return ReviewDeleteResponseDto.of(review);
	}

}
