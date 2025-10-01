package com.sparta.delivery.backend.store.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.image.entity.Image;

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
@Table(name = "p_store_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreImage extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "image_id")
	private Image image;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private StoreImageStatusEnum status;

	@Builder
	public StoreImage(Store store, Image image, StoreImageStatusEnum status) {
		this.store = store;
		this.image = image;
		this.status = status;
	}
}
