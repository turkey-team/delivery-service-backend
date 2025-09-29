package com.sparta.delivery.backend.order.entity;

import com.sparta.delivery.backend.common.BaseEntity;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import jakarta.persistence.*;

@Entity
@Table(name = "p_order_menu")
public class OrderMenu extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "p_store_menu_id", nullable = false)
	private StoreMenu storeMenu;
}
