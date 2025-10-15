package com.sparta.delivery.backend.store.entity;

import com.sparta.delivery.backend.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
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

	@Builder
	public StoreDetails(Store store, String operationHours, String holiday, String description, String businessNumber) {
		this.store = store;
		this.operationHours = operationHours;
		this.holiday = holiday;
		this.description = description;
		this.businessNumber = businessNumber;
	}

	public void updateStoreBusinessNumber(String businessNumber) {
		this.businessNumber = businessNumber;
	}

	public void updateStoreDetails(String description, String holiday, String operationHours) {
		this.description = description;
		this.holiday = holiday;
		this.operationHours = operationHours;
	}
}
