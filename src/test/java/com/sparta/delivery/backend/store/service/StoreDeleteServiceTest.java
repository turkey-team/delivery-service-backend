package com.sparta.delivery.backend.store.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.cart.entity.Cart;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.payment.entity.PayMethod;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.reply.entity.Reply;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreCategory;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.dto.ReqCreateStoreMenuDto;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.store.repository.StoreDetailsRepository;
import com.sparta.delivery.backend.store.repository.StoreImageRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
public class StoreDeleteServiceTest {
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


	private User userCustomer;
	private Customer customer;

	private User userOwner;
	private Owner owner;
	private Store store;

	private Category category;
	private StoreCategory storeCategory;
	private Image image;
	private StoreImage storeImage;
	private StoreDetails storeDetails;
	private StoreMenu storeMenu;
	private Image menuImage;

	private Cart cart;

	private Order order;

	private Review review;
	private Reply reply;

	private Sido sido;
	private Sigungu sigungu;
	private Dong dong;

	@BeforeEach
	void setUp() {

		userCustomer = User.builder()
			.username("testCustomer1")
			.password("testCustomer1234!")
			.role(UserRoleEnum.OWNER)
			.build();
		ReflectionTestUtils.setField(userCustomer, "publicId", UUID.randomUUID());

		customer = Customer.builder()
			.user(userCustomer)
			.nickname("customer1")
			.email("test@example.com")
			.phoneNumber("01012345678")
			.build();
		ReflectionTestUtils.setField(customer, "id", UUID.randomUUID());

		userOwner = User.builder()
			.username("testOwner1")
			.password("testOwner1@@")
			.role(UserRoleEnum.OWNER)
			.build();
		ReflectionTestUtils.setField(userOwner, "publicId", UUID.randomUUID());

		owner = Owner.builder()
			.nickname("owner1234")
			.email("testOwner1@naver.com")
			.phoneNumber("01022223333")
			.user(userOwner)
			.build();
		ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());

		sido = Sido.builder().name("서울특별시").code("11").build();
		sigungu = Sigungu.builder().sido(sido).name("강남구").code("680").build();
		dong = Dong.builder().sigungu(sigungu).code("010").build();

		ReflectionTestUtils.setField(dong, "id", UUID.randomUUID());

		store = Store.builder()
			.owner(owner)
			.name("햄버거 가게")
			.address(Address.builder().dong(dong).fullAddress("강남구 테스트동 123").build())
			.reviewRate(0.0)
			.minOrderPrice(13000)
			.deliveryFee(1500)
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("01012345678")
			.build();
		ReflectionTestUtils.setField(store, "id", UUID.randomUUID());

		storeDetails = StoreDetails.builder()
			.store(store)
			.businessNumber("1234567890")
			.operationHours("10:00~22:00")
			.description("테스트용입니다.")
			.build();
		ReflectionTestUtils.setField(storeDetails, "id", UUID.randomUUID());

		image = Image.builder()
			.imageUrl("https://example.com/hamburger.jpg")
			.build();

		ReflectionTestUtils.setField(image, "id", UUID.randomUUID());

		storeImage = StoreImage.builder().store(store).image(image).build();
		ReflectionTestUtils.setField(storeImage, "id", UUID.randomUUID());

		category = Category.builder().name("햄버거").build();
		ReflectionTestUtils.setField(category, "id", UUID.randomUUID());

		storeCategory = StoreCategory.builder().store(store).category(category).build();
		ReflectionTestUtils.setField(storeCategory, "id", UUID.randomUUID());

		ReqCreateStoreMenuDto reqCreateStoreMenuDto = new ReqCreateStoreMenuDto();
		reqCreateStoreMenuDto.setName("치즈버거");
		reqCreateStoreMenuDto.setImageUrl(image.getImageUrl());
		reqCreateStoreMenuDto.setPrice(4000);
		reqCreateStoreMenuDto.setDescription("치즈, 소고기, 피클, 마요네즈가 들어있습니다.");
		reqCreateStoreMenuDto.setPrepTime("15분");
		reqCreateStoreMenuDto.setStockStatus(StockStatus.ON_SALE);
		reqCreateStoreMenuDto.setIsHidden(false);

		menuImage = Image.builder().imageUrl("menu.png").build();
		ReflectionTestUtils.setField(menuImage, "id", UUID.randomUUID());

		storeMenu = StoreMenu.builder()
			.store(store)
			.image(menuImage)
			.reqCreateStoreMenuDto(reqCreateStoreMenuDto)
			.build();
		ReflectionTestUtils.setField(storeMenu, "id", UUID.randomUUID());

		cart = Cart.builder().customer(customer).menu(storeMenu).build();
		ReflectionTestUtils.setField(cart, "id", UUID.randomUUID());

		order = Order.builder()
			.store(store)
			.orderStatus(OrderStatus.COMPLETED)
			.customer(customer)
			.gu("gu")
			.dong("dong")
			.addressDetails("123로")
			.payMethod(PayMethod.CARD).build();

		ReflectionTestUtils.setField(order, "id", UUID.randomUUID());

		review = Review.builder()
			.customer(customer)
			.rate(5)
			.context("맛있어요")
			.store(store)
			.imageUrl("xxxxxxxx.png")
			.build();

		ReflectionTestUtils.setField(review, "id", UUID.randomUUID());

		reply = Reply.builder()
			.review(review)
			.owner(owner)
			.context("감사합니다")
			.build();

	}

	@Test
	@DisplayName("Store 연쇄 삭제 성공")
	void deleteStoreRelay(){
		// given
		Long deletedBy = 123L;

		ReflectionTestUtils.setField(store, "storeDetails", storeDetails);
		ReflectionTestUtils.setField(store, "storeImages", List.of(storeImage));
		ReflectionTestUtils.setField(store, "storeMenus", List.of(storeMenu));
		ReflectionTestUtils.setField(store, "reviews", List.of(review));
		ReflectionTestUtils.setField(store, "storeCategories", List.of(storeCategory));
		ReflectionTestUtils.setField(review, "replies", List.of(reply));
		ReflectionTestUtils.setField(storeMenu, "image", menuImage);  // null로 되어 있음
		ReflectionTestUtils.setField(storeMenu, "carts", List.of(cart));

		// when
		store.delete(deletedBy);

		// then
		assertTrue(store.isDeleted());
		assertEquals(deletedBy, store.getDeletedBy());

		assertTrue(storeDetails.isDeleted());
		assertEquals(deletedBy, storeDetails.getDeletedBy());

		assertTrue(storeImage.getDeletedAt() != null);
		assertTrue(storeImage.getImage().isDeleted());

		assertTrue(storeMenu.isDeleted());
		assertTrue(storeMenu.getImage().isDeleted());
		assertTrue(cart.getDeletedAt() != null);

		assertTrue(review.isDeleted());
		assertTrue(reply.isDeleted());

		assertTrue(storeCategory.getDeletedAt() != null);

	}


}
