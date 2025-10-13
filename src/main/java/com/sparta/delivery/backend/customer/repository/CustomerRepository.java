package com.sparta.delivery.backend.customer.repository;

import java.util.Optional;
import java.util.UUID;

import com.sparta.delivery.backend.customer.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByUserId(Long id);

	Optional<Customer> findByUserIdAndDeletedAtIsNull(Long userId);

	@Query("SELECT c FROM Customer c " +
		"JOIN FETCH c.user " +
		"WHERE c.user.publicId = :publicId " +
		"AND c.deletedAt IS NULL")
	Optional<Customer> findByUserPublicIdAndDeletedAtNull(@Param("publicId") UUID publicId);
}
