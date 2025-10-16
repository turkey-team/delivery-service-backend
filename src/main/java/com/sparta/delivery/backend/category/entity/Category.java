package com.sparta.delivery.backend.category.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.store.entity.StoreCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {
	@Column(name = "name", length = 100, nullable = false, unique = true)
	private String name;

	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
	private List<StoreCategory> storeCategories = new ArrayList<>();

	@Builder
	public Category(String name) {
		this.name = name;
	}

	public void updateCategoryName(String name) {
		this.name = name;
	}
}
