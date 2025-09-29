package com.sparta.delivery.backend.reply.entity;

import java.util.UUID;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Reply extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_id", nullable = false)
	private Review review;*/

	private String context;

}
