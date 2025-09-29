package com.sparta.delivery.backend.cart.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_cart")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Cart {
	@Id
	@UuidGenerator
	@Column(name = "id", updatable = false, nullable = false, unique = true)
	private UUID id;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "p_customer_id", nullable = false)
	// private Customer customer;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "p_store_menu_id", nullable = false)
	// private Menu menu;

	@Column(name = "create_at", updatable = false, nullable = false)
	private LocalDateTime createAt;

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@PrePersist
	public void generateId() {
		if (this.id == null) {
			this.id = UUID.randomUUID();
		}
	}
}
