package com.sparta.delivery.backend.ai.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_ai_prompt")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class AiPrompt {

	@Id
	@UuidGenerator
	private UUID id;

	@Column(name = "req_message", length = 200, nullable = false)
	private String reqMessage;

	@Column(name = "res_message", length = 200)
	private String resMessage;

	@CreatedDate
	@Column(updatable = false)
	private Instant createdAt;

	@CreatedBy
	private Long createdBy;

	@Builder
	private AiPrompt(String reqMessage, String resMessage) {
		this.reqMessage = reqMessage;
		this.resMessage = resMessage;
	}

}
