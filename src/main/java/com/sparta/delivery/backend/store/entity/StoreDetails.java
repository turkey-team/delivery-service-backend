package com.sparta.delivery.backend.store.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store_details")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreDetails extends BaseEntity {
	@OneToOne
	@JoinColumn(name = "p_store_id")
	private Store store;

	@Column(name = "operation_hours")
	private String operationHours;

	@Column(name = "holiday")
	private String holiday;

	@Column(name = "description")
	private String description;

	@Column(name = "business_number", length = 12)
	private String businessNumber;
}
