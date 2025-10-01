package com.sparta.delivery.backend.store.menu.entity;

import java.time.Instant;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.dto.ReqStoreMenuOwnerDto;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
	name = "p_store_menu",
	uniqueConstraints = @UniqueConstraint(columnNames = {"p_store_id", "sort_order"})
	/**
		스토어(p_store_id) 단위로 sort_order Unique 보장
		-> Order의 수는 겹치면 안되며 1부터 시작해야 한다.
		1. jakarta.validation으로 @Min Annotation 사용
		2. DB 생성 시
			ALTER TABLE p_store_menu
			ADD CONSTRAINT chk_sort_order CHECK (sort_order >= 1);
		비즈니스 로직:
			Integer maxSort = repository.findMaxSortOrderByStore(storeId);
			menu.setSortOrder(maxSort + 1);
	*/
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreMenu extends BaseEntity {

	// FK 매핑: p_store_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_store_id", nullable = false)
	private Store store;

	// FK 매핑: p_image_id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_image_id")
	private Image image;

	@Column(nullable = false, length = 100, unique = true) // 메뉴 이름은 중복 불가
	private String name;

	@Column(nullable = false)
	private int price;

	@Column(length = 500)
	private String description;

	@Column(name = "prep_time", length = 50)
	private String prepTime;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Enumerated(EnumType.STRING)
	@Column(name = "stock_status", length = 20)
	private StockStatus stockStatus;

	// hiddenAt 로그 여부로 숨기기/보이기 설정하려면 Instant 가 적합
	@Column(name = "hidden_at")
	private Instant hiddenAt;

	@Builder
	private StoreMenu(ReqStoreMenuOwnerDto reqStoreMenuOwnerDto, Store store, Image image) {
		this.store = store;
		this.image = image;
		this.name = reqStoreMenuOwnerDto.getName();
		this.price = reqStoreMenuOwnerDto.getPrice();
		this.description = reqStoreMenuOwnerDto.getDescription();
		this.prepTime = reqStoreMenuOwnerDto.getPrepTime();
		this.stockStatus = reqStoreMenuOwnerDto.getStockStatus();
	}
}
