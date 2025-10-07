package com.sparta.delivery.backend.region.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;

public interface DongRepository extends JpaRepository<Dong, UUID> {
	Dong findByCode(String code);

	@Query("""
		SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END
		FROM Dong d
		WHERE d.name IN :names AND d.sigungu = :sigungu AND d.deletedAt IS NULL
		""")
	boolean existsByNameInAndSigunguCustom(@Param("names") List<String> names, @Param("sigungu") Sigungu sigungu);

	@Query("""
		SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END
		FROM Dong d
		WHERE d.code IN :codes AND d.deletedAt IS NULL
		""")
	boolean existsByCodeInCustom(@Param("codes") List<String> codes);

	@Query("""
		SELECT d
		FROM Dong d
		WHERE d.sigungu = :sigungu AND d.deletedAt IS NULL
		""")
	List<Dong> findAllBySigunguCustom(@Param("sigungu") Sigungu sigungu);

	@Query("""
		SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END
		FROM Dong d
		WHERE d.name = :name AND d.sigungu = :sigungu AND d.id != :dongId AND d.deletedAt IS NULL
		""")
	boolean existsByNameAndSigunguAndIdNotCustom(@Param("name") String name,
		@Param("sigungu") Sigungu sigungu, @Param("dongId") UUID dongId);

	@Query("""
		SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END
		FROM Dong d
		WHERE d.code = :code AND d.id != :dongId AND d.deletedAt IS NULL
		""")
	boolean existsByCodeAndIdNotCustom(@Param("code") String code, @Param("dongId") UUID dongId);

	@Query("""
		SELECT d
		FROM Dong d
		WHERE d.id = :dongId AND d.sigungu = :sigungu AND d.deletedAt IS NULL
		""")
	Optional<Dong> findByIdAndSigunguCustom(@Param("dongId") UUID dongId, @Param("sigungu") Sigungu sigungu);

	Optional<Dong> findByCode(String code);

}
