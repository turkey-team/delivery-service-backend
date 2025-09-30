package com.sparta.delivery.backend.order.entity;

import java.time.Instant;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "p_order")
@Getter
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
}
