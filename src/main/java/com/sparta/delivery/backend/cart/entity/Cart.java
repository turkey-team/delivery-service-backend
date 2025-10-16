package com.sparta.delivery.backend.cart.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_store_menu_id", nullable = false)
	private StoreMenu menu;

	@Column(name = "created_at", updatable = false, nullable = false)
	private Instant createdAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Column(name = "deleted_by")
	private Long deletedBy;

	@Builder
	public Cart(Customer customer, StoreMenu menu) {
		this.customer = customer;
		this.menu = menu;
	}

	@PrePersist
	protected void preCreate() {
		this.createdAt = Instant.now();
	}

	public void delete(Long deletedBy) {
		this.deletedAt = Instant.now();
		this.deletedBy = deletedBy;
	}

}
