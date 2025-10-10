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
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@DataJpaTest
public class SidoRepositoryTest {

	@Autowired
	private SidoRepository sidoRepository;

	@Autowired
	private TestEntityManager em;

	private User user;
	private Sido seoul;
	private Sido busan;

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
		busan = Sido.builder()
			.name("부산광역시")
			.code("26")
			.build();

		em.persist(user);
		em.persist(seoul);
		em.persist(busan);
		em.flush();
		em.clear();
	}

	@Nested
	@DisplayName("existsByNameInCustom 메서드는")
	class ExistsByNameInCustomTest {

		@Test
		@DisplayName("DB에 이름이 존재하면, true를 반환한다.")
		void returnTrueWhenNameDoesExist() {
			boolean isExists = sidoRepository.existsByNameInCustom(List.of("서울특별시", "경기도"));
			assertThat(isExists).isTrue();
		}

		@Test
		@DisplayName("DB에 이름이 존재하지 않으면, false를 반환한다.")
		void returnFalseWhenNameDoesNotExist() {
			boolean isExists = sidoRepository.existsByNameInCustom(List.of("경기도", "인천광역시"));
			assertThat(isExists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			boolean isExists = sidoRepository.existsByNameInCustom(List.of("부산광역시"));
			assertThat(isExists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeInCustom 메서드는")
	class ExistsByCodeInCustomTest {

		@Test
		@DisplayName("DB에 코드가 존재하면, true를 반환한다.")
		void returnTrueWhenCodeDoesExist() {
			boolean isExists = sidoRepository.existsByCodeInCustom(List.of("11", "41"));
			assertThat(isExists).isTrue();
		}

		@Test
		@DisplayName("DB에 코드가 존재하지 않으면, false를 반환한다.")
		void returnFalseWhenCodeDoesNotExist() {
			boolean isExists = sidoRepository.existsByNameInCustom(List.of("41", "28"));
			assertThat(isExists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			boolean isExists = sidoRepository.existsByCodeInCustom(List.of("26"));
			assertThat(isExists).isFalse();
		}

	}

	@Nested
	@DisplayName("findAllCustom 메서드는")
	class FindAllCustomTest {

		@Test
		@DisplayName("DB에 저장된 시·도가 있으면, 모든 데이터를 반환한다.")
		void returnAllWhenSidoPresent() {
			List<Sido> sidoList = sidoRepository.findAllCustom();
			assertThat(sidoList.size()).isEqualTo(2);
		}

		@Test
		@DisplayName("DB에 저장된 시·도가 없으면, 빈 리스트를 반환한다.")
		void returnEmptyListWhenSidoNotPresent() {
			List<Sido> allSidoList = sidoRepository.findAllCustom();
			allSidoList.forEach(em::remove);

			List<Sido> sidoList = sidoRepository.findAllCustom();
			assertThat(sidoList).isEmpty();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			List<Sido> sidoList = sidoRepository.findAllCustom();
			assertThat(sidoList).hasSize(1);
		}

	}

	@Nested
	@DisplayName("findByIdCustom 메서드는")
	class FindByIdCustomTest {

		@Test
		@DisplayName("DB에서 Id가 일치하는 시·도를 반환한다.")
		void returnSidoWhenSidoPresent() {
			Optional<Sido> sido = sidoRepository.findByIdCustom(busan.getId());
			assertThat(sido.isPresent()).isTrue();
			assertThat(busan.getId()).isEqualTo(sido.get().getId());
		}

		@Test
		@DisplayName("DB에 저장된 시·도가 없으면, 빈 객체를 반환한다.")
		void returnEmptySidoWhenSidoNotPresent() {
			Optional<Sido> sido = sidoRepository.findByIdCustom(UUID.randomUUID());
			assertThat(sido.isPresent()).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			Optional<Sido> sido = sidoRepository.findByIdCustom(busan.getId());
			assertThat(sido.isPresent()).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByNameAndIdNotCustom 메서드는")
	class ExistsByNameAndIdNotCustomTest {

		@Test
		@DisplayName("DB에 이름이 존재하고 Id가 자신이 아니면, true를 반환한다.")
		void returnTrueWhenNameDoesExistAndIdIsNotSame() {
			boolean isExits = sidoRepository.existsByNameAndIdNotCustom("부산광역시", UUID.randomUUID());
			assertThat(isExits).isTrue();
		}

		@Test
		@DisplayName("DB에 이름이 존재하고 Id가 자신이면, false를 반환한다.")
		void returnTrueWhenNameDoesExistAndIdIsSame() {
			boolean isExits = sidoRepository.existsByNameAndIdNotCustom("부산광역시", busan.getId());
			assertThat(isExits).isFalse();
		}

		@Test
		@DisplayName("DB에 이름이 존재하지 않으면, false를 반환한다.")
		void returnFalseWhenNameDoesNotExist() {
			boolean isExists = sidoRepository.existsByNameAndIdNotCustom("경기도", busan.getId());
			assertThat(isExists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			boolean isExists = sidoRepository.existsByNameAndIdNotCustom("부산광역시", busan.getId());
			assertThat(isExists).isFalse();
		}

	}

	@Nested
	@DisplayName("existsByCodeAndIdNotCustom 메서드는")
	class ExistsByCodeAndIdNotCustomTest {

		@Test
		@DisplayName("DB에 코드가 존재하고 Id가 자신이 아니면, true를 반환한다.")
		void returnTrueWhenNameDoesExistAndIdIsNotSame() {
			boolean isExits = sidoRepository.existsByCodeAndIdNotCustom("26", UUID.randomUUID());
			assertThat(isExits).isTrue();
		}

		@Test
		@DisplayName("DB에 코드가 존재하고 Id가 자신이면, false를 반환한다.")
		void returnFalseWhenNameDoesExistAndIdIsSame() {
			boolean isExists = sidoRepository.existsByCodeAndIdNotCustom("26", busan.getId());
			assertThat(isExists).isFalse();
		}

		@Test
		@DisplayName("DB에 코드가 존재하지 않으면, false를 반환한다.")
		void returnFalseWhenNameDoesNotExist() {
			boolean isExists = sidoRepository.existsByCodeAndIdNotCustom("41", busan.getId());
			assertThat(isExists).isFalse();
		}

		@Test
		@DisplayName("삭제된 데이터는 제외된다.")
		void excludeDeleted() {
			Sido persistedBusan = em.find(Sido.class, busan.getId());
			persistedBusan.softDelete(user.getId());
			assertThat(persistedBusan.getDeletedAt()).isNotNull();
			assertThat(persistedBusan.getDeletedBy()).isEqualTo(user.getId());

			boolean isExists = sidoRepository.existsByCodeAndIdNotCustom("부산광역시", busan.getId());
			assertThat(isExists).isFalse();
		}

	}

}
