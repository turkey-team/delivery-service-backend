package com.sparta.delivery.backend.store.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.dto.StoreResponseDto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Store extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_owner_id", nullable = false)
	private Owner owner;

	@OneToMany(mappedBy = "store")
	private List<StoreImage> storeImages = new ArrayList<>();

	@Column(name = "p_region_dong")
	private String regionDong;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	 private List<StoreCategory> storeCategories = new ArrayList<>();

	@Column(name = "name")
	private String name;

	@Column(name = "address_details")
	private String addressDetails;

	@Column(name = "review_rate")
	private double reviewRate;

	@Column(name = "min_order_price")
	private Integer minOrderPrice;

	@Column(name = "delivery_fee")
	private int deliveryFee;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StoreStatusEnum status;

	@Column(name = "phone_number")
	private String phoneNumber;



}
