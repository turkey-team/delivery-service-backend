package com.sparta.delivery.backend.category.entity;

import java.time.LocalDateTime;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_store_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category extends BaseEntity {
	@Column(name = "name", nullable = false)
	private String name;
}
