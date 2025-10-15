package com.sparta.delivery.backend.region.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_region_sido", nullable = false)
	private Sido sido;

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "code", length = 3, nullable = false)
	private String code;

	@OneToMany(mappedBy = "sigungu")
	private List<Dong> dongList = new ArrayList<>();

	@Builder
	private Sigungu(Sido sido, String name, String code) {
		this.sido = sido;
		this.name = name;
		this.code = code;
		sido.getSigunguList().add(this);
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

	@Override
	public void softDelete(Long loginUserId) {
		super.softDelete(loginUserId);
		dongList.forEach(dong -> dong.softDelete(loginUserId));
	}

}
