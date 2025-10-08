package com.sparta.delivery.backend.cart.repository;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
import com.sparta.delivery.backend.user.repository.UserRepository;

@DataJpaTest
public class CartRepositoryTest {
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private StoreRepository storeRepository;

	@Autowired
	private StoreMenuRepository storeMenuRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private User user;
	private Customer customer;
	private User ownerUser;
	private Owner owner;
	private Store store;
	private StoreMenu menu;

	@BeforeEach
	void setUp() {
		this.user = userRepository.save(User.builder()
			.username("testuser")
			.password("pass")
			.role(UserRoleEnum.CUSTOMER)
			.build());

		this.customer = customerRepository.save(Customer.builder()
			.user(user)
			.nickname("닉네임")
			.email("test@example.com")
			.phoneNumber("010-0000-0000")
			.build());

		this.ownerUser = userRepository.save(User.builder()
			.username("ownerUser")
			.password("password456")
			.role(UserRoleEnum.OWNER)
			.build());

		this.owner = ownerRepository.save(Owner.builder()
			.nickname("사장님")
			.businessNumber("1234567890")
			.user(ownerUser)
			.email("owner@test.net")
			.phoneNumber("01098745632")
			.build());

		this.store = storeRepository.save(Store.builder()
			.name("도미노피자")
			.reviewRate(4.8)
			.deliveryFee(3000)
			.minOrderPrice(15000)
			.status(StoreStatusEnum.CLOSED)
			.owner(owner)
			.build());

		ReqCreateStoreMenuDto requestDto = new ReqCreateStoreMenuDto();
		requestDto.setName("조회메뉴");
		requestDto.setPrice(3000);
		requestDto.setDescription("조회용 설명");
		requestDto.setPrepTime("7분");
		requestDto.setStockStatus(null);
		requestDto.setSortOrder(1);
		requestDto.setIsHidden(false);

		this.menu = storeMenuRepository.save(StoreMenu.builder()
			.reqCreateStoreMenuDto(requestDto)
			.store(store)
			.build());
	}

	@Test
	@DisplayName("existsByCustomerIdAndDeletedAtIsNull:기존카트존재여부확인")
	void existsByCustomerIdAndDeletedAtIsNullTest() {
		// given
		Cart cart = Cart.builder()
			.customer(customer)
			.menu(menu)
			.build();

		cartRepository.save(cart);

		// when
		boolean exists = cartRepository.existsByCustomerIdAndDeletedAtIsNull(customer.getId());

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("카트가 이미 존재할 때 같은 가게의 카트인지 확인")
	void existsByDeletedAtIsNullAndMenuStoreIdTest() {
		// given
		Cart cart = cartRepository.save(Cart.builder()
			.customer(customer)
			.menu(menu)
			.build());

		Store storeB = storeRepository.save(Store.builder()
			.name("다른피자")
			.reviewRate(4.8)
			.deliveryFee(4000)
			.minOrderPrice(18000)
			.status(StoreStatusEnum.CLOSED)
			.owner(owner)
			.build());

		ReqCreateStoreMenuDto requestDto = new ReqCreateStoreMenuDto();
		requestDto.setName("고구마피자");
		requestDto.setPrice(3000);
		requestDto.setDescription("조회용 설명");
		requestDto.setPrepTime("7분");
		requestDto.setStockStatus(null);
		requestDto.setSortOrder(1);
		requestDto.setIsHidden(false);

		StoreMenu menuB = storeMenuRepository.save(StoreMenu.builder()
			.reqCreateStoreMenuDto(requestDto)
			.store(storeB)
			.build());

		// when
		boolean cartExists = cartRepository.existsByCustomerIdAndDeletedAtIsNull(customer.getId());
		boolean storeCartExists = cartRepository.existsByDeletedAtIsNullAndMenuStoreId(storeB.getId());

		// then
		assertThat(cartExists).isTrue();
		assertThat(storeCartExists).isFalse();
	}


}
