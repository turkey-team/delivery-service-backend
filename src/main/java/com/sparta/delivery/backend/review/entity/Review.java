package com.sparta.delivery.backend.review.entity;

import java.util.UUID;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class Review extends BaseEntity {

	@Id
	@GeneratedValue
	private UUID id;

	// customerId
	// imageId
	// storeId

	private String context;

	private int rate; // rate는 null값 존재X

}
