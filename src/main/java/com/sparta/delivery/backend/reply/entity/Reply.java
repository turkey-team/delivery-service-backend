package com.sparta.delivery.backend.reply.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.review.entity.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_reply")
public class Reply extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_review_id", nullable = false)
	private Review review;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_owner_id", nullable = false)
	private Owner owner;

	@Column(name = "context", nullable = false)
	private String context;

	@Builder
	private Reply(Review review, Owner owner, String context) {
		this.review = review;
		this.owner = owner;
		this.context = context;
	}

	public void update(String context) {
		this.context = context;
	}

}