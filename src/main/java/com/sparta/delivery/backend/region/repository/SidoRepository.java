package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;

public interface SidoRepository extends JpaRepository<Sido, UUID> {

	boolean existsByNameIn(List<String> names);

	boolean existsByCodeIn(List<String> codes);

	boolean existsByNameAndIdNot(String name, UUID sidoId);

	boolean existsByCodeAndIdNot(String code, UUID sidoId);

}
