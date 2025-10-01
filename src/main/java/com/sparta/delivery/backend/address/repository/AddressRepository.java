package com.sparta.delivery.backend.address.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

	// @Query("select a from Address a join fetch a.user u where u.id = :userId order by a.createdAt desc")

	List<Address> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
