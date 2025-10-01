package com.sparta.delivery.backend.cart.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
	private Instant createAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;
}
