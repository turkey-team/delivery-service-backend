package com.sparta.delivery.backend.region.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;

public interface SidoRepository extends JpaRepository<Sido, UUID> {

	boolean existsByName(String name);

	boolean existsByCode(String name);

	boolean existsByNameAndIdNot(String name, UUID sidoId);

	boolean existsByCodeAndIdNot(String code, UUID sidoId);

}
