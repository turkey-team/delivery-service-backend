package com.sparta.delivery.backend.store.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.MultiPolygon;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.global.common.util.PhoneNumberFormatter;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

	@OneToOne(mappedBy = "store", fetch = FetchType.LAZY)
	private StoreDetails storeDetails;

	@OneToMany(mappedBy = "store")
	private List<StoreMenu> storeMenus = new ArrayList<>();

	@OneToMany(mappedBy = "store")
	private List<Review> reviews = new ArrayList<>();

	@OneToMany(mappedBy = "store")
	private List<StoreImage> storeImages = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "p_address_id", nullable = false)
	private Address address;

	@Column(name = "delivery_zone", columnDefinition = "geometry(MultiPolygon, 4326)", nullable = false)
	private MultiPolygon deliveryZone;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<StoreCategory> storeCategories = new ArrayList<>();

	@Column(name = "name")
	private String name;

	@Column(name = "review_rate")
	private double reviewRate;

	@Column(name = "review_cnt")
	private int reviewCnt;

	@Column(name = "min_order_price")
	private Integer minOrderPrice;

	@Column(name = "delivery_fee")
	private int deliveryFee;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "status")
	private StoreStatusEnum status;

	@Column(name = "phone_number")
	private String phoneNumber;

	public void addReview(int rate) {
		this.reviewRate = (this.reviewRate * this.reviewCnt + rate) / (this.reviewCnt + 1);
		this.reviewCnt++;
	}

	public void updateReview(int oldRate, int newRate) {
		this.reviewRate = (this.reviewRate * this.reviewCnt - oldRate + newRate) / this.reviewCnt;
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

	@Builder
	public Store(Owner owner, String name, double reviewRate, Integer minOrderPrice, int deliveryFee, Address address,
		StoreStatusEnum status, String phoneNumber, MultiPolygon deliveryZone
	) {
		this.owner = owner;
		this.name = name;
		this.address = address;
		this.reviewRate = reviewRate;
		this.minOrderPrice = minOrderPrice;
		this.deliveryFee = deliveryFee;
		this.status = status;
		this.phoneNumber = phoneNumber;
		this.deliveryZone = deliveryZone;
	}

	public StoreImage addImage(Store store, Image image, StoreImageStatusEnum status) {
		return StoreImage.builder().store(store).image(image).status(status).build();
	}

	public void updateStoreInfo(ReqUpdateStoreInfoDto requestDto, Address address) {
		this.name = requestDto.getStoreName();
		this.address = address;
		this.phoneNumber = requestDto.getPhoneNumber();

	}

	public void updateStoreDetails(int deliveryFee, Integer minOrderPrice) {
		this.deliveryFee = deliveryFee;
		this.minOrderPrice = minOrderPrice;
	}

	public void updateStoreStatus(StoreStatusEnum status) {
		this.status = status;
	}

	public void delete(Long deletedBy) {

		this.softDelete(deletedBy);

		if (this.storeDetails != null) {
			storeDetails.softDelete(deletedBy);
		}

		this.storeImages.forEach(storeImage -> {
			storeImage.delete(deletedBy);
		});

		this.storeMenus.forEach(storeMenu -> {
			storeMenu.delete(deletedBy);
		});

		this.reviews.forEach(review -> review.deleteWithReply(deletedBy));

		this.storeCategories.forEach(storeCategory -> storeCategory.softDelete(deletedBy));

	}

	public String getFormattedPhoneNumber() {
		return PhoneNumberFormatter.formatPhoneNumber(phoneNumber);
	}
}
