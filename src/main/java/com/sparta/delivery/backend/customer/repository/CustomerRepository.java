package com.sparta.delivery.backend.customer.repository;

import java.util.Optional;
import java.util.UUID;

import com.sparta.delivery.backend.customer.entity.Customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByUserId(Long id);

	Optional<Customer> findByUserIdAndDeletedAtIsNull(Long userId);
}
