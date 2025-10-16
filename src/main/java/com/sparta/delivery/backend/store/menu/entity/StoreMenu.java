package com.sparta.delivery.backend.store.menu.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.sparta.delivery.backend.global.common.BaseEntity;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.order.entity.OrderMenu;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
	name = "p_store_menu",
	uniqueConstraints = @UniqueConstraint(columnNames = {"p_store_id", "sort_order"})
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

	@Column(length = 50)
	private int price;

	@Column(length = 500)
	private String description;

	@Column(name = "prep_time", length = 50)
	private String prepTime;

	@Column(name = "sort_order", nullable = false)
	private int sortOrder;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	@Column(name = "stock_status")
	private StockStatus stockStatus;

	// hiddenAt 로그 여부로 숨기기/보이기 설정하려면 Instant 가 적합
	@Column(name = "hidden_at")
	private Instant hiddenAt;

	@OneToMany(mappedBy = "storeMenu")
	private List<OrderMenu> orderMenus;

	@OneToMany(mappedBy = "menu")
	private List<Cart> carts = new ArrayList<>();

	@Builder
	private StoreMenu(ReqCreateStoreMenuDto reqCreateStoreMenuDto, Store store, Image image) {
		this.store = store;
		this.image = image;
		this.name = reqCreateStoreMenuDto.getName();
		this.price = reqCreateStoreMenuDto.getPrice();
		this.description = reqCreateStoreMenuDto.getDescription();
		this.prepTime = reqCreateStoreMenuDto.getPrepTime();
		this.stockStatus = reqCreateStoreMenuDto.getStockStatus();
		this.setHiddenAt(reqCreateStoreMenuDto.getIsHidden());
	}

	public void updateStoreMenu(ReqUpdateStoreMenuDto reqUpdateStoreMenuDto, Image image) {
		this.image = image;
		this.name = reqUpdateStoreMenuDto.getName();
		this.price = reqUpdateStoreMenuDto.getPrice();
		this.description = reqUpdateStoreMenuDto.getDescription();
		this.prepTime = reqUpdateStoreMenuDto.getPrepTime();
		this.stockStatus = reqUpdateStoreMenuDto.getStockStatus();
	}

	// 생성할 때는 순서 정하는게 없다. 이후에 수정해야함
	public void setSortOrder(int sortOrder) {
		if (sortOrder < 1)
			throw new IllegalArgumentException("sortOrder는 1 이상이어야 합니다.");
		this.sortOrder = sortOrder;
	}

	public void setHiddenAt(Boolean isHidden) {
		// True 일때는 Instant 로 변환해서 저장, 아닐 경우 null 로 저장
		this.hiddenAt = Boolean.TRUE.equals(isHidden) ? Instant.now() : null;
	}

	public String getImageUrl() {
		return image != null ? image.getImageUrl() : null;
	}

	// sortOrder 가 음수로 변경 → 조회 시 제외됨
	public void softDelete(Long deletedByUserId, int minSortOrder) {
		this.softDelete(deletedByUserId);
		this.sortOrder = minSortOrder - 1;
	}

	// 영구 삭제
	public void delete(Long deletedBy) {
		this.softDelete(deletedBy);

		if (this.image != null && this.image.getDeletedAt() == null) {
			this.image.softDelete(deletedBy);
		}

		this.carts.stream()
			.filter(cart -> cart.getDeletedAt() == null)
			.forEach(cart -> cart.delete(deletedBy));

		if (this.orderMenus != null) {
			this.orderMenus.stream()
				.filter(orderMenu -> orderMenu.getDeletedAt() == null)
				.forEach(orderMenu -> orderMenu.softDelete(deletedBy));
		}
	}
}
