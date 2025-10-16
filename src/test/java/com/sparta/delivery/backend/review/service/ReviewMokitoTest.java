package com.sparta.delivery.backend.review.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.customer.repository.CustomerRepository;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.order.entity.Order;
import com.sparta.delivery.backend.order.enums.OrderStatus;
import com.sparta.delivery.backend.order.repository.OrderRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.entity.Sido;
import com.sparta.delivery.backend.region.entity.Sigungu;
import com.sparta.delivery.backend.reply.entity.Reply;
import com.sparta.delivery.backend.reply.repository.ReplyRepository;
import com.sparta.delivery.backend.reply.service.ReplyService;
import com.sparta.delivery.backend.review.dto.ReqCreateReviewDto;
import com.sparta.delivery.backend.review.dto.ReqUpdateReviewDto;
import com.sparta.delivery.backend.review.dto.ResDeleteReviewDto;
import com.sparta.delivery.backend.review.dto.ResResultReviewDto;
import com.sparta.delivery.backend.review.dto.ResViewReviewDto;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.review.util.ReviewGenerationUtil;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
public class ReviewMokitoTest {

	@InjectMocks
	private ReviewService reviewService;

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private CustomerRepository customerRepository;
	@Mock
	private StoreRepository storeRepository;
	@Mock
	private OrderRepository orderRepository;
	@Mock
	private CacheManager cacheManager;
	@Mock
	private ReplyService replyService;
	@Mock
	private EntityManager em;

	@Mock
	private Cache reviewCache;

	@Mock
	private ReviewGenerationUtil util;

	@Mock
	private ReplyRepository replyRepository;

	private UUID storeId;
	private UUID reviewId;
	private Pageable pageable;

	private User mockUser;
	private Customer mockCustomer;
	private Store mockStore;
	private Order mockOrder;
	private Review mockReview;

	@BeforeEach
	void setUp() {
		pageable = PageRequest.of(0, 10);

		// User
		mockUser = User.builder()
			.username("user1")
			.password("password")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		ReflectionTestUtils.setField(mockUser, "id", 1L); // User.id는 Long

		// Customer
		mockCustomer = Customer.builder()
			.user(mockUser)
			.nickname("테스트 사용자")
			.build();
		ReflectionTestUtils.setField(mockCustomer, "id", UUID.randomUUID());

		// Owner
		Owner mockOwner = Owner.builder()
			.user(mockUser)
			.nickname("테스트 사장님")
			.build();
		ReflectionTestUtils.setField(mockOwner, "id", UUID.randomUUID());

		// Store
		mockStore = Store.builder()
			.name("테스트 가게")
			.owner(mockOwner)
			.reviewRate(0.0)
			.minOrderPrice(15000)
			.deliveryFee(3000)
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("010-5555-6666")
			.build();
		storeId = UUID.randomUUID();
		ReflectionTestUtils.setField(mockStore, "id", storeId);

		// Sido -> Sigungu -> Dong
		Sido mockSido = Sido.builder().name("테스트시도").code("01").build();
		Sigungu mockSigungu = Sigungu.builder().sido(mockSido).name("테스트시군구").code("001").build();
		Dong mockDong = Dong.builder().sigungu(mockSigungu).name("테스트동").code("001").build();

		// Order
		mockOrder = Order.builder()
			.customer(mockCustomer)
			.orderStatus(OrderStatus.ACCEPTED)
			.gu("테스트구")
			.dong("테스트동")
			.addressDetails("상세주소")
			.dongEntity(mockDong)
			.build();
		ReflectionTestUtils.setField(mockOrder, "id", UUID.randomUUID());

		// Review
		mockReview = Review.builder()
			.customer(mockCustomer)
			.store(mockStore)
			.context("테스트 리뷰")
			.rate(5)
			.imageUrl(null)
			.build();
		reviewId = UUID.randomUUID();
		ReflectionTestUtils.setField(mockReview, "id", reviewId);

		// CacheManager mock (lenient 처리)
		lenient().when(cacheManager.getCache("reviewList")).thenReturn(reviewCache);
		lenient().doNothing().when(reviewCache).put(anyString(), any());
		lenient().doNothing().when(reviewCache).evict(anyString());

		lenient().doNothing().when(em).flush();

		// ReviewService에 em 강제 주입
		ReflectionTestUtils.setField(reviewService, "em", em);
		ReflectionTestUtils.setField(reviewService, "replyRepository", replyRepository);
		ReflectionTestUtils.setField(reviewService, "util", util);

		// EntityManager flush mock
		lenient().doNothing().when(em).flush();
	}

