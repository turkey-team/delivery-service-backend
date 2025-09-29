package com.sparta.delivery.backend.store.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store_details")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreDetails extends BaseEntity {
	// @OneToOne
	// @JoinColumn(name = "p_store_id")
	// private Store store;

	@Column(name = "operation_hours")
	private String operationHours;

	@Column(name = "holiday")
	private String holiday;

	@Column(name = "description")
	private String description;

	@Column(name = "business_number", length = 12)
	private String businessNumber;
}
