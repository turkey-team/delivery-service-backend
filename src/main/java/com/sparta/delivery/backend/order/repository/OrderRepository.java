package com.sparta.delivery.backend.order.repository;

import java.nio.channels.FileChannel;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.order.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID>, OrderRepositoryCustom {
	Page<Order> findOrdersByUserId(UUID userId, Pageable pageable);
}
