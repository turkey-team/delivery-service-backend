package com.sparta.delivery.backend.review.util;

import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(value = "util")
@RequiredArgsConstructor
public class ReviewGenerationUtil {

	private final StringRedisTemplate redisTemplate;

	private String key(UUID storeId) {
		return "reviews:gen:" + storeId;
	}

	public long getGeneration(UUID storeId) {
		String value = redisTemplate.opsForValue().get(key(storeId));
		long generation = (value == null) ? 0L : Long.parseLong(value);
		log.info("[ReviewGenerationUtil] getGeneration - storeId: {}, generation: {}",
			storeId, generation);

		return generation;
	}

	public void increaseGeneration(UUID storeId) {
		Long increment = redisTemplate.opsForValue().increment(key(storeId));
		log.info("[ReviewGenerationUtil] increaseGeneration - storeId: {}, newGeneration: {}",
			storeId, increment);
	}

}
