package com.sparta.delivery.backend.store.menu.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.backend.store.menu.entity.QStoreMenu;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;

import jakarta.persistence.EntityManager;

public class StoreMenuRepositoryCustomImpl implements StoreMenuRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public StoreMenuRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	// 조건에 따른 deletedAt 처리
	private BooleanExpression deletedCondition(QStoreMenu storeMenu, Instant deletedAt) {
		if (deletedAt == null) {
			return storeMenu.deletedAt.isNull(); // 활성 메뉴만
		}
		return storeMenu.deletedAt.isNotNull();   // 삭제된 메뉴만
	}

	// SortOrder 가 음수가 아닌 값들만 고려
	private BooleanExpression activeCondition(QStoreMenu storeMenu) {
		return storeMenu.sortOrder.gt(0); // sortOrder가 양수인 것만
	}

	// 여러 메뉴 조회 (페이징)
	@Override
	public Page<StoreMenu> findAllByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable, Instant deletedAt) {
		QStoreMenu storeMenu = QStoreMenu.storeMenu;

		// 조회 조건: deleted 여부 + sortOrder 양수
		BooleanExpression condition = storeMenu.store.id.eq(storeId)
			.and(deletedCondition(storeMenu, deletedAt))
			.and(activeCondition(storeMenu));

		// 실제 데이터 조회
		List<StoreMenu> results = queryFactory
			.selectFrom(storeMenu)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 개수 조회
		Long totalCount = queryFactory
			.select(storeMenu.id.count())
			.from(storeMenu)
			.where(condition)
			.fetchOne();

		long total = totalCount != null ? totalCount : 0L;

		return PageableExecutionUtils.getPage(results, pageable, () -> total);
	}

	// 단일 메뉴 조회
	@Override
	public Optional<StoreMenu> findByStoreIdAndDeletedAtIsNull(UUID storeId, UUID menuId, Instant deletedAt) {
		QStoreMenu storeMenu = QStoreMenu.storeMenu;

		// 조회 조건: deleted 여부 + sortOrder 양수
		BooleanExpression condition = storeMenu.store.id.eq(storeId)
			.and(storeMenu.id.eq(menuId))
			.and(deletedCondition(storeMenu, deletedAt))
			.and(activeCondition(storeMenu));

		StoreMenu result = queryFactory
			.selectFrom(storeMenu)
			.where(condition)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	// 최대 sortOrder 조회
	@Override
	public Integer findMaxSortOrderByStore(UUID storeId) {
		QStoreMenu sm = QStoreMenu.storeMenu;
		Integer maxSort = queryFactory
			.select(sm.sortOrder.max())
			.from(sm)
			.where(sm.store.id.eq(storeId), sm.deletedAt.isNull(), sm.sortOrder.gt(0))
			.fetchOne();
		return maxSort != null ? maxSort : 0;
	}

	// 전체 메뉴 정렬 조회
	@Override
	public List<StoreMenu> findAllByStoreIdOrderBySortAsc(UUID storeId) {
		QStoreMenu sm = QStoreMenu.storeMenu;
		return queryFactory
			.selectFrom(sm)
			.where(sm.store.id.eq(storeId), sm.deletedAt.isNull(), sm.sortOrder.gt(0))
			.orderBy(sm.sortOrder.asc())
			.fetch();
	}
}
