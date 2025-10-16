package com.sparta.delivery.backend.global.common;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

	@Id
	@UuidGenerator
	private UUID id;

	@CreatedDate
	@Column(updatable = false)
	private Instant createdAt;

	// BIGSERIAL 타입으로 되어있어서 Long으로 일단 했습니다
	@CreatedBy
	private Long createdBy;

	@LastModifiedDate
	private Instant updatedAt;

	@LastModifiedBy
	private Long updatedBy;

	private Instant deletedAt;

	private Long deletedBy;

	@PrePersist
	protected void prePersist() {
		this.createdAt = Instant.now();
		this.updatedAt = Instant.now();
	}

	@PreUpdate
	protected void preUpdate() {
		this.updatedAt = Instant.now();
	}

	public void softDelete(Long userId) {
		if (deletedAt == null) {
			this.deletedAt = Instant.now();
			this.deletedBy = userId;
		}
	}

	public boolean isDeleted() {
		return this.deletedAt != null;
	}

}