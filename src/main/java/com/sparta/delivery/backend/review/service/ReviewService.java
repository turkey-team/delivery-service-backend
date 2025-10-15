package com.sparta.delivery.backend.review.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.reply.service.ReplyService;
import com.sparta.delivery.backend.review.dto.ReqCreateReviewDto;
import com.sparta.delivery.backend.review.dto.ReqUpdateReviewDto;
import com.sparta.delivery.backend.review.dto.ResDeleteReviewDto;
import com.sparta.delivery.backend.review.dto.ResResultReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.review.repository.ReviewRepositorySearchConditionDto;
import com.sparta.delivery.backend.review.util.ReviewGenerationUtil;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.repository.StoreRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final CustomerRepository customerRepository;
	private final StoreRepository storeRepository;
	private final OrderRepository orderRepository;
	private final CacheManager cacheManager;
	private final ReplyService replyService;

	private final ReviewGenerationUtil util;

	@PersistenceContext
	private EntityManager em;

	private static final String REVIEW_CACHE_NAME = "reviewList";

	// review 단건 조회
	@Transactional(readOnly = true)
	public ResViewReviewDto getReview(UUID storeId, UUID reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.filter(r -> r.getStore().getId().equals(storeId))
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		return ResViewReviewDto.of(review);
	}

	// reviews list 조회 + cache
	@Cacheable(
		value = "reviewList",
		key = "'review:store:' + #storeId + ':page:' + #pageable.pageNumber + "
			+ "':size:' + #pageable.pageSize + ':gen:' + @util.getGeneration(#storeId)",
		cacheManager = "reviewCacheManager",
		condition = "#pageable.pageNumber == 0 && (#condition == null || #condition.isNull())")
	@Transactional(readOnly = true)
	public List<ResViewReviewDto> getReviews(UUID storeId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		Page<ResViewReviewDto> reviews = reviewRepository.findReviews(storeId, condition, pageable);

		return reviews.getContent();
	}

	// 내가 작성한 reviews list 조회
	public Page<ResViewReviewDto> getMyReviews(UUID customerId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		return reviewRepository.findMyOwnReviews(customerId, condition, pageable);
	}

	private void evictReviewCache(UUID storeId) {
		Cache cache = cacheManager.getCache(REVIEW_CACHE_NAME);
		if (cache != null) {
			String key = "review:store:" + storeId + ":page:0";
			cache.evict(key);
			System.out.println("Redis 첫 페이지 캐시 삭제됨 -> key : " + key);
		}
	}

	// review 등록
	/*@CacheEvict(
		value = "reviewList",
		key = "'review:store:' + #storeId",
		cacheManager = "reviewCacheManager"
	)*/
	@Transactional
	public ResResultReviewDto registerReview(ReqCreateReviewDto registerDto, UUID storeId, Long userId) {

		Customer customer = customerRepository.findByUserId(userId).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		Order order = orderRepository.findById(registerDto.getOrderId())
			.orElseThrow(() -> new NoSuchElementException("해당 Order를 찾을 수 없습니다."));

		if (!order.getOrderStatus().equals(OrderStatus.ACCEPTED)) {
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

		if (registerDto.getRate() < 1 || registerDto.getRate() > 5) {
			throw new IllegalStateException("리뷰 평점은 1~5 사이여야 합니다.");
		}

		reviewRepository.save(review);

		store.addReview(review.getRate());
		em.flush();
		em.clear();

		//evictReviewCache(storeId);
		util.increaseGeneration(storeId);

		Owner owner = store.getOwner();
		log.info("registerReview 완료 - 비동기 호출 직전 thread: {}", Thread.currentThread().getName());
		replyService.generateReplyAsync(review.getId(), owner.getId());

		return ResResultReviewDto.of(review);
	}

	// review 수정
	/*@CacheEvict(
		value = "reviewList",
		key = "'review:store:' + #review.store.id",
		cacheManager = "reviewCacheManager"
	)*/
	@Transactional
	public ResResultReviewDto updateReview(ReqUpdateReviewDto dto, UUID reviewId,
		Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다."));

		Customer customer = customerRepository.findByUserId(userId).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		if (!review.getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("본인이 작성한 리뷰만 수정 가능합니다.");
		}

		int oldRate = review.getRate();

		review.update(dto.getContext(), dto.getRate(), dto.getImageUrl());

		Store store = review.getStore();
		store.updateReview(oldRate, dto.getRate());
		//evictReviewCache(store.getId());
		util.increaseGeneration(store.getId());

		return ResResultReviewDto.of(review);
	}

	// review 삭제
	/*@CacheEvict(
		value = "reviewList",
		key = "'review:store:' + #review.store.id",
		cacheManager = "reviewCacheManager"
	)*/
	@Transactional
	public ResDeleteReviewDto deleteReview(UUID reviewId, Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new NoSuchElementException("해당 리뷰를 찾을 수 없습니다."));

		Customer customer = customerRepository.findByUserId(userId).orElseThrow(
			() -> new IllegalStateException("해당 User를 찾을 수 없습니다.")
		);

		if (!review.getCustomer().getId().equals(customer.getId())) {
			throw new UnauthorizedException("본인이 작성한 리뷰만 삭제 가능합니다.");
		}

		review.softDelete(userId);

		Store store = review.getStore();
		store.deleteReview(review.getRate());
		//evictReviewCache(store.getId());
		util.increaseGeneration(store.getId());

		return ResDeleteReviewDto.of(review);
	}

}