	@Test
	void testGetReview_Success() {
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));

		ResViewReviewDto result = reviewService.getReview(storeId, reviewId);
		System.out.println("result = " + result);

		assertEquals(mockReview.getContext(), result.getContext());
	}

	/*@Test
	void testGetReviews_Cacheable() {
		Page<ResViewReviewDto> pageMock = new PageImpl<>(List.of(ResViewReviewDto.of(mockReview)));
		when(reviewRepository.findReviews(eq(storeId), any(), any())).thenReturn(pageMock);

		List<ResViewReviewDto> result = reviewService.getReviews(storeId, new ReviewRepositorySearchConditionDto(),
			pageable);

		for (ResViewReviewDto reviewDto : result) {
			System.out.println("reviewDto = " + reviewDto);
		}

		verify(reviewRepository).findReviews(eq(storeId), any(), any());
	}*/

	@Test
	void testRegisterReview_Success() {
		ReqCreateReviewDto dto = new ReqCreateReviewDto(
			"맛있음",
			5,
			null,
			mockOrder.getId()
		);

		when(customerRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockCustomer));
		when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(mockOrder));
		when(storeRepository.findById(storeId)).thenReturn(Optional.of(mockStore));
		when(reviewRepository.save(any())).thenReturn(mockReview);

		ResResultReviewDto result = reviewService.registerReview(dto, storeId, mockUser.getId());

		assertEquals("맛있음", result.getContext());
		//verify(replyService).generateReplyAsync(any(), any());
	}

	@Test
	void testUpdateReview_Success() {
		ReqUpdateReviewDto dto = new ReqUpdateReviewDto("수정된 리뷰", 4, null);

		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
		when(customerRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockCustomer));

		ResResultReviewDto result = reviewService.updateReview(dto, reviewId, mockUser.getId());

		assertEquals(4, result.getRate());
	}

	@Test
	void testDeleteReview_Success() {
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
		when(customerRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockCustomer));
		when(replyRepository.findAllByReviewIdAndDeletedAtIsNull(reviewId)).thenReturn(List.of());

		Reply mockReply1 = Reply.builder()
			.review(mockReview)
			.context("테스트 답글 1")
			.build();
		ReflectionTestUtils.setField(mockReply1, "id", UUID.randomUUID());

		Reply mockReply2 = Reply.builder()
			.review(mockReview)
			.context("테스트 답글 2")
			.build();
		ReflectionTestUtils.setField(mockReply2, "id", UUID.randomUUID());

		when(replyRepository.findAllByReviewIdAndDeletedAtIsNull(reviewId))
			.thenReturn(List.of(mockReply1, mockReply2));
		doNothing().when(util).increaseGeneration(any(UUID.class));

		ResDeleteReviewDto result = reviewService.deleteReview(reviewId, mockUser.getId());

		assertEquals(mockReview.getId(), result.getReviewId());

		assertNotNull(mockReview.getDeletedAt());
		assertEquals(mockUser.getId(), mockReview.getDeletedBy());

		for (Reply reply : List.of(mockReply1, mockReply2)) {
			assertNotNull(reply.getDeletedAt());
			assertEquals(mockUser.getId(), reply.getDeletedBy());
		}
	}

	@Test
	void testRegisterReview_OrderNotSuccess() {
		ReflectionTestUtils.setField(mockOrder, "orderStatus", OrderStatus.ORDERED);
		ReqCreateReviewDto dto = new ReqCreateReviewDto();
		ReflectionTestUtils.setField(dto, "context", "테스트");
		ReflectionTestUtils.setField(dto, "rate", 5);
		ReflectionTestUtils.setField(dto, "orderId", mockOrder.getId());

		when(customerRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(mockCustomer));
		when(orderRepository.findById(dto.getOrderId())).thenReturn(Optional.of(mockOrder));

		assertThrows(IllegalStateException.class, () -> reviewService.registerReview(dto, storeId, mockUser.getId()));
	}

	@Test
	void testUpdateReview_Unauthorized() {
		ReqUpdateReviewDto dto = new ReqUpdateReviewDto("변경", 3, null);

		// 리뷰 작성자와 다른 customer id
		Customer otherCustomer = Customer.builder()
			.user(mockUser)
			.nickname("다른 사용자")
			.build();
		ReflectionTestUtils.setField(otherCustomer, "id", UUID.randomUUID()); // UUID로 맞춤
		when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(mockReview));
		when(customerRepository.findByUserId(mockUser.getId())).thenReturn(Optional.of(otherCustomer));

		assertThrows(UnauthorizedException.class, () -> reviewService.updateReview(dto, reviewId, mockUser.getId()));
	}
}


