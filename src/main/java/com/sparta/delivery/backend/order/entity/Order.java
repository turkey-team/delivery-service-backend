package com.sparta.delivery.backend.order.entity;

import java.time.Instant;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	// 매장 FK
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_store_id", nullable = false)
	private Store store;

	// 주문 고객 FK
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_customer_id", nullable = false)
	private Customer customer;

	// 행정동 FK
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_dong_id", nullable = false)
	private Dong dongEntity;

	@Column(length = 50, nullable = false)
	private String gu;

	@Column(length = 50, nullable = false)
	private String dong;

	@Column(name = "address_detail", length = 255, nullable = false)
	private String addressDetail;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private OrderStatus orderStatus;

	@Column(name = "cancelled_at")
	private Instant cancelledAt;

	@Column(name = "cancelled_by")
	private Long cancelledBy;      // 주문을 취소한 사용자 ID (User PK 등 참조)

	@Column(name = "cancelled_reason", length = 255)
	private String cancelledReason;

	@Builder
	private Order(Store store, Customer customer, Dong dongEntity, String gu, String dong,
		String addressDetail, OrderStatus orderStatus, Instant cancelledAt, Long cancelledBy,
		String cancelledReason) {
		this.store = store;
		this.customer = customer;
		this.dongEntity = dongEntity;
		this.gu = gu;
		this.dong = dong;
		this.addressDetail = addressDetail;
		this.orderStatus = orderStatus;
		this.cancelledAt = cancelledAt;
		this.cancelledBy = cancelledBy;
		this.cancelledReason = cancelledReason;
	}
}
