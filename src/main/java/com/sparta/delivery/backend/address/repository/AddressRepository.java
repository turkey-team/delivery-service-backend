package com.sparta.delivery.backend.address.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

	List<Address> findAllByUserIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long userId);
	Optional<Address> findByIdAndDeletedAtIsNull(UUID id);
	Optional<Address> findByUserIdAndIsDefaultTrueAndDeletedAtIsNull(Long userId);
}
