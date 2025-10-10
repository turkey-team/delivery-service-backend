package com.sparta.delivery.backend.store.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.review.dto.ReviewRegisterDto;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResCreateStoreDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.store.repository.StoreDetailsRepository;
import com.sparta.delivery.backend.store.repository.StoreImageRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
@Disabled
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

	@Test
	@Disabled
	void storeCreateTestSuccess() {
		//Owner, User Setter 설정

		// public void setId(UUID id) {
		//         this.id = id;
		//     }
		//     baseEntity에 추가

		// ReqCreateStoreDto Builder 추가

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

		Owner owner = Owner.builder().nickname("ownerTest").email("abc@naver.com").phoneNumber("010147852369").businessNumber("9874563210").user(user).build();
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

		//Test 1
		when(ownerRepository.findByUser_PublicId(userPublicId)).thenReturn(owner);
		when(dongRepository.findByCode("123")).thenReturn(dong);
		when(categoryRepository.findAllById(List.of(categoryId))).thenReturn(categoriesList);

		List<Image> storeImages = storeImageUrls.stream()
			.map(url -> Image.builder().imageUrl(url).build())
			.collect(Collectors.toList());
		List<Image> businessImages = businessImageUrls.stream()
			.map(url -> Image.builder().imageUrl(url).build())
			.collect(Collectors.toList());



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
}
