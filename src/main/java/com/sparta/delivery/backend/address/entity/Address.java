package com.sparta.delivery.backend.address.entity;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_region_dong_id", nullable = false)
	private Dong dong;

	@Column(name = "address", nullable = false)
	private String address;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_user_id", nullable = false)
	private User user;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault;

	@Builder
	private Address(Dong dong, String address, User user) {
		this.dong = dong;
		this.address = address;
		this.user = user;
		this.isDefault = true;
	}

	public void update(Dong dong, String address) {
		this.dong = dong;
		this.address = address;
	}

	public void setDefault() {
		this.isDefault = true;
	}

	public void unsetDefault() {
		this.isDefault = false;
	}

}
