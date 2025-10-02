package com.sparta.delivery.backend.image.entity;

import java.util.ArrayList;
import java.util.List;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.store.entity.StoreImage;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_image")
public class Image extends BaseEntity {

	private String imageUrl;

	private String imageName;

	@OneToMany(mappedBy = "image", fetch = FetchType.LAZY)
	private List<StoreImage> storeImages = new ArrayList<>();

	@Builder
	private Image(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}