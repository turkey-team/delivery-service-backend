package com.sparta.delivery.backend.store.entity;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.global.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StoreCategory extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="p_store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_category_id")
	private Category category;

	@Builder
	public StoreCategory(Store store, Category category) {
		this.store = store;
		this.category = category;
	}

}
