package com.sparta.delivery.backend.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.order.entity.OrderMenu;

@Repository
public interface OrderMenuRepository extends JpaRepository<OrderMenu, UUID> {
}
