package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, UUID> {

	boolean existsByName(String name);

	boolean existsByCode(String code);

	List<Sigungu> findAllBySido(Sido sido);

	Optional<Sigungu> findByIdAndSido(UUID sigunguId, Sido sido);

	boolean existsByNameAndIdNot(String name, UUID sidoId);

	boolean existsByCodeAndIdNot(String code, UUID sidoId);

}
