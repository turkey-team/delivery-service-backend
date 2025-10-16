package com.sparta.delivery.backend.store.menu.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateSortOrderDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ReqUpdateVisibilityDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetListStoreMenuDto;
import com.sparta.delivery.backend.store.menu.dto.ResGetStoreMenuDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
@DisplayName("StoreMenuService 테스트")
class StoreMenuServiceTest {

	@Mock
	private StoreMenuRepository storeMenuRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private ImageRepository imageRepository;

	@InjectMocks
	private StoreMenuService storeMenuService;

	private User user;
	private Owner owner;
	private Customer customer;
	private Store store;
	private StoreMenu menu1;
	private StoreMenu menu2;
	private StoreMenu targetMenu;
	private Image image;
	private ReqCreateStoreMenuDto reqCreateStoreMenuDto;

	@BeforeEach
	void setUp() {
		// 테스트용 User(Owner) 생성
		user = User.builder()
			.username("testOwner1")
			.password("testOwner1@@")
			.role(UserRoleEnum.OWNER)
			.build();
		ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());

		owner = Owner.builder()
			.nickname("Owner1234")
			.email("testOwner1@naver.com")
			.phoneNumber("01022223333")
			.user(user)
			.build();
		ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());

		// 테스트 가게
		store = Store.builder()
			.owner(owner) // 더미 Owner
			.name("테스트용 햄버거 가게")
			//.addressDetails("고양시 덕양구 화정동 백양로 65")
			.reviewRate(0.0)
			.minOrderPrice(13000)
			.deliveryFee(1500)
			//.regionDong(null) // 추후 필요하면 더미 Dong 생성
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("01012345678")
			.build();
		ReflectionTestUtils.setField(store, "id", UUID.randomUUID());

		image = Image.builder()
			.imageUrl("https://example.com/hamburger.jpg")
			.build();

		reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
		reqCreateStoreMenuDto.setName("치즈버거");
		reqCreateStoreMenuDto.setImageUrl(image.getImageUrl());
		reqCreateStoreMenuDto.setPrice(4000);
		reqCreateStoreMenuDto.setDescription("치즈, 소고기, 피클, 마요네즈가 들어있습니다.");
		reqCreateStoreMenuDto.setPrepTime("15분");
		reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
		reqCreateStoreMenuDto.setIsHidden(false);

		// 테스트 가게 메뉴
		menu1 = StoreMenu.builder()
			.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
			.store(store)
			.image(image)
			.build();
		ReflectionTestUtils.setField(menu1, "id", UUID.randomUUID()); // Test UUID 주입
		menu1.setSortOrder(1);
	}

	// Create
	@Nested
	@DisplayName("가게 메뉴 생성 테스트")
	class CreateStoreMenuTest {

		@Test
		@DisplayName("성공")
		void create_success() {
			/* given */
			UUID storeId = store.getId();
			reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
			reqCreateStoreMenuDto.setName("치즈버거");
			reqCreateStoreMenuDto.setPrice(4000);
			reqCreateStoreMenuDto.setDescription("Cheese Burger, 클래식한 치즈가 들어간 햄버거이다.");
			reqCreateStoreMenuDto.setPrepTime("5분");
			reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqCreateStoreMenuDto.setIsHidden(false);
			reqCreateStoreMenuDto.setImageUrl("http://image.url/cheese_burger.png");

			// storeRepository.findById
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			// imageRepository.save
			when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
			// storeMenuRepository.findMaxSortOrderByStore
			when(storeMenuRepository.findMaxSortOrderByStore(storeId))
				.thenAnswer(invocation -> {
					// 현재 Mock에 저장된 메뉴 개수만큼 sortOrder를 계산
					// ex) 메뉴가 3개면 다음 순서 4
					List<StoreMenu> menus = List.of(menu1); // 현재 테스트에 존재하는 메뉴들
					return menus.stream()
						.map(StoreMenu::getSortOrder)
						.max(Integer::compareTo)
						.orElse(0);
				});
			// storeMenuRepository.save
			when(storeMenuRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

			/* when */
			storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto);

			/* then */
			verify(storeMenuRepository).save(any(StoreMenu.class));

			ArgumentCaptor<StoreMenu> captor = ArgumentCaptor.forClass(StoreMenu.class);
			verify(storeMenuRepository).save(captor.capture());
			StoreMenu savedMenu = captor.getValue();

			assertEquals("치즈버거", savedMenu.getName());
			assertEquals(4000, savedMenu.getPrice());
			assertEquals("Cheese Burger, 클래식한 치즈가 들어간 햄버거이다.", savedMenu.getDescription());
			assertEquals("5분", savedMenu.getPrepTime());
			assertEquals(StockStatus.ON_SALE, savedMenu.getStockStatus());
			assertEquals(store, savedMenu.getStore());
			assertEquals(2, savedMenu.getSortOrder()); // maxSortOrder == null 이면 1부터 시작
			assertNotNull(savedMenu.getImage());
		}

		@Test
		@DisplayName("성공 - Image 미설정 시 기본 Image")
		void create_success_whenImageUrlIsNull() {
			/* given */
			UUID storeId = store.getId();
			reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
			reqCreateStoreMenuDto.setName("치즈버거");
			reqCreateStoreMenuDto.setPrice(4000);
			reqCreateStoreMenuDto.setDescription("Cheese Burger, 클래식한 치즈가 들어간 햄버거이다.");
			reqCreateStoreMenuDto.setPrepTime("5분");
			reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqCreateStoreMenuDto.setIsHidden(false);
			reqCreateStoreMenuDto.setImageUrl("http://image.url/cheese_burger.png");

			// storeRepository.findById
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			// imageRepository.save
			when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
			// storeMenuRepository.findMaxSortOrderByStore
			when(storeMenuRepository.findMaxSortOrderByStore(storeId))
				.thenAnswer(invocation -> {
					// 현재 Mock에 저장된 메뉴 개수만큼 sortOrder를 계산
					// ex) 메뉴가 3개면 다음 순서 4
					List<StoreMenu> menus = List.of(menu1); // 현재 테스트에 존재하는 메뉴들
					return menus.stream()
						.map(StoreMenu::getSortOrder)
						.max(Integer::compareTo)
						.orElse(0);
				});
			// storeMenuRepository.save
			when(storeMenuRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

			/* when */
			storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto);

			/* then */
			verify(storeMenuRepository).save(any(StoreMenu.class));

			ArgumentCaptor<StoreMenu> captor = ArgumentCaptor.forClass(StoreMenu.class);
			verify(storeMenuRepository).save(captor.capture());
			StoreMenu savedMenu = captor.getValue();

			assertEquals("치즈버거", savedMenu.getName());
			assertEquals(4000, savedMenu.getPrice());
			assertEquals("Cheese Burger, 클래식한 치즈가 들어간 햄버거이다.", savedMenu.getDescription());
			assertEquals("5분", savedMenu.getPrepTime());
			assertEquals(StockStatus.ON_SALE, savedMenu.getStockStatus());
			assertEquals(store, savedMenu.getStore());
			assertEquals(2, savedMenu.getSortOrder()); // maxSortOrder == null 이면 1부터 시작
			assertNotNull(savedMenu.getImage());
		}

		@Test
		@DisplayName("실패 - Store 가 존재하지 않는 경우")
		void failure_storeNull() {
			/* given */
			UUID storeId = store.getId();
			reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
			reqCreateStoreMenuDto.setName("불고기버거");
			reqCreateStoreMenuDto.setPrice(4000);
			reqCreateStoreMenuDto.setDescription("불고기버거, 불고기, 바베큐 소스가 들어간 햄버거이다.");
			reqCreateStoreMenuDto.setPrepTime("5분");
			reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqCreateStoreMenuDto.setIsHidden(false);
			reqCreateStoreMenuDto.setImageUrl("http://image.url/bulgogi_burger.png");

			when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto)
			);

			assertEquals("Store not found", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 메뉴 이름이 중복된 경우")
		void failure_duplicateName() {
			UUID storeId = store.getId();
			reqCreateStoreMenuDto.setName("치즈버거"); // 이미 존재하는 이름

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndName(storeId, "치즈버거"))
				.thenReturn(Optional.of(menu1)); // 중복 메뉴 존재
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto)
			);

			assertEquals("Menu name already exists", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 권한이 없는 사용자(Customer)가 생성")
		void failure_noPermission() {
			/* given */
			UUID storeId = store.getId();

			// 테스트용 User(Customer) 생성
			user = User.builder()
				.username("customerUser")
				.password("pass")
				.role(UserRoleEnum.CUSTOMER)
				.build();
			ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());

			// storeRepository.findById()는 정상적으로 store 반환
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			/* when & then */
			SecurityException exception = assertThrows(
				SecurityException.class,
				() -> storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto)
			);

			assertEquals("You do not have permission", exception.getMessage());
		}

		/* 리뷰 이벤트 같은 경우 +0 을 의도적으로 설정하는 경우가 있어서 예외처리하는게 맞을지 의문
        @Test
        @DisplayName("실패 - 가격을 입력하지 않은 경우")
        void failure_priceNull() {
            UUID storeId = store.getId();
            reqCreateStoreMenuDto.setPrice(0); // 가격을 입력하지 않은 경우

            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> storeMenuService.createStoreMenu(user, storeId, reqCreateStoreMenuDto)
            );

            assertEquals("Price must exists", exception.getMessage());
        }
        */
	}

	// Get
	@Nested
	@DisplayName("가게 메뉴 단일 조회 테스트")
	class GetStoreMenuTest {

		@Test
		@DisplayName("성공")
		void get_success() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			// Mock 설정
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(eq(storeId), eq(menuId), isNull()))
				.thenReturn(Optional.of(menu1));

			/* when */
			ResGetStoreMenuDto resGetStoreMenuDto = storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId);

			/* then */
			assertNotNull(resGetStoreMenuDto);
			assertEquals(menu1.getId(), resGetStoreMenuDto.getId());
			assertEquals(menu1.getName(), resGetStoreMenuDto.getName());
			assertEquals(menu1.getPrice(), resGetStoreMenuDto.getPrice());
			verify(storeMenuRepository, times(1))
				.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
		}

		@Test
		@DisplayName("실패 - 가게가 존재하지 않은 경우")
		void failure_storeNull() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			/* when */
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			/* then */
			assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId)
			);
		}

		@Test
		@DisplayName("실패 - 메뉴가 존재하지 않은 경우")
		void failure_menuNull() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			/* when */
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(eq(storeId), eq(menuId), isNull()))
				.thenReturn(Optional.empty());

			/* then */
			assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.getStoreMenuByStoreMenuId(storeId, menuId)
			);
		}
	}

	@Nested
	@DisplayName("가게 메뉴 목록 조회 테스트")
	class GetListStoreMenuTest {

		@Test
		@DisplayName("성공 - Customer는 숨김된 메뉴를 조회하지 않음")
		void getList_customer_excludesHiddenMenus() {
			/* given */
			UUID storeId = store.getId();
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

			User customerUser = User.builder()
				.username("customerUser")
				.password("pass")
				.role(UserRoleEnum.CUSTOMER)
				.build();
			ReflectionTestUtils.setField(customerUser, "publicId", UUID.randomUUID());

			// 메뉴 2만 숨김 처리
			menu1.setHiddenAt(null);
			menu2 = StoreMenu.builder()
				.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				.store(store)
				.image(image)
				.build();
			ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
			menu2.setHiddenAt(true);

			List<StoreMenu> menuList = List.of(menu1);
			Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(menuList, pageable,
				menuList.size());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNullAndHiddenAtIsNull(storeId, pageable))
				.thenReturn(pageResult);

			/* when */
			Page<ResGetListStoreMenuDto> resGetListStoreMenuDto = storeMenuService.getStoreMenusByStoreId(customerUser, storeId, 0,
				10);

			/* then */
			assertNotNull(resGetListStoreMenuDto);
			assertEquals(1, resGetListStoreMenuDto.getContent().size());
			assertEquals(menu1.getName(), resGetListStoreMenuDto.getContent().get(0).getName());
			verify(storeMenuRepository, times(1))
				.findAllByStoreIdAndDeletedAtIsNullAndHiddenAtIsNull(storeId, pageable);
		}

		@Test
		@DisplayName("성공 - 다른 가게의 Owner는 숨김된 메뉴를 조회하지 않음")
		void getList_differentOwner_cannotAccessOtherStore() {
			/* given */
			UUID storeId = store.getId();
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

			// 새로운 다른 Owner 유저 생성
			User differentOwner = User.builder()
				.username("otherOwner")
				.password("testPass")
				.role(UserRoleEnum.OWNER)
				.build();
			ReflectionTestUtils.setField(differentOwner, "publicId", UUID.randomUUID());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			// 메뉴 2만 숨김 처리
			menu1.setHiddenAt(null);
			menu2 = StoreMenu.builder()
				.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				.store(store)
				.image(image)
				.build();
			ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
			menu2.setHiddenAt(true);

			List<StoreMenu> menuList = List.of(menu1);
			Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(menuList, pageable,
				menuList.size());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNullAndHiddenAtIsNull(storeId, pageable))
				.thenReturn(pageResult);

			/* when */
			Page<ResGetListStoreMenuDto> resGetListStoreMenuDto = storeMenuService.getStoreMenusByStoreId(differentOwner, storeId, 0,
				10);

			/* then */
			assertNotNull(resGetListStoreMenuDto);
			assertEquals(1, resGetListStoreMenuDto.getContent().size());
			assertEquals(menu1.getName(), resGetListStoreMenuDto.getContent().get(0).getName());
			verify(storeMenuRepository, times(1))
				.findAllByStoreIdAndDeletedAtIsNullAndHiddenAtIsNull(storeId, pageable);
		}

		@Test
		@DisplayName("성공 - 가게의 Owner 본인은 숨긴 메뉴도 조회 가능")
		void getList_owner_includesHiddenMenus() {
			/* given */
			UUID storeId = store.getId();
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

			// 메뉴 2만 숨김 처리
			menu1.setHiddenAt(null);
			menu2 = StoreMenu.builder()
				.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				.store(store)
				.image(image)
				.build();
			ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
			menu2.setHiddenAt(true);

			List<StoreMenu> menuList = List.of(menu1, menu2);
			Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(menuList, pageable,
				menuList.size());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable))
				.thenReturn(pageResult);

			/* when */
			Page<ResGetListStoreMenuDto> resGetListStoreMenuDto = storeMenuService.getStoreMenusByStoreId(user, storeId, 0,
				10);

			/* then */
			assertNotNull(resGetListStoreMenuDto);
			assertEquals(2, resGetListStoreMenuDto.getContent().size());
			assertEquals(menu1.getName(), resGetListStoreMenuDto.getContent().get(0).getName());
			verify(storeMenuRepository, times(1))
				.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);
		}

		// 현재 Manager, Master 도 삭제된 메뉴는 안보이도록 설계하기로 결정
		// @Test
		// @DisplayName("성공 - Manager, Master는 softDelete된 메뉴도 조회 가능")
		// void getList_success_managerAndMasterCanSeeDeleted() {
		// 	/* given */
		// 	UUID storeId = store.getId();
		// 	int page = 0;
		// 	int size = 10;
		// 	Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());
		//
		// 	// MANAGER 권한 부여
		// 	user = User.builder()
		// 		.username("managerUser")
		// 		.password("pass")
		// 		.role(UserRoleEnum.MANAGER)
		// 		.build();
		// 	ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());
		//
		// 	// softDelete된 메뉴 포함
		// 	menu1.softDelete(user.getPublicId(), -1);
		// 	menu2 = StoreMenu.builder()
		// 		.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
		// 		.store(store)
		// 		.image(image)
		// 		.build();
		// 	ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
		//
		// 	List<StoreMenu> allMenus = List.of(menu1, menu2);
		// 	Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(allMenus, pageable, allMenus.size());
		//
		// 	when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
		// 	when(storeMenuRepository.findAllByStoreId(storeId, pageable))
		// 		.thenReturn(pageResult);
		//
		// 	/* when */
		// 	Page<ResGetListStoreMenuDto> result = storeMenuService.getStoreMenusByStoreId(user, storeId, page, size);
		//
		// 	/* then */
		// 	assertNotNull(result);
		// 	assertEquals(2, result.getContent().size());
		// 	verify(storeMenuRepository, times(1))
		// 		.findAllByStoreId(storeId, pageable);
		// }

		@Test
		@DisplayName("성공 - Image 미설정 시 기본 Image")
		void getList_success_whenImageUrlIsNull() {
			/* given */
			UUID storeId = store.getId();
			int page = 0;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

			reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
			reqCreateStoreMenuDto.setName("새우버거");
			reqCreateStoreMenuDto.setImageUrl(image.getImageUrl());
			reqCreateStoreMenuDto.setPrice(3000);
			reqCreateStoreMenuDto.setDescription("새우, 마요네즈가 들어있습니다.");
			reqCreateStoreMenuDto.setPrepTime("10분");
			reqCreateStoreMenuDto.setStockStatus(StockStatus.LOW_STOCK);
			reqCreateStoreMenuDto.setIsHidden(false);

			// 테스트 가게 메뉴
			menu2 = StoreMenu.builder()
				.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				.store(store)
				.image(null)
				.build();
			ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID()); // Test UUID 주입

			List<StoreMenu> menuList = List.of(menu1, menu2);
			Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(menuList, pageable,
				menuList.size());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable))
				.thenReturn(pageResult);

			/* when */
			Page<ResGetListStoreMenuDto> resGetListStoreMenuDto = storeMenuService.getStoreMenusByStoreId(user, storeId, 0,
				10);

			/* then */
			assertNotNull(resGetListStoreMenuDto);
			assertEquals(2, resGetListStoreMenuDto.getContent().size());
			assertEquals(menu1.getName(), resGetListStoreMenuDto.getContent().get(0).getName());
			verify(storeMenuRepository, times(1))
				.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);
		}

		@Test
		@DisplayName("실패 - 가게가 존재하지 않은 경우")
		void failure_storeNull() {
			/* given */
			UUID storeId = store.getId();
			int page = 1;
			int size = 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder").ascending());

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			// menu 는 비어있어도 괜찮다.
			when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable))
				.thenReturn(Page.empty(pageable));

			/* when */
			Page<ResGetListStoreMenuDto> result =
				storeMenuService.getStoreMenusByStoreId(user, storeId, page, size);

			/* then */
			assertNotNull(result);
			assertTrue(result.isEmpty());
			verify(storeRepository, times(1)).findById(storeId);
			verify(storeMenuRepository, times(1))
				.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable);
		}
	}

	// Put
	@Nested
	@DisplayName("가게 메뉴 수정 테스트")
	class UpdateStoreMenuTest {

		@Test
		@DisplayName("성공")
		void update_success() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			// 수정 DTO
			ReqUpdateStoreMenuDto reqUpdateStoreMenuDto = new ReqUpdateStoreMenuDto();
			reqUpdateStoreMenuDto.setName("더블치즈버거");
			reqUpdateStoreMenuDto.setPrice(4500);
			reqUpdateStoreMenuDto.setDescription("두 배의 치즈와 소고기가 들어간 햄버거");
			reqUpdateStoreMenuDto.setPrepTime("20분");
			reqUpdateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqUpdateStoreMenuDto.setImageUrl("http://image.url/double_cheese.png");

			Image newImage = Image.builder().imageUrl(reqUpdateStoreMenuDto.getImageUrl()).build();
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(eq(storeId), eq(menuId), isNull()))
				.thenReturn(Optional.of(menu1));
			when(imageRepository.save(any())).thenReturn(newImage);

			/* when */
			storeMenuService.updateStoreMenu(user, storeId, menuId, reqUpdateStoreMenuDto);

			/* then */
			verify(storeMenuRepository).findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
			verify(imageRepository).save(any(Image.class));

			assertEquals("더블치즈버거", menu1.getName());
			assertEquals(4500, menu1.getPrice());
			assertEquals("두 배의 치즈와 소고기가 들어간 햄버거", menu1.getDescription());
			assertEquals("20분", menu1.getPrepTime());
			assertEquals(StockStatus.ON_SALE, menu1.getStockStatus());
			assertEquals(reqUpdateStoreMenuDto.getImageUrl(), menu1.getImage().getImageUrl());
		}

		@Test
		@DisplayName("성공 - Image 미설정 시 기본 Image")
		void update_success_whenImageUrlIsNull() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			// 수정 DTO
			ReqUpdateStoreMenuDto reqUpdateStoreMenuDto = new ReqUpdateStoreMenuDto();
			reqUpdateStoreMenuDto.setName("더블치즈버거");
			reqUpdateStoreMenuDto.setPrice(4500);
			reqUpdateStoreMenuDto.setDescription("두 배의 치즈와 소고기가 들어간 햄버거");
			reqUpdateStoreMenuDto.setPrepTime("20분");
			reqUpdateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqUpdateStoreMenuDto.setImageUrl(null);

			Image newImage = Image.builder().imageUrl(reqUpdateStoreMenuDto.getImageUrl()).build();
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(eq(storeId), eq(menuId), isNull()))
				.thenReturn(Optional.of(menu1));
			when(imageRepository.save(any())).thenReturn(newImage);

			/* when */
			storeMenuService.updateStoreMenu(user, storeId, menuId, reqUpdateStoreMenuDto);

			/* then */
			verify(storeMenuRepository).findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
			verify(imageRepository).save(any(Image.class));

			assertEquals("더블치즈버거", menu1.getName());
			assertEquals(4500, menu1.getPrice());
			assertEquals("두 배의 치즈와 소고기가 들어간 햄버거", menu1.getDescription());
			assertEquals("20분", menu1.getPrepTime());
			assertEquals(StockStatus.ON_SALE, menu1.getStockStatus());
			assertEquals(reqUpdateStoreMenuDto.getImageUrl(), menu1.getImage().getImageUrl());
		}

		@Test
		@DisplayName("실패 - 가게가 존재하지 않는 경우")
		void failure_storeNull() {
			/* given */
			UUID storeId = UUID.randomUUID();
			UUID menuId = menu1.getId();
			ReqUpdateStoreMenuDto updateDto = new ReqUpdateStoreMenuDto();

			when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateStoreMenu(user, storeId, menuId, updateDto)
			);

			assertEquals("Store not found", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 메뉴가 존재하지 않는 경우")
		void failure_menuNull() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = UUID.randomUUID();
			ReqUpdateStoreMenuDto updateDto = new ReqUpdateStoreMenuDto();

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(eq(storeId), eq(menuId), isNull()))
				.thenReturn(Optional.empty());

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateStoreMenu(user, storeId, menuId, updateDto)
			);

			assertEquals("StoreMenu not found", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 메뉴명이 중복되는 경우")
		void failure_whenNameAlreadyExists() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			// 이미 존재하는 메뉴명
			ReqUpdateStoreMenuDto reqUpdateStoreMenuDto = new ReqUpdateStoreMenuDto();
			reqUpdateStoreMenuDto.setName("치즈버거");
			reqUpdateStoreMenuDto.setPrice(4000);
			reqUpdateStoreMenuDto.setDescription("같은 이름으로 업데이트 시도");
			reqUpdateStoreMenuDto.setPrepTime("10분");
			reqUpdateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqUpdateStoreMenuDto.setImageUrl("http://image.url/duplicate.png");

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			// 이미 존재하는 이름이므로 findByStoreIdAndName()이 Optional.of()를 반환하도록 설정
			when(storeMenuRepository.findByStoreIdAndName(storeId, "치즈버거"))
				.thenReturn(Optional.of(menu1));

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateStoreMenu(user, storeId, menuId, reqUpdateStoreMenuDto)
			);

			/* then */
			assertEquals("Menu name already exists", exception.getMessage());
			verify(storeMenuRepository, times(1)).findByStoreIdAndName(storeId, "치즈버거");
		}

		@Test
		@DisplayName("실패 - 권한이 없는 사용자(Customer)가 수정")
		void failure_customerNoPermission() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			// Customer 역할의 사용자
			user = User.builder()
				.username("customerUser")
				.password("pass")
				.role(UserRoleEnum.CUSTOMER)
				.build();
			ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());

			ReqUpdateStoreMenuDto reqUpdateStoreMenuDto = new ReqUpdateStoreMenuDto();
			reqUpdateStoreMenuDto.setName("수정불가버거");
			reqUpdateStoreMenuDto.setPrice(5000);
			reqUpdateStoreMenuDto.setDescription("Customer는 수정 불가");
			reqUpdateStoreMenuDto.setPrepTime("10분");
			reqUpdateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqUpdateStoreMenuDto.setImageUrl("http://image.url/customer_fail.png");

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			/* when & then */
			SecurityException exception = assertThrows(
				SecurityException.class,
				() -> storeMenuService.updateStoreMenu(user, storeId, menuId, reqUpdateStoreMenuDto)
			);

			assertEquals("You do not have permission", exception.getMessage());
			verify(storeRepository, times(1)).findById(storeId);
		}

		@Test
		@DisplayName("실패 - 다른 Owner가 수정 시도")
		void failure_differentOwnerNoPermission() {
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();

			user = User.builder()
				.username("anotherOwner")
				.password("pass")
				.role(UserRoleEnum.OWNER)
				.build();
			ReflectionTestUtils.setField(user, "publicId", UUID.randomUUID());

			ReqUpdateStoreMenuDto reqUpdateStoreMenuDto = new ReqUpdateStoreMenuDto();
			reqUpdateStoreMenuDto.setName("수정불가버거");
			reqUpdateStoreMenuDto.setPrice(5000);
			reqUpdateStoreMenuDto.setDescription("다른 Owner는 수정 불가");
			reqUpdateStoreMenuDto.setPrepTime("10분");
			reqUpdateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
			reqUpdateStoreMenuDto.setImageUrl("http://image.url/owner_fail.png");

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			SecurityException exception = assertThrows(
				SecurityException.class,
				() -> storeMenuService.updateStoreMenu(user, storeId, menuId, reqUpdateStoreMenuDto)
			);

			assertEquals("You do not have permission", exception.getMessage());
			verify(storeRepository, times(1)).findById(storeId);
		}
	}

	// Sort_order
	@Nested
	@DisplayName("가게 메뉴 순서 변경 테스트")
	class UpdateStoreMenuSortOrderTest {

		@Test
		@DisplayName("성공")
		void update_sortOrder_success() {
			/* given */
			UUID storeId = store.getId();

			menu1.setSortOrder(1);
			menu2 = StoreMenu.builder()
				.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				.store(store)
				.image(image)
				.build();
			ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
			menu2.setSortOrder(2);

			UUID menuId = menu2.getId();
			ReqUpdateSortOrderDto reqUpdateSortOrderDto = new ReqUpdateSortOrderDto();
			reqUpdateSortOrderDto.setSortOrder(1); // 이동하고 싶은 위치

			List<StoreMenu> menusToShift = List.of(menu1, menu2);
			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				.thenReturn(Optional.of(menu2));
			when(storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1))
				.thenReturn(menusToShift);

			/* when */
			storeMenuService.updateSortOrder(user, storeId, menuId, reqUpdateSortOrderDto);

			/* then */
			// 타겟 메뉴(menu2) 순서 변경 확인
			assertEquals(1, menu2.getSortOrder());

			// 나머지 메뉴(menu1) 순서 재정렬 확인
			assertEquals(2, menu1.getSortOrder());

			verify(storeMenuRepository, times(2))
				.flush(); // DB flush 호출 검증
		}

		@Test
		@DisplayName("실패 - 가게가 존재하지 않는 경우")
		void failure_storeNotFound() {
			/* given */
			UUID storeId = UUID.randomUUID();
			UUID menuId = UUID.randomUUID();
			ReqUpdateSortOrderDto dto = new ReqUpdateSortOrderDto();
			dto.setSortOrder(1);

			when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateSortOrder(user, storeId, menuId, dto)
			);

			assertEquals("Store not found", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 권한이 없는 경우")
		void failure_noPermission() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();
			user = User.builder()
				.username("customerUser")
				.password("test")
				.role(UserRoleEnum.CUSTOMER)
				.build();

			ReqUpdateSortOrderDto dto = new ReqUpdateSortOrderDto();
			dto.setSortOrder(1);

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			/* when & then */
			SecurityException exception = assertThrows(
				SecurityException.class,
				() -> storeMenuService.updateSortOrder(user, storeId, menuId, dto)
			);

			assertEquals("You do not have permission", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 메뉴가 존재하지 않는 경우")
		void failure_menuNotFound() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = UUID.randomUUID();
			ReqUpdateSortOrderDto dto = new ReqUpdateSortOrderDto();
			dto.setSortOrder(1);

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				.thenReturn(Optional.empty());

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateSortOrder(user, storeId, menuId, dto)
			);

			assertEquals("StoreMenu not found", exception.getMessage());
		}

		@Test
		@DisplayName("실패 - 잘못된 정렬 순서 요청(0 이하의 수)")
		void failure_invalidSortOrder() {
			/* given */
			UUID storeId = store.getId();
			UUID menuId = menu1.getId();
			ReqUpdateSortOrderDto dto = new ReqUpdateSortOrderDto();
			dto.setSortOrder(-1); // 비정상 값

			when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				.thenReturn(Optional.of(menu1));

			/* when & then */
			IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> storeMenuService.updateSortOrder(user, storeId, menuId, dto)
			);

			assertEquals("sortOrder는 1 이상이어야 합니다.", exception.getMessage());
		}
	}

	   // Visibility
	   @Nested
	   @DisplayName("가게 메뉴 숨기기 / 보이기 테스트")
	   class UpdateStoreMenuVisibilityTest {

		   @Test
		   @DisplayName("성공 - 숨기기 체크")
		   void update_visible_success() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();
			   // 현재 보이는 메뉴
			   menu1.setHiddenAt(false);

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(true); // 숨기기 설정

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.of(menu1));

			   /* when */
			   storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto);

			   /* then */
			   assertNotNull(menu1.getHiddenAt()); // 숨김 상태
			   verify(storeRepository, times(1)).findById(storeId);
			   verify(storeMenuRepository, times(1))
				   .findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
		   }

		   @Test
		   @DisplayName("성공 - 숨기기 체크 해제")
		   void update_hide_success() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();
			   // 이미 숨겨져 있는 메뉴 (hiddenAt 존재)
			   menu1.setHiddenAt(true);

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(false); // 숨기기 해제 설정

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.of(menu1));

			   /* when */
			   storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto);

			   /* then */
			   assertNull(menu1.getHiddenAt()); // 숨김 해제 → null 처리
			   verify(storeRepository, times(1)).findById(storeId);
			   verify(storeMenuRepository, times(1))
				   .findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
		   }

		   @Test
		   @DisplayName("실패 - 가게가 존재하지 않는 경우")
		   void failure_storeNotFound() {
			   /* given */
			   UUID storeId = UUID.randomUUID();
			   UUID menuId = UUID.randomUUID();

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(true);

			   when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

			   /* when & then */
			   IllegalArgumentException exception = assertThrows(
				   IllegalArgumentException.class,
				   () -> storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto)
			   );

			   assertEquals("Store not found", exception.getMessage());
		   }

		   @Test
		   @DisplayName("실패 - 권한이 없는 사용자")
		   void failure_noPermission() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();

			   User customer = User.builder()
				   .username("customerUser")
				   .password("test123")
				   .role(UserRoleEnum.CUSTOMER)
				   .build();

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(true);

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			   /* when & then */
			   SecurityException exception = assertThrows(
				   SecurityException.class,
				   () -> storeMenuService.updateVisibility(customer, storeId, menuId, reqUpdateVisibilityDto)
			   );

			   assertEquals("You do not have permission", exception.getMessage());
		   }

		   @Test
		   @DisplayName("실패 - 메뉴가 존재하지 않는 경우")
		   void failure_menuNotFound() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = UUID.randomUUID();

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(false);

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.empty());

			   /* when & then */
			   IllegalArgumentException exception = assertThrows(
				   IllegalArgumentException.class,
				   () -> storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto)
			   );

			   assertEquals("StoreMenu not found", exception.getMessage());
		   }

		   @Test
		   @DisplayName("실패 - 숨기기 상태에서 다시 숨기기 요청")
		   void failure_alreadyHidden() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();

			   // 이미 숨겨진 상태
			   menu1.setHiddenAt(true);

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(true); // 또 숨기기 요청

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.of(menu1));

			   /* when & then */
			   IllegalStateException exception = assertThrows(
				   IllegalStateException.class,
				   () -> storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto)
			   );

			   assertEquals("이미 숨김 상태입니다.", exception.getMessage());
			   verify(storeRepository, times(1)).findById(storeId);
			   verify(storeMenuRepository, times(1))
				   .findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
		   }

		   @Test
		   @DisplayName("실패 - 이미 숨김 해제 상태에서 다시 숨김 해제 요청")
		   void failure_alreadyVisible() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();

			   // 이미 표시 중 (숨김 해제 상태)
			   menu1.setHiddenAt(false);

			   ReqUpdateVisibilityDto reqUpdateVisibilityDto = new ReqUpdateVisibilityDto();
			   reqUpdateVisibilityDto.setHidden(false); // 다시 보이기 요청

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.of(menu1));

			   /* when & then */
			   IllegalStateException exception = assertThrows(
				   IllegalStateException.class,
				   () -> storeMenuService.updateVisibility(user, storeId, menuId, reqUpdateVisibilityDto)
			   );

			   assertEquals("이미 표시 상태입니다.", exception.getMessage());
			   verify(storeRepository, times(1)).findById(storeId);
			   verify(storeMenuRepository, times(1))
				   .findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
		   }
	   }

	   // Delete
	   @Nested
	   @DisplayName("가게 메뉴 삭제 테스트")
	   class DeleteStoreMenuTest {

	       @Test
	       @DisplayName("성공 - 메뉴 삭제(남은 메뉴들 순서 재정렬)")
		   void delete_success() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();

			   menu2 = StoreMenu.builder()
				   .reqCreateStoreMenuDto(reqCreateStoreMenuDto)
				   .store(store)
				   .image(image)
				   .build();
			   ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
			   menu1.setSortOrder(1);
			   menu2.setSortOrder(2);

			   List<StoreMenu> remainingMenus = List.of(menu1, menu2);

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.of(menu1));
			   when(storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1))
				   .thenReturn(remainingMenus);

			   /* when */
			   storeMenuService.deleteStoreMenu(user, storeId, menuId);

			   /* then */
			   assertNotNull(menu1.getDeletedAt()); // softDelete 적용 확인
			   verify(storeRepository, times(1)).findById(storeId);
			   verify(storeMenuRepository, times(1))
				   .findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null);
			   verify(storeMenuRepository, times(1))
				   .findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1);
		   }

		   @Test
		   @DisplayName("실패 - 가게가 존재하지 않는 경우")
		   void failure_storeNotFound() {
			   /* given */
			   UUID storeId = UUID.randomUUID();
			   UUID menuId = UUID.randomUUID();

			   when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

			   /* when & then */
			   IllegalArgumentException exception = assertThrows(
				   IllegalArgumentException.class,
				   () -> storeMenuService.deleteStoreMenu(user, storeId, menuId)
			   );

			   assertEquals("Store not found", exception.getMessage());
		   }

		   @Test
		   @DisplayName("실패 - 권한이 없는 사용자")
		   void failure_noPermission() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = menu1.getId();

			   user = User.builder()
				   .username("customerUser")
				   .password("test123")
				   .role(UserRoleEnum.CUSTOMER)
				   .build();

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

			   /* when & then */
			   SecurityException exception = assertThrows(
				   SecurityException.class,
				   () -> storeMenuService.deleteStoreMenu(user, storeId, menuId)
			   );

			   assertEquals("You do not have permission", exception.getMessage());
		   }

		   @Test
		   @DisplayName("실패 - 메뉴가 존재하지 않는 경우")
		   void failure_menuNotFound() {
			   /* given */
			   UUID storeId = store.getId();
			   UUID menuId = UUID.randomUUID();

			   when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
			   when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
				   .thenReturn(Optional.empty());

			   /* when & then */
			   IllegalArgumentException exception = assertThrows(
				   IllegalArgumentException.class,
				   () -> storeMenuService.deleteStoreMenu(user, storeId, menuId)
			   );

			   assertEquals("StoreMenu not found", exception.getMessage());
		   }
	   }
}
