package com.sparta.delivery.backend.review.dto;

import java.util.UUID;

import com.querydsl.core.annotations.QueryProjection;
import com.sparta.delivery.backend.review.entity.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewViewDto {

	private UUID id;
	private UUID customerId;
	private UUID storeId;
	private String imageUrl;

	private String context;
	private int rate;

	@QueryProjection
	public ReviewViewDto(UUID id, UUID customerId, UUID storeId,
		String imageUrl, String context, int rate) {
		this.id = id;
		this.customerId = customerId;
		this.storeId = storeId;
		this.imageUrl = imageUrl;
		this.context = context;
		this.rate = rate;
	}

	public static ReviewViewDto of(Review review) {
		ReviewViewDto dto = new ReviewViewDto();
		dto.id = review.getId();
		dto.customerId = review.getCustomer().getId();
		dto.storeId = review.getStore().getId();
		dto.imageUrl = review.getImageUrl();
		dto.context = review.getContext();
		dto.rate = review.getRate();

		return dto;
	}

}
