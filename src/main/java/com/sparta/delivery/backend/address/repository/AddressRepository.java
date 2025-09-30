package com.sparta.delivery.backend.address.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.address.entity.Address;

public interface AddressRepository extends JpaRepository<Address, UUID> {
}
