package com.sparta.delivery.backend.region.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface DongRepository extends JpaRepository<Dong, UUID> {

	boolean existsByNameAndSigungu(String name, Sigungu sigungu);

	boolean existsByCode(String code);

}
