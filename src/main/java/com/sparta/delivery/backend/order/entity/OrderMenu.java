package com.sparta.delivery.backend.order.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

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
@Table(name = "p_order_menu")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_store_menu_id", nullable = false)
	private StoreMenu storeMenu;

	@Builder
	public OrderMenu(Order order, StoreMenu storeMenu) {
		this.order = order;
		this.storeMenu = storeMenu;
	}
}
