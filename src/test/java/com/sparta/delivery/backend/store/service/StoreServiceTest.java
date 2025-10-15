package com.sparta.delivery.backend.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ReqDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.dto.ResDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreCategory;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.store.repository.StoreDetailsRepository;
import com.sparta.delivery.backend.store.repository.StoreImageRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@Mock
	StoreRepository storeRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private ImageRepository imageRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private DongRepository dongRepository;

	@Mock
	private StoreDetailsRepository storeDetailsRepository;

	@Mock
	private StoreCategoryRepository storeCategoryRepository;

	@Mock
	private StoreImageRepository storeImageRepository;

	@InjectMocks
	StoreService storeService;

	private UUID customerId;
	private Long userId;
	private User mockUser;
	private Customer mockCustomer;

	private UUID ownerId;
	private Owner mockOwner;
	private User mockOwnerUser;
	private Long ownerUserId;

	private UUID managerId;
	private User mockManagerUser;
	private Manager mockManager;
	private Long managerUserId;

	private UUID storeId;
	private Store mockStore;

	private UUID storeDetailId;
	private StoreDetails mockStoreDetails;

	private UUID hansikCategoryId;
	private Category hansikCategory;
	private StoreCategory hansikStoreCategory;

	private UUID bunsikCategoryId;
	private Category bunsikCategory;
	private StoreCategory bunsikStoreCategory;

	private UUID storeImageId;
	private Image storeTypeImage;
	private StoreImage storeStoreImage;

	private UUID businessImageId;
	private Image businessTypeImage;
	private StoreImage businessStoreImage;


	@BeforeEach
	void setUp() {

		customerId = UUID.randomUUID();
		userId = 1L;

		storeId = UUID.randomUUID();
		storeDetailId = UUID.randomUUID();

		ownerId = UUID.randomUUID();
		ownerUserId = 2L;

		managerId = UUID.randomUUID();
		managerUserId = 3L;

		hansikCategoryId = UUID.randomUUID();
		bunsikCategoryId = UUID.randomUUID();

		storeImageId = UUID.randomUUID();
		businessImageId = UUID.randomUUID();

		mockUser = mock(User.class);
		when(mockUser.getId()).thenReturn(userId);
		when(mockUser.getRole()).thenReturn(UserRoleEnum.CUSTOMER);

		mockOwnerUser = mock(User.class);
		when(mockOwnerUser.getRole()).thenReturn(UserRoleEnum.OWNER);
		when(mockOwnerUser.getId()).thenReturn(ownerUserId);
		mockOwner = mock(Owner.class);
		when(mockOwner.getId()).thenReturn(ownerId);
		when(mockOwner.getUser()).thenReturn(mockOwnerUser);

		mockManagerUser = mock(User.class);
		when(mockManagerUser.getRole()).thenReturn(UserRoleEnum.MANAGER);
		when(mockManagerUser.getId()).thenReturn(managerUserId);
		mockManager = mock(Manager.class);
		when(mockManager.getId()).thenReturn(managerId);
		when(mockManager.getUser()).thenReturn(mockManagerUser);

		mockStore = mock(Store.class);
		when(mockStore.getId()).thenReturn(storeId);
		when(mockStore.getOwner()).thenReturn(mockOwner);

		mockStoreDetails = mock(StoreDetails.class);
		when(mockStoreDetails.getId()).thenReturn(storeDetailId);
		when(mockStoreDetails.getStore()).thenReturn(mockStore);
		when(mockStoreDetails.getStore().getId()).thenReturn(storeId);

		hansikCategory = mock(Category.class);
		when(hansikCategory.getId()).thenReturn(hansikCategoryId);
		hansikStoreCategory = mock(StoreCategory.class);
		when(hansikStoreCategory.getCategory()).thenReturn(hansikCategory);
		when(hansikStoreCategory.getStore()).thenReturn(mockStore);

		bunsikCategory = mock(Category.class);
		when(bunsikCategory.getId()).thenReturn(bunsikCategoryId);
		bunsikStoreCategory = mock(StoreCategory.class);
		when(bunsikStoreCategory.getCategory()).thenReturn(bunsikCategory);
		when(bunsikStoreCategory.getStore()).thenReturn(mockStore);

		storeTypeImage = mock(Image.class);
		when(storeTypeImage.getId()).thenReturn(storeImageId);

		storeStoreImage = mock(StoreImage.class);
		when(storeStoreImage.getImage()).thenReturn(storeTypeImage);
		when(storeStoreImage.getStore()).thenReturn(mockStore);

		businessTypeImage = mock(Image.class);
		when(businessTypeImage.getId()).thenReturn(businessImageId);

		businessStoreImage = mock(StoreImage.class);
		when(businessStoreImage.getImage()).thenReturn(businessTypeImage);
		when(businessStoreImage.getStore()).thenReturn(mockStore);

	}

	@Test
	@DisplayName("Store 생성 : 생성 성공")
	@Disabled
	void storeCreateTestSuccess() {
		//Owner, User Setter 설정

		// public void setId(UUID id) {
		//         this.id = id;
		//     }
		//     baseEntity에 추가

		// ReqCreateStoreDto Builder 추가

		// given
		long userId = 1L;
		UUID userPublicId = UUID.randomUUID();
		UUID ownerId = UUID.randomUUID();

		UUID storeId = UUID.randomUUID();
		UUID imageId = UUID.randomUUID();
		UUID categoryId = UUID.randomUUID();

		// 테스트용 User Owner 생성
		String usernae = "user01";
		String password = "User01!wasd";
		UserRoleEnum role = UserRoleEnum.OWNER;


		User user = User.builder().username(usernae).password(password).role(role).build();
		// user.setId(userId);
		// user.setPublicId(userPublicId);

		Owner owner = Owner.builder().nickname("ownerTest").email("abc@naver.com").phoneNumber("010147852369").user(user).build();
		// owner.setId(ownerId);

		Category category = Category.builder().name("한식").build();
		// category.setId(categoryId);

		List<UUID> categories = List.of(category.getId());
		List<Category> categoriesList = List.of(category);

		Dong dong = Dong.builder().code("123").name("테스트동").build();
		// dong.setId(UUID.randomUUID());

		List<String> storeImageUrls = List.of("store1.jpg", "store2.jpg");
		List<String> businessImageUrls = List.of("biz1.jpg");

		Map<String, List<String>> images = Map.of(
			"store", storeImageUrls,
			"business", businessImageUrls
		);

		// ReqCreateStoreDto requestDto = ReqCreateStoreDto.builder()
		// 	.name("김밥천국")
		// 	.regionCode("123")
		// 	.addressDetail("테스트로123길")
		// 	.phoneNumber("01032615487")
		// 	.description("설명")
		// 	.holiday("수요일")
		// 	.operatingHours("AM10:30~PM10:30")
		// 	.businessNumber("789456126")
		// 	.images(images)
		// 	.deliveryFee(3000)
		// 	.minOrderPrice(12000)
		// 	.categories(categories)
		// 	.build();

		// Store store = Store.builder()
		// 	.owner(owner)
		// 	.name(requestDto.getName())
		// 	.regionDong(dong)
		// 	.addressDetails(requestDto.getAddressDetail())
		// 	.deliveryFee(requestDto.getDeliveryFee())
		// 	.minOrderPrice(requestDto.getMinOrderPrice())
		// 	.reviewRate(5.0)
		// 	.phoneNumber(requestDto.getPhoneNumber())
		// 	.status(StoreStatusEnum.CLOSED)
		// 	.build();
		//
		// store.setId(storeId);

		// when
		when(ownerRepository.findByUserPublicIdAndDeletedAtIsNull(userPublicId)).thenReturn(Optional.of(owner));
		when(dongRepository.findByCode("123")).thenReturn(Optional.of(dong));
		when(categoryRepository.findAllById(List.of(categoryId))).thenReturn(categoriesList);

		List<Image> storeImages = storeImageUrls.stream()
			.map(url -> Image.builder().imageUrl(url).build())
			.collect(Collectors.toList());
		List<Image> businessImages = businessImageUrls.stream()
			.map(url -> Image.builder().imageUrl(url).build())
			.collect(Collectors.toList());


		// then
		when(imageRepository.save(any(Image.class))).thenAnswer(inv -> inv.getArgument(0));
		when(storeRepository.save(any(Store.class))).thenAnswer(invocation -> {
			Store saved = invocation.getArgument(0);
			//saved.setId(storeId); // ID 강제 세팅
			return saved;
		});
		when(storeImageRepository.save(any(StoreImage.class))).thenAnswer(inv -> inv.getArgument(0));

		// when
		//ResCreateStoreDto result = storeService.createStore(requestDto, user);

		// then
		// assertNotNull(result);
		// assertEquals("김밥천국", result.getName());
		// assertNotNull(result.getId());
		///
		// 저장 메서드들이 잘 호출되었는지 검증
		// verify(ownerRepository, times(1)).findByUser_PublicId(userPublicId);
		// verify(dongRepository, times(1)).findByCode("123");
		// verify(categoryRepository, times(1)).findAllById(List.of(categoryId));
		// verify(storeRepository, times(1)).save(any(Store.class));
		// verify(storeDetailsRepository, times(1)).save(any(StoreDetails.class));
		// verify(storeCategoryRepository, times(1)).saveAll(anyList());
		// verify(imageRepository, times(storeImageUrls.size() + businessImageUrls.size())).save(any(Image.class));
		// verify(storeImageRepository, times(storeImageUrls.size() + businessImageUrls.size())).save(any(StoreImage.class));

	}

	@Test
	@DisplayName("store 생성 : 권한이 CUSTOMER여서 실패")
	void storeCreateTestFailure() {
		// given
		ReqCreateStoreDto requestDto = new ReqCreateStoreDto(
			"김밥천국",                      // name
			"123",                         // regionCode
			"서울시 종로구",                // addressDetail
			"02-1234-5678",                // phoneNumber
			3000,                          // deliveryFee
			12000,                         // minOrderPrice
			List.of(hansikCategoryId),     // categories
			List.of(
				new ReqCreateStoreDto.ImageDto("store.jpg", "STORE"),
				new ReqCreateStoreDto.ImageDto("biz.jpg", "BUSINESS")
			),                             // images
			ownerId,                       // ownerId
			"광화문 김밥천국입니다.",        // description
			"08:00 ~ 20:00",               // operatingHours
			"일요일",                      // holiday
			"1234567890"                   // businessNumber
		);

		// when & then
		AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
			storeService.createStore(requestDto, mockUser);
		});

		assertEquals("가게 생성 권한이 없습니다", exception.getMessage());

	}

	@Test
	@DisplayName("store 생성 : Manager 일 때 Owner Id가 DTO에 없어서 실패")
	void storeCreateTest_Failure_OwnerId() {
		// given
		ReqCreateStoreDto requestDto = new ReqCreateStoreDto(
			"김밥천국",                      // name
			"123",                         // regionCode
			"서울시 종로구",                // addressDetail
			"02-1234-5678",                // phoneNumber
			3000,                          // deliveryFee
			12000,                         // minOrderPrice
			List.of(hansikCategoryId),     // categories
			List.of(
				new ReqCreateStoreDto.ImageDto("store.jpg", "STORE"),
				new ReqCreateStoreDto.ImageDto("biz.jpg", "BUSINESS")
			),                             // images
			ownerId,                       // ownerId
			"광화문 김밥천국입니다.",        // description
			"08:00 ~ 20:00",               // operatingHours
			"일요일",                      // holiday
			"1234567890"                   // businessNumber
		);

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			storeService.createStore(requestDto, mockManagerUser);
		});

		assertEquals("OwnerId가 적절하지 않습니다.", exception.getReason());

	}


	@Test
	@DisplayName("store 정보 수정 : 성공")
	void storeUpdateTest_Success() {
		// given
		ReqUpdateStoreInfoDto requestDto = new ReqUpdateStoreInfoDto(
			"김밥천국 광화문점",
			"광화문로 1길 123",
			"02-1111-7777",
			"123",
			List.of(hansikCategoryId),
			List.of(new ReqUpdateStoreInfoDto.ImageDto(null,"sotre1.png","STORE"))
		);

		Sigungu sigungu = mock(Sigungu.class);
		Dong dong = Dong.builder().code("123").name("테스트동").sigungu(sigungu).build();

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(storeDetailsRepository.findByStoreId(storeId)).thenReturn(Optional.of(mockStoreDetails));
		when(dongRepository.findByCode("123")).thenReturn(Optional.of(dong));

		when(mockStore.getOwner()).thenReturn(mockOwner);
		when(mockStore.getRegionDong()).thenReturn(dong);

		when(mockOwner.getUser()).thenReturn(mockOwnerUser);

		Category category = mock(Category.class);
		when(category.getId()).thenReturn(hansikCategoryId);
		when(categoryRepository.findAllById(any())).thenReturn(List.of(category));
		when(mockStore.getStoreCategories()).thenReturn(new ArrayList<>());

		when(mockStore.getStoreImages()).thenReturn(new ArrayList<>());
		when(imageRepository.findByImageUrl("store1.png")).thenReturn(Optional.empty());
		when(imageRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
		when(storeImageRepository.saveAll(any())).thenReturn(List.of());

		doAnswer(invocation -> {
			ReqUpdateStoreInfoDto dto = invocation.getArgument(0);
			when(mockStore.getName()).thenReturn(dto.getStoreName());
			return null;
		}).when(mockStore).updateStoreInfo(any(), any());

		// when
		ResUpdateStoreInfoDto result = storeService.updateStoreInfo(storeId, requestDto, mockOwnerUser);

		// then
		assertNotNull(result);
		assertEquals("김밥천국 광화문점", result.getStoreName());
		verify(storeRepository, times(1)).save(any(Store.class));

	}

	@Test
	@DisplayName("store 정보 수정 : 존재하지 않는 가게여서 실패")
	void updateStoreInfoTest_fail_storeNotFound() {
		// given
		ReqUpdateStoreInfoDto requestDto = new ReqUpdateStoreInfoDto(
			"김밥천국 광화문점",
			"광화문로 1길 11",
			"02-9874-6521",
			"123",
			List.of(hansikCategoryId),
			List.of(new ReqUpdateStoreInfoDto.ImageDto(null, "store1.png", "store"))
		);

		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// when & then
		assertThrows(ResponseStatusException.class, () -> {
			storeService.updateStoreInfo(storeId, requestDto, mockOwnerUser);
		});
	}

	@Test
	@DisplayName("store 배달 정보 수정 : 성공")
	void updateStoreDetailsTest_Success() {
		// given
		ReqUpdateStoreDetailsDto requestDto = new ReqUpdateStoreDetailsDto(
			13000,
			3000,
			"월요일",
			"09:00 - 21:00",
			"맛있는 가게"
		);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(storeDetailsRepository.findByStoreId(storeId)).thenReturn(Optional.of(mockStoreDetails));

		doNothing().when(mockStore).updateStoreDetails(requestDto.getDeliveryFee(), requestDto.getMinOrderPrice());
		doNothing().when(mockStoreDetails).updateStoreDetails(requestDto.getDescription(), requestDto.getHoliday(), requestDto.getOperationHours());

		when(mockStore.getId()).thenReturn(storeId);
		when(mockStore.getName()).thenReturn("가게 이름");
		when(mockStore.getDeliveryFee()).thenReturn(requestDto.getDeliveryFee());
		when(mockStore.getMinOrderPrice()).thenReturn(requestDto.getMinOrderPrice());
		when(mockStoreDetails.getDescription()).thenReturn(requestDto.getDescription());
		when(mockStoreDetails.getHoliday()).thenReturn(requestDto.getHoliday());
		when(mockStoreDetails.getOperationHours()).thenReturn(requestDto.getOperationHours());

		// when
		ResUpdateStoreDetailsDto result = storeService.updateStoreDetails(storeId, requestDto, mockOwnerUser);

		// then
		assertNotNull(result);
		assertEquals(storeId, result.getStoreId());
		assertEquals("가게 이름", result.getStoreName());
		assertEquals(3000, result.getDeliveryFee());
		assertEquals(13000, result.getMinOrderPrice());
		assertEquals("맛있는 가게", result.getDescription());
		assertEquals("월요일", result.getHoliday());
		assertEquals("09:00 - 21:00", result.getOperationHours());

		verify(storeRepository, times(1)).save(mockStore);
		verify(storeDetailsRepository, times(1)).save(mockStoreDetails);
	}

	@Test
	@DisplayName("storeDetails 수정 : 실패 - 가게가 존재하지 않을 때")
	void updateStoreDetailsTest_Fail_storeNotFound() {
		// given
		ReqUpdateStoreDetailsDto requestDto = new ReqUpdateStoreDetailsDto(
			13000
			, 50000
			, "수요일"
			, "08:00 ~ 20:00"
			, "설명"
		);

		when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> storeService.updateStoreDetails(storeId, requestDto, mockUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("존재하지 않는 가게입니다.", exception.getReason());
	}


	@Test
	@DisplayName("가게 목록 조회 - 정상 조회 (키워드, 카테고리 포함)")
	void getStoresTest_Success() {
		// given
		int page = 1;
		int size = 10;
		String sort = "name";
		String keyword = "맛집";
		String categoryId = hansikCategoryId.toString();

		Pageable pageable = PageRequest.of(page - 1, size);

		List<ResGetListStoreDto> mockStoreList = List.of(
			ResGetListStoreDto.builder()
				.storeId(UUID.randomUUID())
				.name("맛있는 가게")
				.reviewCnt(10)
				.reviewRate(4.5)
				.deliveryFee(3000)
				.minOrderPrice(10000)
				.status(StoreStatusEnum.OPEN)
				.build()
		);

		Page<ResGetListStoreDto> mockPage = new PageImpl<>(mockStoreList, pageable, mockStoreList.size());

		when(storeRepository.getStores(any(Pageable.class), eq(sort), eq(keyword), eq(hansikCategoryId)))
			.thenReturn(mockPage);

		// when
		Page<ResGetListStoreDto> result = storeService.getStores(page, size, sort, keyword, categoryId, mockUser);

		// then
		assertNotNull(result);
		assertEquals(1, result.getTotalPages());
		assertEquals(1, result.getContent().size());
		assertEquals("맛있는 가게", result.getContent().get(0).getName());
		verify(storeRepository, times(1)).getStores(any(Pageable.class), eq(sort), eq(keyword), eq(hansikCategoryId));
	}

	@Test
	@DisplayName("가게 목록 조회 - category가 유효하지 않을 경우")
	void testGetStoresInvalidCategoryId() {
		// given
		int page = 1;
		int size = 10;
		String sort = "name";
		String keyword = null;
		String invalidCategoryId = "invalid-uuid";

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
			storeService.getStores(page, size, sort, keyword, invalidCategoryId, mockUser);
		});

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("올바른 카테고리가 아닙니다.", exception.getReason());
	}

	@Test
	@DisplayName("가게 상태 수정 : 성공")
	void updateStoreStatusTest_Success() {
		// given
		ReqUpdateStoreStatusDto requestDto = new ReqUpdateStoreStatusDto();
		ReflectionTestUtils.setField(requestDto, "storeStatus", StoreStatusEnum.CLOSED);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(mockStore.getStatus())
			.thenReturn(StoreStatusEnum.OPEN)     // 첫 번째 호출: 상태 비교용
			.thenReturn(StoreStatusEnum.CLOSED);

		doNothing().when(mockStore).updateStoreStatus(StoreStatusEnum.CLOSED);

		when(mockStore.getId()).thenReturn(storeId);

		// when
		ResUpdateStoreStatusDto result = storeService.updateStoreStatus(storeId, requestDto, mockOwnerUser);

		// then
		assertNotNull(result);
		assertEquals(storeId, result.getStoreId());
		assertEquals(StoreStatusEnum.CLOSED, result.getStoreStatus());

		verify(storeRepository).save(mockStore);
	}

	@Test
	@DisplayName("가게 상태 수정 : 동일한 가게 상태로 변경 요청해 실패")
	void updateStoreStatusTest_Fail_SameStatus() {
		// given
		ReqUpdateStoreStatusDto requestDto = new ReqUpdateStoreStatusDto();
		ReflectionTestUtils.setField(requestDto, "storeStatus", StoreStatusEnum.OPEN);

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(mockStore.getStatus()).thenReturn(StoreStatusEnum.OPEN);

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> storeService.updateStoreStatus(storeId, requestDto, mockOwnerUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("현재와 동일한 상태로는 변경할 수 없습니다.", exception.getReason());
	}

	@Test
	@DisplayName("가게 삭제 : 성공")
	void deleteStoreTest_Success() {
		// given
		String correctBusinessNumber = "123-45-67890";

		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(storeDetailsRepository.findByStoreId(storeId)).thenReturn(Optional.of(mockStoreDetails));
		when(mockStoreDetails.getBusinessNumber()).thenReturn(correctBusinessNumber);
		when(mockStore.isDeleted()).thenReturn(false);
		when(mockStoreDetails.isDeleted()).thenReturn(false);

		ReqDeleteStoreDto requestDto = new ReqDeleteStoreDto(correctBusinessNumber);

		doNothing().when(mockStore).softDelete(userId);
		doNothing().when(mockStoreDetails).softDelete(userId);

		// when
		ResDeleteStoreDto result = storeService.deleteStore(storeId, requestDto, mockOwnerUser);

		// then
		assertEquals(storeId, result.getStoreId());
		assertEquals(correctBusinessNumber, result.getBusinessNumber());
		verify(storeRepository).save(mockStore);
		verify(storeDetailsRepository).save(mockStoreDetails);
	}

	@Test
	@DisplayName("가게 삭제 : 실패")
	void deleteStoreTest_Fail_StoreNotFound() {
		// given
		UUID nonExistentStoreId = UUID.randomUUID();
		when(storeRepository.findById(nonExistentStoreId)).thenReturn(Optional.empty());

		ReqDeleteStoreDto requestDto = new ReqDeleteStoreDto("123-45-67890");

		// when & then
		ResponseStatusException exception = assertThrows(ResponseStatusException.class,
			() -> storeService.deleteStore(nonExistentStoreId, requestDto, mockUser));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("존재하지 않는 가게입니다.", exception.getReason());
	}

}
