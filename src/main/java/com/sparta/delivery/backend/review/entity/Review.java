package com.sparta.delivery.backend.review.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_review")
public class Review extends BaseEntity {

	// customer
	// image
	// store

	@Column(name = "context", nullable = true)
	private String context;

	@Column(name = "rate", nullable = false)
	private int rate; // rate는 null값 존재X

	@Builder
	private Review(String context, int rate) {
		// 필드 추가
		this.context = context;
		this.rate = rate;
	}

}
