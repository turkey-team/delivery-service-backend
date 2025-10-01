package com.sparta.delivery.backend.cart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {
}
