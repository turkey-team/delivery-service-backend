package com.sparta.delivery.backend.reply.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Reply extends BaseEntity {

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;*/

	private String context;

}
