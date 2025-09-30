package com.sparta.delivery.backend.category.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@Column(name = "name", nullable = false)
	private String name;

	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<Store> stores;

	@Builder
	public Category(String name) {
		this.name = name;
	}

}
