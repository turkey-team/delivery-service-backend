package com.sparta.delivery.backend.global.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	public CacheManager reviewCacheManager(RedisConnectionFactory redisConnectionFactory) {
		System.out.println(">>> RedisCacheManager(reviewCacheManager) 생성됨 <<<");
		RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
			.defaultCacheConfig()
			.serializeKeysWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new StringRedisSerializer())
			)
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(
					new Jackson2JsonRedisSerializer<Object>(Object.class)
				)
			)
			.entryTtl(Duration.ofMinutes(5L));

		return RedisCacheManager
			.RedisCacheManagerBuilder
			.fromConnectionFactory(redisConnectionFactory)
			.cacheDefaults(redisCacheConfiguration)
			.build();
	}

}