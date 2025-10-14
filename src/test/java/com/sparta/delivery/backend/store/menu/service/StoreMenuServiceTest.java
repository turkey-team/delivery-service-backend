package com.sparta.delivery.backend.store.menu.service;

import com.sparta.delivery.backend.common.LoginUserAuditorAware;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.menu.dto.*;
import com.sparta.delivery.backend.store.menu.entity.StoreMenu;
import com.sparta.delivery.backend.store.menu.enums.StockStatus;
import com.sparta.delivery.backend.store.menu.repository.StoreMenuRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreMenuServiceTest {

    @Mock
    private StoreMenuRepository storeMenuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private LoginUserAuditorAware loginUserAuditorAware;

    @InjectMocks
    private StoreMenuService storeMenuService;

    private User user;
    private Owner owner;
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

        owner = Owner.builder()
                .nickname("Owner1234")
                .email("testOwner1@naver.com")
                .phoneNumber("010-2222-3333")
                .user(user)
                .build();

        // 테스트 가게
        store = Store.builder()
                .owner(owner) // 더미 Owner
                .name("테스트용 햄버거 가게")
                .addressDetails("고양시 덕양구 화정동 백양로 65")
                .reviewRate(0.0)
                .minOrderPrice(13000)
                .deliveryFee(1500)
                .regionDong(null) // 추후 필요하면 더미 Dong 생성
                .status(StoreStatusEnum.OPEN)
                .phoneNumber("010-1234-5678")
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
            when(storeMenuRepository.findMaxSortOrderByStore(storeId)).thenReturn(null);
            // storeMenuRepository.save
            when(storeMenuRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            /* when */
            storeMenuService.createStoreMenu(storeId, reqCreateStoreMenuDto);

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
                    () -> storeMenuService.createStoreMenu(storeId, reqCreateStoreMenuDto)
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
                    () -> storeMenuService.createStoreMenu(storeId, reqCreateStoreMenuDto)
            );

            assertEquals("Menu name already exists", exception.getMessage());
        }

        @Test
        @DisplayName("실패 - 가격을 입력하지 않은 경우")
        void failure_priceNull() {
            UUID storeId = store.getId();
            reqCreateStoreMenuDto.setPrice(0); // 가격을 입력하지 않은 경우

            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> storeMenuService.createStoreMenu(storeId, reqCreateStoreMenuDto)
            );

            assertEquals("Price must exists", exception.getMessage());
        }
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
        @DisplayName("성공")
        void getList_success() {
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
                    .image(image)
                    .build();
            ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID()); // Test UUID 주입

            List<StoreMenu> menuList = List.of(menu1, menu2);
            Page<StoreMenu> pageResult = new org.springframework.data.domain.PageImpl<>(menuList, pageable, menuList.size());

            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(storeMenuRepository.findAllByStoreIdAndDeletedAtIsNull(storeId, pageable))
                    .thenReturn(pageResult);

            /* when */
            Page<ResGetListStoreMenuDto> resGetListStoreMenuDto = storeMenuService.getStoreMenusByStoreId(storeId, 0, 10);

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

            /* when */
            when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

            /* then */
            assertThrows(
                    IllegalArgumentException.class,
                    () -> storeMenuService.getStoreMenusByStoreId(storeId, page, size)
            );
        }
    }


    // Put
    @Nested
    @DisplayName("가게 메뉴 수정 테스트")
    class UpdateStoreMenuTest {

        @Test
        @DisplayName("성공")
        void success() {
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
            storeMenuService.updateStoreMenu(storeId, menuId, reqUpdateStoreMenuDto);

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
                    () -> storeMenuService.updateStoreMenu(storeId, menuId, updateDto)
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
                    () -> storeMenuService.updateStoreMenu(storeId, menuId, updateDto)
            );

            assertEquals("StoreMenu not found", exception.getMessage());
        }
    }

    // Sort_order
    @Nested
    @DisplayName("가게 메뉴 순서 변경 테스트")
    class UpdateStoreMenuSortOrderTest {

        @Test
        @DisplayName("성공")
        void success() {
            /* given */
            UUID storeId = store.getId();

            menu2 = StoreMenu.builder()
                    .reqCreateStoreMenuDto(reqCreateStoreMenuDto)
                    .store(store)
                    .image(image)
                    .build();
            ReflectionTestUtils.setField(menu2, "id", UUID.randomUUID());
            UUID menuId = menu2.getId();
            ReqUpdateSortOrderDto reqUpdateSortOrderDto = new ReqUpdateSortOrderDto();
            reqUpdateSortOrderDto.setSortOrder(1); // 이동하고 싶은 위치

            List<StoreMenu> menusToShift = List.of(menu2);
            when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
            when(storeMenuRepository.findByStoreIdAndIdAndDeletedAtIsNull(storeId, menuId, null))
                    .thenReturn(Optional.of(menu2));
            when(storeMenuRepository.findAllByStoreIdAndSortOrderGreaterThanEqualAndDeletedAtIsNull(storeId, 1))
                    .thenReturn(menusToShift);

            /* when */
            storeMenuService.updateSortOrder(storeId, menuId, reqUpdateSortOrderDto);

            /* then */
            // 타겟 메뉴 순서 변경 확인
            assertEquals(1, menu1.getSortOrder());

            // 나머지 메뉴 순서 재정렬 확인
            assertEquals(2, menusToShift.get(0).getSortOrder());

            verify(storeMenuRepository, times(2))
                    .flush(); // DB flush 호출 검증
        }

        @Test
        @DisplayName("실패")
        void failure() {
            /* given */

            /* when */

            /* then */

        }
    }

//    // Visibility
//    @Nested
//    @DisplayName("가게 메뉴 숨기기 테스트")
//    class PatchStoreMenuIsHiddenTest {
//
//        @Test
//        @DisplayName("성공")
//        void success() {
//            /* given */
//
//            /* when */
//
//            /* then */
//
//        }
//
//        @Test
//        @DisplayName("실패")
//        void failure() {
//            /* given */
//
//            /* when */
//
//            /* then */
//
//        }
//    }
//
//    // Delete
//    @Nested
//    @DisplayName("가게 메뉴 삭제 테스트")
//    class DeleteStoreMenuTest {
//
//        @Test
//        @DisplayName("성공")
//        void success() {
//            /* given */
//
//            /* when */
//
//            /* then */
//
//        }
//
//        @Test
//        @DisplayName("실패")
//        void failure() {
//            /* given */
//
//            /* when */
//
//            /* then */
//
//        }
//    }
}
