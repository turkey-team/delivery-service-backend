package com.sparta.delivery.backend.address.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
