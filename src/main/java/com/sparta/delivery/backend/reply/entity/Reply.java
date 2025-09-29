package com.sparta.delivery.backend.reply.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.review.entity.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_reply")
public class Reply extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_review_id", nullable = false)
	private Review review;

	@Column(name = "context", nullable = false)
	private String context;

}
