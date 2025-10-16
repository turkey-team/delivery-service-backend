package com.sparta.delivery.backend.reply.entity;

import java.util.UUID;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.manager.entity.Manager;
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
	@JoinColumn(name = "p_owner_id")
	private Owner owner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_manager_id")
	private Manager manager;

	@Column(name = "context", nullable = false, length = 500)
	private String context;

	@Builder
	private Reply(Review review, Owner owner, Manager manager, String context) {
		this.review = review;
		this.owner = owner;
		this.manager = manager;
		this.context = context;
	}

	public void update(String context) {
		this.context = context;
	}

	public UUID getWriterId() {
		if (owner != null) {
			return owner.getId();
		}
		if (manager != null) {
			return manager.getId();
		}
		return null;
	}

	public String getWriterName() {
		if (owner != null)
			return owner.getNickname();
		if (manager != null)
			return manager.getName();
		return "알 수 없음";
	}

}