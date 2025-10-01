package com.sparta.delivery.backend.store.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.owner.entity.Owner;

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

	@Column(name = "review_cnt")
	private int reviewCnt;

	@Column(name = "min_order_price")
	private Integer minOrderPrice;

	@Column(name = "delivery_fee")
	private int deliveryFee;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StoreStatusEnum status;

	@Column(name = "phone_number")
	private String phoneNumber;

	public void addReview(int rate) {
		this.reviewRate = (this.reviewRate * this.reviewCnt + rate) / (this.reviewCnt + 1);
		this.reviewCnt++;
	}

	public void updateReview(int oldRate, int newRate) {
		this.reviewRate = (this.reviewRate * this.reviewCnt - oldRate + newRate);
	}

	public void deleteReview(int rate) {
		if (this.reviewCnt <= 1) {
			this.reviewRate = 0.0;
			this.reviewCnt = 0;
		} else {
			this.reviewRate = (this.reviewRate * this.reviewCnt - rate) / (this.reviewCnt - 1);
			this.reviewCnt--;
		}
	}

}
