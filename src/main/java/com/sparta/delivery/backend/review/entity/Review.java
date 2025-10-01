package com.sparta.delivery.backend.review.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "p_review")
@Setter
public class Review extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer customer;

	@Column(name = "image_url", nullable = true)
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	private Store store;

	@Column(name = "context", nullable = true)
	private String context;

	@Column(name = "rate", nullable = false)
	private int rate; // rate는 null값 존재X

	@Builder
	private Review(Customer customer, String imageUrl, Store store, String context, int rate) {
		this.customer = customer;
		this.imageUrl = imageUrl;
		this.store = store;
		this.context = context;
		this.rate = rate;
	}

	public void update(String context, int rate, String imageUrl) {
		this.context = context;
		this.rate = rate;
		this.imageUrl = imageUrl;
	}

}
