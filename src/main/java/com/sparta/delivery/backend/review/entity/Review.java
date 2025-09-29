package com.sparta.delivery.backend.review.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Entity;
import lombok.Getter;

@Entity
@Getter
public class Review extends BaseEntity {

	// customerId
	// imageId
	// storeId

	private String context;

	private int rate; // rate는 null값 존재X

}
