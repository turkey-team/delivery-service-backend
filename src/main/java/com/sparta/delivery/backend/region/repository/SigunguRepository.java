package com.sparta.delivery.backend.region.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, UUID> {

	boolean existsByName(String name);

	boolean existsByCode(String code);

}
