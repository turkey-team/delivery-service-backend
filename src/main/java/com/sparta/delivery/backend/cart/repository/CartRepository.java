package com.sparta.delivery.backend.cart.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.cart.dto.ResGetCartDto;
import com.sparta.delivery.backend.cart.entity.Cart;

import io.lettuce.core.dynamic.annotation.Param;

public interface CartRepository extends JpaRepository<Cart, UUID>, CartRepositoryCustom {

	boolean existsByCustomerIdAndDeletedAtIsNull(UUID id);

	boolean existsByDeletedAtIsNullAndMenuStoreId(UUID storeId);

	List<Cart> findAllByCustomerIdAndDeletedAtIsNull(UUID id);

	@Query("SELECT DISTINCT c FROM Cart c JOIN FETCH c.customer cu JOIN FETCH cu.user WHERE c.id = :cartId")
	Optional<Cart> findByIdWithCustomerAndUser(@Param("cartId") UUID cartId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE Cart c SET c.deletedAt = :deletedAt " +
		"WHERE c.menu.id IN (SELECT sm.id FROM StoreMenu sm WHERE sm.store.id = :storeId)")
	void bulkSoftDeleteCartByStoreId(Long storeId, Instant deletedAt);
  
	List<Cart> findAllByMenuIdAndDeletedAtIsNull(UUID menuId);
}
