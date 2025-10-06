package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface DongRepository extends JpaRepository<Dong, UUID> {
	Dong findByCode(String code);

	boolean existsByNameAndSigungu(String name, Sigungu sigungu);

	boolean existsByCode(String code);

	List<Dong> findAllBySigungu(Sigungu sigungu);

	boolean existsByNameAndSigunguAndIdNot(String name, Sigungu sigungu, UUID dongId);

	boolean existsByCodeAndIdNot(String code, UUID dongId);

	Optional<Dong> findByIdAndSigungu(UUID dongId, Sigungu sigungu);
  
  Optional<Dong> findByCode(String code);
  
}
