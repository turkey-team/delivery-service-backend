package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface DongRepository extends JpaRepository<Dong, UUID> {

	boolean existsByNameInAndSigunguAndDeletedAtIsNull(List<String> names, Sigungu sigungu);

	boolean existsByCodeInAndDeletedAtIsNull(List<String> codes);

	List<Dong> findAllBySigunguAndDeletedAtIsNull(Sigungu sigungu);

	boolean existsByNameAndSigunguAndIdNotAndDeletedAtIsNull(String name, Sigungu sigungu, UUID dongId);

	boolean existsByCodeAndIdNotAndDeletedAtIsNull(String code, UUID dongId);

	Optional<Dong> findByIdAndSigunguAndDeletedAtIsNull(UUID dongId, Sigungu sigungu);

	Optional<Dong> findByCode(String code);

}
