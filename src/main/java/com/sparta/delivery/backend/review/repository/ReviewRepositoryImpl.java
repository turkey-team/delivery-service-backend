package com.sparta.delivery.backend.review.repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.delivery.backend.review.dto.QResViewReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.entity.QReview;

import jakarta.persistence.EntityManager;

public class ReviewRepositoryImpl implements ReviewRepoistoryCustom {

	private final JPAQueryFactory queryFactory;

	public ReviewRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<ResViewReviewDto> findReviews(UUID storeId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		QReview review = QReview.review;

		JPAQuery<ResViewReviewDto> query = queryFactory.select(new QResViewReviewDto(
				review.id, review.customer.id,
				review.store.id, review.imageUrl,
				review.context, review.rate, review.createdAt, review.createdBy
			))
			.from(review)
			.where(review.store.id.eq(storeId),
				rateBetween(condition.getMinRate(), condition.getMaxRate()),
				containsContext(condition.getContext()),
				createdBetween(condition.getStartDate(), condition.getEndDate()),
				review.deletedAt.isNull())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		// 정렬 조건을 담은 list
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		if (pageable.getSort().isSorted()) {
			for (Sort.Order order : pageable.getSort()) {
				if (order.getProperty().equals("rate")) {
					orders.add(order.isAscending() ? review.rate.asc() : review.rate.desc());
				} else if (order.getProperty().equals("createdAt")) {
					orders.add(order.isAscending() ? review.createdAt.asc() : review.createdAt.desc());
				}
			}
		}

		if (orders.isEmpty()) {
			orders.add(review.createdAt.desc());
		}

		query.orderBy(orders.toArray(new OrderSpecifier[0]));

		QueryResults<ResViewReviewDto> results = query.fetchResults();
		List<ResViewReviewDto> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);

	}

	@Override
	public Page<ResViewReviewDto> findMyOwnReviews(UUID customerId, ReviewRepositorySearchConditionDto condition,
		Pageable pageable) {
		QReview review = QReview.review;

		JPAQuery<ResViewReviewDto> query = queryFactory.select(new QResViewReviewDto(
				review.id, review.customer.id,
				review.store.id, review.imageUrl,
				review.context, review.rate, review.createdAt, review.createdBy
			))
			.from(review)
			.where(review.customer.id.eq(customerId),
				rateBetween(condition.getMinRate(), condition.getMaxRate()),
				containsContext(condition.getContext()),
				createdBetween(condition.getStartDate(), condition.getEndDate()),
				review.deletedAt.isNull())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		// 정렬 조건을 담은 list
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		if (pageable.getSort().isSorted()) {
			for (Sort.Order order : pageable.getSort()) {
				if (order.getProperty().equals("rate")) {
					orders.add(order.isAscending() ? review.rate.asc() : review.rate.desc());
				} else if (order.getProperty().equals("createdAt")) {
					orders.add(order.isAscending() ? review.createdAt.asc() : review.createdAt.desc());
				}
			}
		}

		if (orders.isEmpty()) {
			orders.add(review.createdAt.desc());
		}

		query.orderBy(orders.toArray(new OrderSpecifier[0]));

		QueryResults<ResViewReviewDto> results = query.fetchResults();
		List<ResViewReviewDto> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression rateBetween(Integer minRate, Integer maxRate) {
		QReview review = QReview.review;
		if (minRate == null && maxRate == null) {
			return null;
		}
		if (minRate == null) {
			return review.rate.loe(maxRate);
		}
		if (maxRate == null) {
			return review.rate.goe(minRate);
		}

		return review.rate.between(minRate, maxRate);
	}

	private BooleanExpression containsContext(String context) {
		QReview review = QReview.review;
		if (context == null || context.isEmpty()) {
			return null;
		}

		return review.context.contains(context);
	}

	private BooleanExpression createdBetween(Instant startDate, Instant endDate) {
		QReview review = QReview.review;
		if (startDate == null && endDate == null)
			return null;
		if (startDate == null)
			return review.createdAt.loe(endDate);
		if (endDate == null)
			return review.createdAt.goe(startDate);
		return review.createdAt.between(startDate, endDate);
	}
}