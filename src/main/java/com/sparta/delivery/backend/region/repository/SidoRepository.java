package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;

public interface SidoRepository extends JpaRepository<Sido, UUID> {

	boolean existsByNameInAndDeletedAtIsNull(List<String> names);

	boolean existsByCodeInAndDeletedAtIsNull(List<String> codes);

	List<Sido> findAllByDeletedAtIsNull();

	Optional<Sido> findByIdAndDeletedAtIsNull(UUID sidoId);

	boolean existsByNameAndIdNotAndDeletedAtIsNull(String name, UUID sidoId);

	boolean existsByCodeAndIdNotAndDeletedAtIsNull(String code, UUID sidoId);

}
