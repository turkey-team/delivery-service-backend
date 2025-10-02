package com.sparta.delivery.backend.customer.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class CustomerRepositoryImpl implements CustomerRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public CustomerRepositoryImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}
}

