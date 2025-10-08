package com.sparta.delivery.backend.cart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.cart.dto.ResGetCartDto;
import com.sparta.delivery.backend.cart.entity.Cart;

import io.lettuce.core.dynamic.annotation.Param;

public interface CartRepository extends JpaRepository<Cart, UUID>, CartRepositoryCustom {

	boolean existsByCustomerIdAndDeletedAtIsNull(UUID id);

	boolean existsByDeletedAtIsNullAndMenuStoreId(UUID storeId);

	List<Cart> findAllByCustomerIdAndDeletedAtIsNull(UUID id);
}
