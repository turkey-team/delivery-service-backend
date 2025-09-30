package com.sparta.delivery.backend.store.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.sparta.delivery.backend.category.entity.Category;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
public class StoreCategory {
	@Id
	@UuidGenerator
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="store_id")
	private Store store;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Builder
	public StoreCategory(Store store, Category category) {
		this.store = store;
		this.category = category;
	}
}
