package com.sparta.delivery.backend.reply.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.customer.entity.Customer;
import com.sparta.delivery.backend.global.excpetion.UnauthorizedException;
import com.sparta.delivery.backend.manager.entity.Manager;
import com.sparta.delivery.backend.manager.repository.ManagerRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.reply.dto.ReqCreateReplyDto;
import com.sparta.delivery.backend.reply.dto.ReqUpdateReplyDto;
import com.sparta.delivery.backend.reply.dto.ResDeleteReplyDto;
import com.sparta.delivery.backend.reply.dto.ResViewReplyDto;
import com.sparta.delivery.backend.reply.entity.Reply;
import com.sparta.delivery.backend.reply.repository.ReplyRepository;
import com.sparta.delivery.backend.review.entity.Review;
import com.sparta.delivery.backend.review.repository.ReviewRepository;
import com.sparta.delivery.backend.security.UserDetailsImpl;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

	@InjectMocks
	private ReplyService replyService;

	@Mock
	private ReplyRepository replyRepository;

	@Mock
	private OwnerRepository ownerRepository;

	@Mock
	private ManagerRepository managerRepository;

	@Mock
	private ReviewRepository reviewRepository;

	private User ownerUser;
	private Owner testOwner;
	private User managerUser;
	private Manager testManager;
	private Store testStore;
	private Review testReview;

	private User customerUser;
	private Customer testCustomer;

	private Long testOwnerId = 1L;
	private Long testManagerId = 2L;
	private Long testCustomerId = 3L;

	@BeforeEach
	void setUp() {
		// ===== 1️⃣ Owner User & Owner =====
		ownerUser = User.builder()
			.username("testOwner")
			.password("testPassword")
			.role(UserRoleEnum.OWNER)
			.build();
		ReflectionTestUtils.setField(ownerUser, "id", testOwnerId);
		ReflectionTestUtils.setField(ownerUser, "publicId", UUID.randomUUID());

		testOwner = Owner.builder()
			.user(ownerUser)
			.nickname("테스트닉네임")
			.email("owner@test.com")
			.phoneNumber("010-1111-2222")
			.build();
		ReflectionTestUtils.setField(testOwner, "id", UUID.randomUUID());

		// ===== 2️⃣ Manager User & Manager =====
		managerUser = User.builder()
			.username("testManager")
			.password("testPassword")
			.role(UserRoleEnum.MANAGER)
			.build();
		ReflectionTestUtils.setField(managerUser, "id", testManagerId);
		ReflectionTestUtils.setField(managerUser, "publicId", UUID.randomUUID());

		testManager = Manager.builder()
			.user(managerUser)
			.name("매니저닉네임")
			.email("manager@test.com")
			.phoneNumber("010-2222-3333")
			.build();
		ReflectionTestUtils.setField(testManager, "id", UUID.randomUUID());

		// ===== 3️⃣ Customer User & Customer =====
		customerUser = User.builder()
			.username("testCustomer")
			.password("testPassword")
			.role(UserRoleEnum.CUSTOMER)
			.build();
		ReflectionTestUtils.setField(customerUser, "id", testCustomerId);
		ReflectionTestUtils.setField(customerUser, "publicId", UUID.randomUUID());

		testCustomer = Customer.builder()
			.user(customerUser)
			.nickname("테스트고객")
			.email("customer@test.com")
			.phoneNumber("010-3333-4444")
			.build();
		ReflectionTestUtils.setField(testCustomer, "id", UUID.randomUUID());

		// ===== 4️⃣ Store =====
		testStore = Store.builder()
			.owner(testOwner)
			.name("테스트가게")
			.address(Address.builder().dong(Dong.builder().code("123").build()).fullAddress("강남구").build())
			.reviewRate(0.0)
			.minOrderPrice(15000)
			.deliveryFee(3000)
			.status(StoreStatusEnum.OPEN)
			.phoneNumber("010-5555-6666")
			.build();
		ReflectionTestUtils.setField(testStore, "id", UUID.randomUUID());

		// ===== 5️⃣ Review =====
		testReview = Review.builder()
			.customer(testCustomer)
			.store(testStore)
			.context("리뷰 내용")
			.rate(5)
			.build();
		ReflectionTestUtils.setField(testReview, "id", UUID.randomUUID());

		// ===== 6️⃣ SecurityContext (CUSTOMER로 로그인된 상태) =====
		UserDetailsImpl userDetails = new UserDetailsImpl(customerUser);
		Authentication auth = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
	}

	// ==================== 답글 등록 ====================
	@Test
	void testCreateReply_manager() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto("매니저 답글 내용");

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());
		when(replyRepository.save(any(Reply.class))).thenAnswer(invocation -> {
			Reply r = invocation.getArgument(0);
			ReflectionTestUtils.setField(r, "id", UUID.randomUUID());
			return r;
		});

		ResViewReplyDto result = replyService.createReply(dto, testReview.getId(), testManagerId);
		System.out.println("result = " + result);

		assertNotNull(result);
		assertEquals(dto.getContext(), result.getContext());
		assertEquals("매니저닉네임", result.getWriterName());
	}

	@Test
	void testCreateReply_notStoreOwner_throwsException() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto("권한 없는 답글");

		// 리뷰의 가게 Owner는 다른 Owner
		Owner otherOwner = Owner.builder()
			.user(User.builder().username("otherOwner").password("pw").role(UserRoleEnum.OWNER).build())
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.build();
		ReflectionTestUtils.setField(otherOwner.getUser(), "id", 999L);
		ReflectionTestUtils.setField(otherOwner, "id", UUID.randomUUID());

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(ownerRepository.findByUserId(testOwnerId)).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(testOwnerId)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.createReply(dto, testReview.getId(), testOwnerId));

		assertEquals("해당 가게의 점주만 리뷰 답글을 등록할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testCreateReply_customerCannotRegister_throwsException() {
		ReqCreateReplyDto dto = new ReqCreateReplyDto("고객 답글");

		when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.of(testReview));
		when(ownerRepository.findByUserId(testCustomerId)).thenReturn(Optional.empty());
		when(managerRepository.findByUserId(testCustomerId)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.createReply(dto, testReview.getId(), testCustomerId));

		assertEquals("리뷰 답글을 등록할 권한이 없습니다.", exception.getMessage());
	}

	// ==================== 답글 수정 ====================
	@Test
	void testUpdateReply_manager() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto("매니저 수정 답글");

		Reply reply = Reply.builder()
			.context("원본 답글")
			.manager(testManager)
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());

		ResViewReplyDto result = replyService.updateReply(dto, replyId, testManagerId);

		assertEquals("매니저 수정 답글", result.getContext());
		assertEquals("매니저 수정 답글", reply.getContext());
	}

	@Test
	void testUpdateReply_notStoreOwner_throwsException() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto("권한 없는 수정");

		Owner storeOwner = testOwner;

		Owner otherOwner = Owner.builder()
			.user(User.builder().username("otherOwner").password("pw").role(UserRoleEnum.OWNER).build())
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.build();
		ReflectionTestUtils.setField(otherOwner.getUser(), "id", 999L);
		ReflectionTestUtils.setField(otherOwner, "id", UUID.randomUUID());

		Reply reply = Reply.builder()
			.context("원본 답글")
			.owner(storeOwner)
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(999L)).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(999L)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.updateReply(dto, replyId, 999L));

		assertEquals("해당 가게의 점주만 리뷰 답글을 수정할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testUpdateReply_customerCannotUpdate_throwsException() {
		UUID replyId = UUID.randomUUID();
		ReqUpdateReplyDto dto = new ReqUpdateReplyDto("고객 수정");

		Reply reply = Reply.builder()
			.context("원본 답글")
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.updateReply(dto, replyId, testCustomerId));

		assertEquals("답글을 수정할 권한이 없습니다.", exception.getMessage());
	}

	// ==================== 답글 삭제 ====================
	@Test
	void testDeleteReply_manager() {
		UUID replyId = UUID.randomUUID();

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.manager(testManager)
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(managerRepository.findByUserId(testManagerId)).thenReturn(Optional.of(testManager));
		when(ownerRepository.findByUserId(testManagerId)).thenReturn(Optional.empty());

		ResDeleteReplyDto dto = replyService.deleteReply(replyId, testManagerId);

		assertNotNull(dto);
	}

	@Test
	void testDeleteReply_notStoreOwner_throwsException() {
		UUID replyId = UUID.randomUUID();

		Owner storeOwner = testOwner;

		Owner otherOwner = Owner.builder()
			.user(User.builder().username("otherOwner").password("pw").role(UserRoleEnum.OWNER).build())
			.nickname("다른점주")
			.email("other@test.com")
			.phoneNumber("010-9999-8888")
			.build();
		ReflectionTestUtils.setField(otherOwner.getUser(), "id", 999L);
		ReflectionTestUtils.setField(otherOwner, "id", UUID.randomUUID());

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.owner(storeOwner)
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));
		when(ownerRepository.findByUserId(999L)).thenReturn(Optional.of(otherOwner));
		when(managerRepository.findByUserId(999L)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.deleteReply(replyId, 999L));

		assertEquals("해당 가게의 점주만 리뷰 답글을 삭제할 수 있습니다.", exception.getMessage());
	}

	@Test
	void testDeleteReply_customerCannotDelete_throwsException() {
		UUID replyId = UUID.randomUUID();

		Reply reply = Reply.builder()
			.context("삭제할 답글")
			.review(testReview)
			.build();
		ReflectionTestUtils.setField(reply, "id", replyId);

		when(replyRepository.findById(replyId)).thenReturn(Optional.of(reply));

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
			() -> replyService.deleteReply(replyId, testCustomerId));

		assertEquals("답글을 삭제할 권한이 없습니다.", exception.getMessage());
	}
}