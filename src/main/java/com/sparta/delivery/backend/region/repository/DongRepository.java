package com.sparta.delivery.backend.region.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Dong;

public interface DongRepository extends JpaRepository<Dong, UUID> {
	Optional<Dong> findByCode(String code);
}
