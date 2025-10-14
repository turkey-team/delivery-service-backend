package com.sparta.delivery.backend.region.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
public class SigunguRepositoryTest {

	@Autowired
	private SigunguRepository sigunguRepository;

	@Autowired
	private TestEntityManager em;

	private User user;
	private Sido seoul;
	private Sigungu gangnam;
	private Sigungu gangdong;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.username("manager")
			.password("1234")
			.role(UserRoleEnum.MANAGER)
			.build();
		seoul = Sido.builder()
			.name("서울특별시")
			.code("11")
			.build();

		em.persist(user);
		em.persist(seoul);
		em.flush();
		em.clear();
	}

	// 필요한 테스트에서 호출하여 생성
	void createSigungus() {
		gangnam = Sigungu.builder()
			.sido(seoul)
			.name("강남구")
			.code("680")
			.build();
		gangdong = Sigungu.builder()
			.sido(seoul)
			.name("강동구")
			.code("740")
			.build();

		em.persist(gangnam);
		em.persist(gangdong);
		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("existsByNameInAndSidoCustom 메서드는")
	class ExistsByNameInAndSidoCustomTest {

		@Test
		@DisplayName("해당 시/도에 이름이 같은 시/군/구가 하나라도 존재하면, true를 반환한다.")
		void returnTrueWhenNameExistsInSido() {
			createSigungus();

			List<String> names = List.of("강서구", "강남구");
			boolean exists = sigunguRepository.existsByNameInAndSidoCustom(names, seoul);
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/도에 이름이 같은 시/군/구가 존재하지 않으면, false를 반환한다.")
		void returnTrueWhenNameNotExistsInSido() {
			createSigungus();

			List<String> names = List.of("강서구", "강북구");
			boolean exists = sigunguRepository.existsByNameInAndSidoCustom(names, seoul);
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			List<String> names = List.of("강서구", "강남구");
			boolean exists = sigunguRepository.existsByNameInAndSidoCustom(names, seoul);
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeInAndSidoCustom 메서드는")
	class ExistsByCodeInCustomTest {

		@Test
		@DisplayName("해당 시/도에 코드가 같은 시/군/구가 하나라도 존재하면, true를 반환한다.")
		void returnTrueWhenCodeExistsInSido() {
			createSigungus();

			List<String> codes = List.of("500", "680");
			boolean exists = sigunguRepository.existsByCodeInAndSidoCustom(codes, seoul);
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/도에 코드가 같은 시/군/구가 존재하지 않으면, false를 반환한다.")
		void returnTrueWhenCodeNotExistsInSido() {
			createSigungus();

			List<String> codes = List.of("500", "230");
			boolean exists = sigunguRepository.existsByCodeInAndSidoCustom(codes, seoul);
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			List<String> codes = List.of("500", "680");
			boolean exists = sigunguRepository.existsByCodeInAndSidoCustom(codes, seoul);
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("findAllBySidoCustom 메서드는")
	class FindAllBySidoCustomTest {

		@Test
		@DisplayName("해당 시/도에 시/군/구가 존재하면, 모든 데이터를 반환한다.")
		void returnAllWhenSigunguPresentInSido() {
			createSigungus();

			List<Sigungu> sigunguList = sigunguRepository.findAllBySidoCustom(seoul);
			assertThat(sigunguList).hasSize(2);
		}

		@Test
		@DisplayName("해당 시/도에 시/군/구가 존재하지 않으면, 빈 리스트를 반환한다.")
		void returnEmptyListWhenSigunguNotPresentInSido() {
			List<Sigungu> sigunguList = sigunguRepository.findAllBySidoCustom(seoul);
			assertThat(sigunguList).isEmpty();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			List<Sigungu> sigunguList = sigunguRepository.findAllBySidoCustom(seoul);
			assertThat(sigunguList).hasSize(1);
		}

	}

	@Nested
	@DisplayName("findByIdAndSidoCustom 메서드는")
	class FindByIdAndSidoCustomTest {

		@Test
		@DisplayName("해당 시/도에 Id가 일치하는 시/군/구가 존재하면, 해당 데이터를 반환한다.")
		void returnSigunguWhenIdExistsInSido() {
			createSigungus();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdAndSidoCustom(gangnam.getId(), seoul);
			assertThat(sigungu.isPresent()).isTrue();
			assertThat(sigungu.get().getId()).isEqualTo(gangnam.getId());
		}

		@Test
		@DisplayName("해당 시/도에 Id가 일치하는 시/군/구가 존재하지 않으면, 빈 객체를 반환한다.")
		void returnEmptyWhenIdNotExistsInSido() {
			createSigungus();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdAndSidoCustom(UUID.randomUUID(), seoul);
			assertThat(sigungu.isPresent()).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdAndSidoCustom(UUID.randomUUID(), seoul);
			assertThat(sigungu.isPresent()).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByNameAndSidoAndIdNotCustom 메서드는")
	class ExistsByNameAndSidoAndIdNotCustomTest {

		@Test
		@DisplayName("해당 시/도에 이름이 일치하고 Id가 자신이 아닌 시/군/구가 존재하면, true를 반환한다.")
		void returnTrueWhenNameExistsAndIdIsNotSameInSido() {
			createSigungus();

			boolean exists = sigunguRepository.existsByNameAndSidoAndIdNotCustom("강남구", seoul, UUID.randomUUID());
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/도에 이름이 일치하고 Id가 자신인 시/군/구가 존재하면, false를 반환한다.")
		void returnFalseWhenNameExistsAndIdIsSameInSido() {
			createSigungus();

			boolean exists = sigunguRepository.existsByNameAndSidoAndIdNotCustom("강남구", seoul, gangnam.getId());
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			boolean exists = sigunguRepository.existsByNameAndSidoAndIdNotCustom("강남구", seoul, UUID.randomUUID());
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeAndSidoAndIdNotCustom 메서드는")
	class ExistsByCodeAndIdNotCustomTest {

		@Test
		@DisplayName("해당 시/도에 코드가 일치하고 Id가 자신이 아닌 시/군/구가 존재하면, true를 반환한다.")
		void returnTrueWhenCodeExistsAndIdIsNotSameInSido() {
			createSigungus();

			boolean exists = sigunguRepository.existsByCodeAndSidoAndIdNotCustom("680", seoul, UUID.randomUUID());
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/도에 코드가 일치하고 Id가 자신인 시/군/구가 존재하면, false를 반환한다.")
		void returnFalseWhenCodeExistsAndIdIsSameInSido() {
			createSigungus();

			boolean exists = sigunguRepository.existsByCodeAndSidoAndIdNotCustom("강남구", seoul, gangnam.getId());
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			boolean exists = sigunguRepository.existsByCodeAndSidoAndIdNotCustom("강남구", seoul, UUID.randomUUID());
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("findByIdCustom 메서드는")
	class FindByIdCustomTest {

		@Test
		@DisplayName("Id가 일치하는 시/군/구가 존재하면, 해당 데이터를 반환한다.")
		void returnSigunguWhenIdExists() {
			createSigungus();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdCustom(gangnam.getId());
			assertThat(sigungu.isPresent()).isTrue();
			assertThat(sigungu.get().getId()).isEqualTo(gangnam.getId());
		}

		@Test
		@DisplayName("Id가 일치하는 시/군/구가 존재하지 않으면, 빈 객체를 반환한다.")
		void returnSigunguWhenIdNotExists() {
			createSigungus();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdCustom(UUID.randomUUID());
			assertThat(sigungu.isPresent()).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createSigungus();

			Sigungu persistedGangnam = em.find(Sigungu.class, gangnam.getId());
			persistedGangnam.softDelete(user.getId());
			assertThat(persistedGangnam.isDeleted()).isTrue();

			Optional<Sigungu> sigungu = sigunguRepository.findByIdCustom(UUID.randomUUID());
			assertThat(sigungu.isPresent()).isFalse();
		}

	}

}
