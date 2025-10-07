package com.sparta.delivery.backend.region.entity;

import com.sparta.delivery.backend.common.BaseEntity;

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
@Table(name = "p_region_dong")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dong extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_region_sigungu", nullable = false)
	private Sigungu sigungu;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "code", length = 3, nullable = false)
	private String code;

	@Builder
	private Dong(Sigungu sigungu, String name, String code) {
		this.sigungu = sigungu;
		this.name = name;
		this.code = code;
		sigungu.getDongList().add(this);
	}

	public void update(Sigungu sigungu, String name, String code) {
		if (sigungu != null) {
			this.sigungu = sigungu;
		}
		if (name != null) {
			this.name = name;
		}
		if (code != null) {
			this.code = code;
		}
	}

}
