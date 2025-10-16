package com.sparta.delivery.backend.store.entity;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sparta.delivery.backend.global.common.BaseEntity;
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
	@JoinColumn(name = "p_store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_image_id")
	private Image image;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "status")
	private StoreImageStatusEnum status;

	@Builder
	public StoreImage(Store store, Image image, StoreImageStatusEnum status) {
		this.store = store;
		this.image = image;
		this.status = status;
	}

	public void delete(Long deletedBy) {
		this.softDelete(deletedBy);
		this.image.softDelete(deletedBy);
	}
}
