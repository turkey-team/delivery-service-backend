package com.sparta.delivery.backend.image.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Image extends BaseEntity {

	private String imageUrl;

}
