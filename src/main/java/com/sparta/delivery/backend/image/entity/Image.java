package com.sparta.delivery.backend.image.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_image")
public class Image extends BaseEntity {

	private String imageUrl;

	@Builder
	private Image(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}