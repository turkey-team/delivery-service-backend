package com.sparta.delivery.backend.region.entity;

import com.sparta.delivery.backend.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_region_sigungu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sigungu extends BaseEntity {

	@ManyToOne
	@JoinColumn(name = "p_region_sido", nullable = false)
	private Sido sido;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "code", length = 3, nullable = false)
	private String code;

	@Builder
	private Sigungu(Sido sido, String name, String code) {
		this.sido = sido;
		this.name = name;
		this.code = code;
	}

	public void update(Sido sido, String name, String code) {
		if (sido != null) {
			this.sido = sido;
		}
		if (name != null) {
			this.name = name;
		}
		if (code != null) {
			this.code = code;
		}
	}

}
