package com.sparta.delivery.backend.image.entity;

import java.util.UUID;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Image extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	private String imageUrl;

}
