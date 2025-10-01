package com.sparta.delivery.backend.review.repository;

import java.time.LocalDate;
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
import com.sparta.delivery.backend.review.dto.QReviewViewDto;
import com.sparta.delivery.backend.review.dto.ReviewSearchCondition;
import com.sparta.delivery.backend.review.dto.ReviewViewDto;
import com.sparta.delivery.backend.review.entity.QReview;

import jakarta.persistence.EntityManager;

public class ReviewRepositoryImpl implements ReviewRepoistoryCustom {

	private final JPAQueryFactory queryFactory;

	public ReviewRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<ReviewViewDto> findReviews(UUID storeId, ReviewSearchCondition condition, Pageable pageable) {
		QReview review = QReview.review;

		JPAQuery<ReviewViewDto> query = queryFactory.select(new QReviewViewDto(
				review.id, review.customer.id,
				review.store.id, review.image.imageUrl,
				review.context, review.rate
			))
			.from(review)
			.where(review.store.id.eq(storeId),
				rateBetween(condition.getMinRate(), condition.getMaxRate()),
				containsContext(condition.getContext()),
				createdBetween(condition.getStartDate(), condition.getEndDate()))
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

		QueryResults<ReviewViewDto> results = query.fetchResults();
		List<ReviewViewDto> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);

	}

	@Override
	public Page<ReviewViewDto> findMyOwnReviews(UUID customerId, ReviewSearchCondition condition, Pageable pageable) {
		QReview review = QReview.review;

		JPAQuery<ReviewViewDto> query = queryFactory.select(new QReviewViewDto(
				review.id, review.customer.id,
				review.store.id, review.image.imageUrl,
				review.context, review.rate
			))
			.from(review)
			.where(review.customer.id.eq(customerId),
				rateBetween(condition.getMinRate(), condition.getMaxRate()),
				containsContext(condition.getContext()),
				createdBetween(condition.getStartDate(), condition.getEndDate()))
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

		QueryResults<ReviewViewDto> results = query.fetchResults();
		List<ReviewViewDto> content = results.getResults();
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
		if (context.isEmpty()) {
			return null;
		}

		return review.context.contains(context);
	}

	private BooleanExpression createdBetween(LocalDate startDate, LocalDate endDate) {
		QReview review = QReview.review;

		if (startDate == null && endDate == null) {
			return null;
		}

		if (startDate == null) {
			return review.createdAt.loe(endDate.atTime(23, 59, 59));
		}
		if (endDate == null) {
			return review.createdAt.goe(startDate.atStartOfDay());
		}

		return review.createdAt.between(
			startDate.atStartOfDay(), endDate.atTime(23, 59, 59)
		);
	}

}
