package com.sparta.delivery.backend.address.entity;

import org.locationtech.jts.geom.Point;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.region.entity.Dong;

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

	@Column(name = "full_address", nullable = false)
	private String fullAddress;

	@Column(name = "location", columnDefinition = "geometry(Point, 4326)", nullable = false)
	private Point location;

	@Builder
	private Address(Dong dong, String fullAddress, Point location) {
		this.dong = dong;
		this.fullAddress = fullAddress;
		this.location = location;
	}

	public void update(Dong dong, String address) {
		this.dong = dong;
		this.fullAddress = address;
	}

	public void delete(Long deletedBy) {
		this.softDelete(deletedBy);
	}

	public String getDongCode() {
		return dong.getCode();
	}
}
