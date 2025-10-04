package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface SigunguRepository extends JpaRepository<Sigungu, UUID> {

	boolean existsByNameInAndSidoAndDeletedAtIsNull(List<String> names, Sido sido);

	boolean existsByCodeInAndDeletedAtIsNull(List<String> codes);

	List<Sigungu> findAllBySidoAndDeletedAtIsNull(Sido sido);

	Optional<Sigungu> findByIdAndSidoAndDeletedAtIsNull(UUID sigunguId, Sido sido);

	boolean existsByNameAndSidoAndIdNotAndDeletedAtIsNull(String name, Sido sido, UUID sigunguId);

	boolean existsByCodeAndIdNotAndDeletedAtIsNull(String code, UUID sigunguId);

	Optional<Sigungu> findByIdAndDeletedAtIsNull(UUID sigunguId);

}
