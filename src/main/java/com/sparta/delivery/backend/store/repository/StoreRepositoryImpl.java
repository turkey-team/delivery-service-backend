// package com.sparta.delivery.backend.store.repository;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.UUID;
// import java.util.stream.Collectors;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageImpl;
// import org.springframework.data.domain.Pageable;
// import org.springframework.stereotype.Repository;
//
// import com.querydsl.core.BooleanBuilder;
// import com.querydsl.core.Tuple;
// import com.querydsl.core.types.OrderSpecifier;
// import com.querydsl.jpa.impl.JPAQueryFactory;
// import com.sparta.delivery.backend.category.entity.QCategory;
// import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
// import com.sparta.delivery.backend.store.entity.QStore;
// import com.sparta.delivery.backend.store.entity.QStoreCategory;
//
// import jakarta.persistence.EntityManager;
//
// @Repository
// public class StoreRepositoryImpl implements StoreRepositoryCustom {
//
// 	private final JPAQueryFactory queryFactory;
//
// 	@Autowired
// 	public StoreRepositoryImpl(EntityManager em) {
// 		this.queryFactory = new JPAQueryFactory(em);
// 	}
//
// 	@Override
// 	public Page<ResGetListStoreDto> getStores(Pageable pageable, String sort, String keyword, UUID categoryId){
// 		QStore store = QStore.store;
// 		QStoreCategory storeCategory = QStoreCategory.storeCategory;
// 		QCategory category = QCategory.category;
//
// 		BooleanBuilder condition = new BooleanBuilder();
// 		condition.and(store.deletedAt.isNull());
// 		condition.and(category.deletedAt.isNull());
//
// 		boolean hasKeyword = keyword != null && !keyword.isBlank();
// 		boolean hasCategory = categoryId != null;
//
// 		// 검색어가 존재하는 경우
// 		if (hasKeyword) {
// 			// 가게 이름 또는 카테고리 이름에 검색어가 포함
// 			condition.and(store.name.containsIgnoreCase(keyword)
// 				.or(category.name.containsIgnoreCase(keyword))
// 			);
// 		}
//
// 		// 카테고리 ID가 존재하는 경우
// 		if (hasCategory) {
// 			// 해당 카테고리 ID와 일치하는 가게만 조회하도록 조건을 추가
// 			condition.and(category.id.eq(categoryId));
// 		}
//
// 		List<OrderSpecifier<?>> allOrderSpecifiers = getOrderSpecifiers(sort, store);
//
// 		List<Tuple> results = queryFactory
// 			// SELECT
// 			.select(
// 				store.id,
// 				store.name,
// 				store.reviewCnt,
// 				store.reviewRate,
// 				store.deliveryFee,
// 				store.minOrderPrice,
// 				store.status,
// 				store.createdAt
// 			)
// 			.from(store)
// 			.innerJoin(store.storeCategories, storeCategory)
// 			.innerJoin(storeCategory.category, category)
// 			.where(condition)
// 			// 중복된 결과를 제거
// 			.distinct()
// 			.orderBy(allOrderSpecifiers.toArray(new OrderSpecifier[0]))
// 			// 페이징 처리
// 			.offset(pageable.getOffset())
// 			.limit(pageable.getPageSize())
// 			.fetch();
//
// 		List<ResGetListStoreDto> storeDtos = results.stream()
// 			.map(tuple -> ResGetListStoreDto.builder()
// 				.storeId(tuple.get(store.id))
// 				.name(tuple.get(store.name))
// 				.reviewCnt(tuple.get(store.reviewCnt))
// 				.reviewRate(tuple.get(store.reviewRate))
// 				.deliveryFee(tuple.get(store.deliveryFee))
// 				.minOrderPrice(tuple.get(store.minOrderPrice))
// 				.status(tuple.get(store.status))
// 				.build())
// 			.collect(Collectors.toList());
//
// 		// 전체 count
// 		Long total = queryFactory
// 			.select(store.countDistinct())
// 			.from(store)
// 			.innerJoin(store.storeCategories, storeCategory)
// 			.innerJoin(storeCategory.category, category)
// 			.where(condition)
// 			.fetchOne();
//
// 		return new PageImpl<>(storeDtos, pageable, total != null ? total : 0 );
// 	}
//
// 	private List<OrderSpecifier<?>> getOrderSpecifiers(String sort, QStore store) {
// 		List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//
// 		switch (sort) {
// 			case "name":
// 				orderSpecifiers.add(store.name.asc()); // 이름(오름차순)
// 				break;
// 			case "reviewRate":
// 				orderSpecifiers.add(store.reviewRate.desc()); // 별점(내림차순)
// 				break;
// 			case "reviewCount":
// 				orderSpecifiers.add(store.reviewCnt.desc()); // 리뷰개수(내림차순)
// 				break;
// 			case "newest":
// 				orderSpecifiers.add(store.createdAt.desc()); // 등록일(내림차순)
// 				break;
// 			case "oldest":
// 				orderSpecifiers.add(store.createdAt.asc()); // 등록일(오름차순)
// 				break;
// 			default:
// 				orderSpecifiers.add(store.createdAt.desc());
// 		}
// 		return orderSpecifiers;
// 	}
// }
