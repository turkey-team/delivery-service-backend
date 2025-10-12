package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.backend.store.menu.entity.QStoreMenu;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import jakarta.persistence.EntityManager;

@Repository
public class StoreMenuRepositoryCustomImpl implements StoreMenuRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public StoreMenuRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	private BooleanExpression deletedCondition(QStoreMenu storeMenu, Instant deletedAt) {
		if (deletedAt == null) {
			return storeMenu.deletedAt.isNull(); // 활성 메뉴
		}
		return storeMenu.deletedAt.isNotNull(); // SoftDeleted 메뉴
	}

	@Override
	public Optional<StoreMenu> findByStoreIdAndDeletedAtIsNull(UUID storeId, UUID menuId, Instant deletedAt) {
		QStoreMenu sm = QStoreMenu.storeMenu;

		StoreMenu result = queryFactory
			.selectFrom(sm)
			.where(
				sm.store.id.eq(storeId),
				sm.id.eq(menuId),
				deletedCondition(sm, deletedAt),
				sm.sortOrder.gt(0)
			)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<StoreMenu> findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(UUID storeId, int sortOrder) {
		QStoreMenu sm = QStoreMenu.storeMenu;

		return queryFactory
			.selectFrom(sm)
			.where(
				sm.store.id.eq(storeId),
				sm.deletedAt.isNull(),
				sm.sortOrder.goe(sortOrder)
			)
			.orderBy(sm.sortOrder.asc())
			.fetch();
	}

	@Override
	public Integer findMaxSortOrderByStore(UUID storeId) {
		QStoreMenu sm = QStoreMenu.storeMenu;

		return queryFactory
			.select(sm.sortOrder.max())
			.from(sm)
			.where(sm.store.id.eq(storeId), sm.deletedAt.isNull())
			.fetchOne();
	}
}
