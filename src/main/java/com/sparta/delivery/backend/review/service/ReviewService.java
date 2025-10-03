package com.sparta.delivery.backend.review.service;

import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.review.dto.ReqCreateReviewDto;
import com.sparta.delivery.backend.review.dto.ReqDeleteReviewDto;
import com.sparta.delivery.backend.review.dto.ReqUpdateReviewDto;
import com.sparta.delivery.backend.review.dto.ResResultReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.review.repository.ReviewRepositorySearchConditionDto;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.repository.StoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final CustomerRepository customerRepository;
	private final StoreRepository storeRepository;
	private final ImageRepository imageRepository;
	private final OrderRepository orderRepository;

	// review 단건 조회
	public ResViewReviewDto getReview(UUID storeId, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.filter(r -> r.getStore().getId().equals(storeId))
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		return ResViewReviewDto.of(review);
	}

	// reviews list 조회
	@Cacheable(value = "reviewList", key = "'review:store:' + #storeId", cacheManager = "reviewCacheManager")
	@Transactional(readOnly = true)
	public Page<ResViewReviewDto> getReviews(UUID storeId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		return reviewRepository.findReviews(storeId, condition, pageable);
	}

	// 내가 작성한 reviews list 조회
	public Page<ResViewReviewDto> getMyReviews(UUID customerId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		return reviewRepository.findMyOwnReviews(customerId, condition, pageable);
	}

	private Long getAuthenticationUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new IllegalStateException("로그인 정보가 없습니다.");
		}

		UserDetailsImpl userDetails = (UserDetailsImpl)authentication.getPrincipal();
		Long userId = userDetails.getId();
		System.out.println("userId = " + userId);

		return userId;
	}

	// review 등록
	@CacheEvict(value = "reviewList", key = "'review:store:' + #storeId", cacheManager = "reviewCacheManager")
	@Transactional
	public ResResultReviewDto registerReview(ReqCreateReviewDto registerDto, UUID storeId,
		UUID orderId) {

		Customer customer = customerRepository.findByUserId(getAuthenticationUserId()).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new NoSuchElementException("해당 Order를 찾을 수 없습니다."));

		if (!order.getOrderStatus().equals(OrderStatus.SUCCESS)) {
			throw new IllegalStateException("배송 완료된 주문만 리뷰 작성 가능");
		}

		if (!order.getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("주문한 고객만 리뷰 작성 가능");
		}

		Store store = storeRepository.findById(storeId).orElseThrow(
			() -> new NoSuchElementException("해당 Store를 찾을 수 없습니다.")
		);

		Review review = Review.builder()
			.customer(customer)
			.store(store).imageUrl(registerDto.getImageUrl())
			.context(registerDto.getContext())
			.rate(registerDto.getRate())
			.build();

		reviewRepository.save(review);
		store.addReview(review.getRate());

		return ResResultReviewDto.of(review);
	}

	// review 수정
	@CacheEvict(value = "reviewList", key = "'review:store:' + #storeId", cacheManager = "reviewCacheManager")
	@Transactional
	public ResResultReviewDto updateReview(ReqUpdateReviewDto dto, UUID reviewId, UUID storeId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		Customer customer = customerRepository.findByUserId(getAuthenticationUserId()).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		if (!review.getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("본인이 작성한 리뷰만 수정 가능합니다.");
		}

		review.update(dto.getContext(), dto.getRate(), dto.getImageUrl());

		Store store = storeRepository.findById(review.getStore().getId()).orElseThrow(
			() -> new NoSuchElementException("해당 Store를 찾을 수 없습니다.")
		);

		store.updateReview(review.getRate(), dto.getRate());

		return ResResultReviewDto.of(review);
	}

	// review 삭제
	@CacheEvict(value = "reviewList", key = "'review:store:' + #storeId", cacheManager = "reviewCacheManager")
	@Transactional
	public ReqDeleteReviewDto deleteReview(UUID reviewId, UUID storeId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		Long userId = getAuthenticationUserId();

		Customer customer = customerRepository.findByUserId(userId).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		if (!review.getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("본인이 작성한 리뷰만 삭제 가능합니다.");
		}

		review.softDelete(userId);

		Store store = storeRepository.findById(review.getStore().getId()).orElseThrow(
			() -> new NoSuchElementException("해당 Store를 찾을 수 없습니다.")
		);

		store.deleteReview(review.getRate());

		return ReqDeleteReviewDto.of(review);
	}

}
