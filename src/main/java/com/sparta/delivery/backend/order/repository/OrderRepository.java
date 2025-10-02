package com.sparta.delivery.backend.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
}
