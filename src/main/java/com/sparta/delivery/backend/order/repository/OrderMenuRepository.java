package com.sparta.delivery.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.order.entity.OrderMenu;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {
}
