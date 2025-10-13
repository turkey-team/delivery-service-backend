package com.sparta.delivery.backend.order.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>{
	Page<Order> findByCustomerId(UUID customerId, Pageable pageable);
	Page<Order> findByStoreOwnerId(UUID ownerId, Pageable pageable);
}
