package com.sparta.delivery.backend.address.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sparta.delivery.backend.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {

	@Query("select a from Address a join fetch a.customer c join c.user u where u.id = :userId")
	List<Address> findAllByUserId(Long userId);
}
