package com.sparta.delivery.backend.payment.entity;

import java.time.Instant;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.order.entity.Order;

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
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

	// order
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_order_id", nullable = false)
	private Order order;

	@Column(name = "payment_gateway", nullable = false)
	private String paymentGateway;

	@Column(name = "payment_key", length = 200, nullable = false)
	private String paymentKey;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Column(name = "amount")
	private int amount;

	@Column(name = "card_number", length = 20, nullable = false)
	private String cardNumber;

	@Column(name = "issuer_code", length = 2, nullable = false)
	private String issuerCode; // 카드 발급사 코드

	@Column(name = "acquirer_code", length = 2, nullable = false)
	private String acquirerCode; // 카드 매입사 코드

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "payment_status", nullable = false)
	private PaymentStatus paymentStatus;

	private Instant approvedAt; // 결제 승인 시간

	@Builder
	private Payment(Order order, String paymentGateway, String paymentKey, String paymentMethod, int amount,
		String cardNumber,
		String issuerCode, String acquirerCode, PaymentStatus paymentStatus, Instant approvedAt) {
		this.order = order;
		this.paymentGateway = paymentGateway;
		this.paymentKey = paymentKey;
		this.paymentMethod = paymentMethod;
		this.amount = amount;
		this.cardNumber = cardNumber;
		this.issuerCode = issuerCode;
		this.acquirerCode = acquirerCode;
		this.paymentStatus = paymentStatus;
		this.approvedAt = approvedAt;
	}

	public void updatePaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

}
