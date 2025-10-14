package com.sparta.delivery.backend.cart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.backend.cart.dto.ResGetCartDto;
import com.sparta.delivery.backend.cart.entity.QCart;
import com.sparta.delivery.backend.image.entity.QImage;
import com.sparta.delivery.backend.store.entity.QStore;
import com.sparta.delivery.backend.store.menu.entity.QStoreMenu;

import jakarta.persistence.EntityManager;

@Repository
public class CartRepositoryImpl implements CartRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	@Autowired
	public CartRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public ResGetCartDto findCartGroupByMenu(UUID cusomerId) {
		QStore store = QStore.store;
		QStoreMenu menu = QStoreMenu.storeMenu;
		QCart cart = QCart.cart;
		QImage image = QImage.image;

		// menu별로 groupBy, row Count
		List<ResGetCartDto.CartDto> cartDtos = queryFactory.select(
			Projections.constructor(
				ResGetCartDto.CartDto.class
				,cart.id.min()
				, menu.name
				, menu.price
				, cart.count()
				, image.imageUrl
			)
		)
			.from(cart)
			.join(cart.menu, menu)
			.leftJoin(menu.image, image)
			.where(cart.customer.id.eq(cusomerId)
				.and(cart.deletedAt.isNull())
			)
			.groupBy(menu.id, menu.name, menu.price)
			.fetch();

		if (cartDtos.isEmpty()) {
			return new ResGetCartDto(null,0,0,List.of());
		}

		ResGetCartDto.CartDto cartDto = cartDtos.get(0);
		StoreInfo storeInfo = queryFactory.select(Projections.constructor(
			StoreInfo.class
			,store.name
			,store.minOrderPrice
			,store.deliveryFee
		)).from(cart)
			.join(cart.menu,menu)
			.join(menu.store,store)
			.where(cart.customer.id.eq(cusomerId).and(cart.deletedAt.isNull()))
			.limit(1)
			.fetchOne();

		Integer safeMinOrderPrice = storeInfo.getMinOrderPrice() != null ? storeInfo.getMinOrderPrice() : 0;

		return new ResGetCartDto(storeInfo.getStoreName()
			,safeMinOrderPrice
			,storeInfo.deliveryFee
			,cartDtos
		);
	}

	public static class StoreInfo {
		private final String storeName;
		private final Integer minOrderPrice;
		private final int deliveryFee;

		public StoreInfo(String storeName, Integer minOrderPrice, int deliveryFee) {
			this.storeName = storeName;
			this.minOrderPrice = minOrderPrice;
			this.deliveryFee = deliveryFee;
		}

		public String getStoreName() {
			return storeName;
		}

		public Integer getMinOrderPrice() {
			return minOrderPrice;
		}

		public int getDeliveryFee() {
			return deliveryFee;
		}
	}
}
