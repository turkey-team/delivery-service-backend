package com.sparta.delivery.backend.customer.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sparta.delivery.backend.customer.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
	Optional<Customer> findByUserId(Long id);
}
