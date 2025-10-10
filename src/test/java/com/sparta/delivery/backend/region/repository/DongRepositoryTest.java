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

import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
public class DongRepositoryTest {

	@Autowired
	private DongRepository dongRepository;

	@Autowired
	private TestEntityManager em;

	private User user;
	private Sido seoul;
	private Sigungu gangnam;
	private Dong yeoksam;
	private Dong samsung;

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
		gangnam = Sigungu.builder()
			.sido(seoul)
			.name("강남구")
			.code("680")
			.build();

		em.persist(user);
		em.persist(seoul);
		em.persist(gangnam);
		em.flush();
		em.clear();
	}

	// 필요한 테스트에서 호출하여 생성
	void createDongs() {
		yeoksam = Dong.builder()
			.sigungu(gangnam)
			.name("역삼동")
			.code("010")
			.build();
		samsung = Dong.builder()
			.sigungu(gangnam)
			.name("삼성동")
			.code("030")
			.build();

		em.persist(yeoksam);
		em.persist(samsung);
		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("existsByNameInAndSigunguCustom 메서드는")
	class ExistsByNameInAndSigunguCustomTest {

		@Test
		@DisplayName("해당 시/군/구에 이름이 같은 동 하나라도 존재하면, true를 반환한다.")
		void returnTrueWhenNameExistsInSigungu() {
			createDongs();

			List<String> names = List.of("대치동", "삼성동");
			boolean exists = dongRepository.existsByNameInAndSigunguCustom(names, gangnam);
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/군/구에 이름이 같은 동이 존재하지 않으면, false를 반환한다.")
		void returnTrueWhenNameNotExistsInSigungu() {
			createDongs();

			List<String> names = List.of("대치동", "청담동");
			boolean exists = dongRepository.existsByNameInAndSigunguCustom(names, gangnam);
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			List<String> names = List.of("대치동", "삼성동");
			boolean exists = dongRepository.existsByNameInAndSigunguCustom(names, gangnam);
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeInCustom 메서드는")
	class ExistsByCodeInCustomTest {

		@Test
		@DisplayName("해당 시/군/구에 코드가 같은 동 하나라도 존재하면, true를 반환한다.")
		void returnTrueWhenCodeExistsInSigungu() {
			createDongs();

			List<String> codes = List.of("050", "030");
			boolean exists = dongRepository.existsByCodeInCustom(codes);
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/군/구에 코드가 같은 동이 존재하지 않으면, false를 반환한다.")
		void returnTrueWhenCodeNotExistsInSigungu() {
			createDongs();

			List<String> codes = List.of("050", "080");
			boolean exists = dongRepository.existsByCodeInCustom(codes);
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			List<String> codes = List.of("050", "030");
			boolean exists = dongRepository.existsByCodeInCustom(codes);
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("findAllBySigunguCustom 메서드는")
	class FindAllBySigunguCustomTest {

		@Test
		@DisplayName("해당 시/군/구에 동이 존재하면, 모든 데이터를 반환한다.")
		void returnAllWhenDongPresentInSigungu() {
			createDongs();

			List<Dong> dongList = dongRepository.findAllBySigunguCustom(gangnam);
			assertThat(dongList).hasSize(2);
		}

		@Test
		@DisplayName("해당 시/군/구에 동이 존재하지 않으면, 빈 리스트를 반환한다.")
		void returnEmptyListWhenDongNotPresentInSigungu() {
			List<Dong> dongList = dongRepository.findAllBySigunguCustom(gangnam);
			assertThat(dongList).isEmpty();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			List<Dong> dongList = dongRepository.findAllBySigunguCustom(gangnam);
			assertThat(dongList).hasSize(1);
		}

	}

	@Nested
	@DisplayName("existsByNameAndSigunguAndIdNotCustom 메서드는")
	class ExistsByNameAndSigunguAndIdNotCustomTest {

		@Test
		@DisplayName("해당 시/군/구에 이름이 일치하고 Id가 자신이 아닌 동이 존재하면, true를 반환한다.")
		void returnTrueWhenNameExistsAndIdIsNotSameInSigungu() {
			createDongs();

			boolean exists = dongRepository.existsByNameAndSigunguAndIdNotCustom("삼성동", gangnam, UUID.randomUUID());
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("해당 시/도에 이름이 일치하고 Id가 자신인 시/군/구가 존재하면, false를 반환한다.")
		void returnFalseWhenNameExistsAndIdIsSameInSigungu() {
			createDongs();

			boolean exists = dongRepository.existsByNameAndSigunguAndIdNotCustom("삼성동", gangnam, samsung.getId());
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			boolean exists = dongRepository.existsByNameAndSigunguAndIdNotCustom("삼성동", gangnam, UUID.randomUUID());
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeAndIdNotCustom 메서드는")
	class ExistsByCodeAndIdNotCustomTest {

		@Test
		@DisplayName("전체 시/군/구에 코드가 일치하고 Id가 자신이 아닌 동이 존재하면, true를 반환한다.")
		void returnTrueWhenCodeExistsAndIdIsNotSame() {
			createDongs();

			boolean exists = dongRepository.existsByCodeAndIdNotCustom("030", UUID.randomUUID());
			assertThat(exists).isTrue();
		}

		@Test
		@DisplayName("전체 시/군/구에 코드가 일치하고 Id가 자신인 동이 존재하면, false를 반환한다.")
		void returnFalseWhenCodeExistsAndIdIsSame() {
			createDongs();

			boolean exists = dongRepository.existsByCodeAndIdNotCustom("030", samsung.getId());
			assertThat(exists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			boolean exists = dongRepository.existsByCodeAndIdNotCustom("030", UUID.randomUUID());
			assertThat(exists).isFalse();
		}

	}

	@Nested
	@DisplayName("findByIdAndSigunguCustom 메서드는")
	class FindByIdAndSigunguCustomTest {

		@Test
		@DisplayName("해당 시/군/구에 Id가 일치하는 동이 존재하면, 해당 데이터를 반환한다.")
		void returnDongWhenIdExistsInSigungu() {
			createDongs();

			Optional<Dong> dong = dongRepository.findByIdAndSigunguCustom(samsung.getId(), gangnam);
			assertThat(dong.isPresent()).isTrue();
			assertThat(dong.get().getId()).isEqualTo(samsung.getId());
		}

		@Test
		@DisplayName("해당 시/군/구에 Id가 일치하는 동이 존재하지 않으면, 빈 객체를 반환한다.")
		void returnEmptyWhenIdNotExistsInSigungu() {
			createDongs();

			Optional<Dong> dong = dongRepository.findByIdAndSigunguCustom(UUID.randomUUID(), gangnam);
			assertThat(dong.isPresent()).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			createDongs();

			Dong persistedSamsung = em.find(Dong.class, samsung.getId());
			persistedSamsung.softDelete(user.getId());
			assertThat(persistedSamsung.isDeleted()).isTrue();

			Optional<Dong> dong = dongRepository.findByIdAndSigunguCustom(samsung.getId(), gangnam);
			assertThat(dong.isPresent()).isFalse();
		}

	}

	@Nested
	@DisplayName("findByCode 메서드는")
	class FindByCodeTest {

		@Test
		@DisplayName("코드가 일치하는 동이 존재하면, 해당 데이터를 반환한다.")
		void returnDongWhenCodeExists() {
			createDongs();

			Optional<Dong> dong = dongRepository.findByCode("030");
			assertThat(dong.isPresent()).isTrue();
			assertThat(dong.get().getId()).isEqualTo(samsung.getId());
		}

		@Test
		@DisplayName("코드가 일치하는 동이 존재하지 않으면, 빈 객체를 반환한다.")
		void returnEmptyWhenCodeNotExists() {
			createDongs();

			Optional<Dong> dong = dongRepository.findByCode("050");
			assertThat(dong.isPresent()).isFalse();
		}

	}

}
