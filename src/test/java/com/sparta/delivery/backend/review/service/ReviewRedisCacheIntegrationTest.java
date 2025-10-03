package com.sparta.delivery.backend.review.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;

@SpringBootTest
@ActiveProfiles("test")
public class ReviewRedisCacheIntegrationTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private ReviewService reviewService;

	@Test
	void cacheTest() {
		/*Store store = new Store();
		storeRepository.save(store);

		Review review = Review.builder()
			.store(store)
			.context("테스트 리뷰")
			.build();
		reviewRepository.save(review);

		// 첫 호출: 캐시 없음 → DB 조회
		Pageable pageable = PageRequest.of(0, 10);
		reviewService.getReviews(store.getId(), new ReviewRepositorySearchConditionDto(), pageable);

		// Redis에 저장됐는지 확인
		String key = "reviewList::review:store:" + store.getId();
		Object cached = redisTemplate.opsForValue().get(key);
		System.out.println("Redis cached value: " + cached);

		Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
		System.out.println("TTL: " + ttl + "초");*/
	}

	@AfterEach
	void cleanUp() {
		redisTemplate.getConnectionFactory().getConnection().flushAll();
	}
}
