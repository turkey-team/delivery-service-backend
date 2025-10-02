package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, UUID> {

	boolean existsByNameAndSido(String name, Sido sido);

	boolean existsByCode(String code);

	List<Sigungu> findAllBySido(Sido sido);

	Optional<Sigungu> findByIdAndSido(UUID sigunguId, Sido sido);

	boolean existsByNameAndSidoAndIdNot(String name, Sido sido, UUID sigunguId);

	boolean existsByCodeAndIdNot(String code, UUID sigunguId);

}
