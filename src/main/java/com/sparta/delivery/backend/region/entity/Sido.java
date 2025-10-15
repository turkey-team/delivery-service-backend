package com.sparta.delivery.backend.region.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.global.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_region_sido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sido extends BaseEntity {

	@Column(name = "name", length = 50, nullable = false)
	private String name;

	@Column(name = "code", length = 2, nullable = false)
	private String code;

	@OneToMany(mappedBy = "sido")
	private List<Sigungu> sigunguList = new ArrayList<>();

	@Builder
	private Sido(String name, String code) {
		this.name = name;
		this.code = code;
	}

	public void update(String name, String code) {
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
		sigunguList.forEach(sigungu -> sigungu.softDelete(loginUserId));
	}

}
