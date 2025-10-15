package com.sparta.delivery.backend.order.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.order.dto.ReqUpdateOrderStatusDto;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.payment.entity.PayMethod;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@Column(name = "address_details", length = 255, nullable = false)
	private String addressDetails;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private OrderStatus orderStatus;

	@Column(name = "request_message")
	private String requestMessage;

	@Enumerated(EnumType.STRING)
	@Column(name = "pay_method", nullable = false)
	private PayMethod payMethod;

	@Column(name = "cancelled_at")
	private Instant cancelledAt;

	@Column(name = "cancelled_by")
	private Long cancelledBy;      // 주문을 취소한 사용자 ID (User PK 등 참조)

	@Column(name = "cancelled_reason", length = 255)
	private String cancelledReason;

	@OneToMany(mappedBy = "order")
	private List<OrderMenu> orderMenus = new ArrayList<>();

	@Builder
	private Order(Store store, Customer customer, Dong dongEntity, String gu, String dong,
		String addressDetails, OrderStatus orderStatus, String requestMessage, PayMethod payMethod, Instant cancelledAt, Long cancelledBy,
		String cancelledReason) {
		this.store = store;
		this.customer = customer;
		this.dongEntity = dongEntity;
		this.gu = gu;
		this.dong = dong;
		this.addressDetails = addressDetails;
		this.orderStatus = orderStatus;
		this.requestMessage =  requestMessage;
		this.payMethod = payMethod;
		this.cancelledAt = cancelledAt;
		this.cancelledBy = cancelledBy;
		this.cancelledReason = cancelledReason;
	}

	public void updateOrderStatus(User user, ReqUpdateOrderStatusDto reqUpdateOrderStatusDto) {
		if (this.orderStatus != OrderStatus.ORDERED) {
			throw new IllegalStateException("이미 처리된 주문입니다.");
		}

		this.orderStatus = reqUpdateOrderStatusDto.getOrderStatus();

		// 주문 거절하였을 때
		if (this.orderStatus == OrderStatus.CANCELLED) {
			this.cancelledAt = Instant.now();
			this.cancelledBy = user.getId();
			this.cancelledReason = reqUpdateOrderStatusDto.getCancelledReason();
		}
	}
}
