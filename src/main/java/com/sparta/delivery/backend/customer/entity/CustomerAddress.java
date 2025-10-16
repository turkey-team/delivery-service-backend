package com.sparta.delivery.backend.customer.entity;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.region.entity.Dong;

import jakarta.persistence.CascadeType;
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
@Getter
@Table(name = "p_customer_address")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerAddress extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_customer_id", nullable = false)
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "p_address_id", nullable = false)
	private Address address;

	@Column(name = "is_default", nullable = false)
	private Boolean isDefault = false;

	@Column(name = "nickname")  // "집", "회사" 등
	private String nickname;

	@Builder
	private CustomerAddress(Customer customer, Address address, Boolean isDefault, String nickname) {
		this.customer = customer;
		this.address = address;
		this.isDefault = isDefault != null ? isDefault : false;
		this.nickname = nickname;
	}

	public void setDefault() {
		this.isDefault = true;
	}

	public void unsetDefault() {
		this.isDefault = false;
	}

	public void updateNickname(String nickname) {
		this.nickname = nickname;
	}

	public void updateCustomer(Customer customer) {
		this.customer = customer;
	}

	public void updateAddress(Long updateBy, Address address, Boolean isDefault, String nickname) {
		this.address.delete(updateBy);
		this.address = address;
		this.isDefault = isDefault;
		this.nickname = nickname;
	}

	public void delete(Long deletedBy) {
		this.softDelete(deletedBy);
		this.address.delete(deletedBy);
	}

	public String getFullAddress() {
		return address.getFullAddress();
	}

	public String getDongCode() {
		return address.getDongCode();
	}

	public Dong getDong() {
		return address.getDong();
	}
}
