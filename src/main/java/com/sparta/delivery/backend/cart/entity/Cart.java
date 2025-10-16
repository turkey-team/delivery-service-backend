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

	@Column(name = "create_at", updatable = false, nullable = false)
	private Instant createAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@Builder
	public Cart(Customer customer, StoreMenu menu) {
		this.customer = customer;
		this.menu = menu;
	}

	@PrePersist
	protected void preCreate() {
		this.createAt = Instant.now();
	}

	public void delete() {
		this.deletedAt = Instant.now();
	}

}
