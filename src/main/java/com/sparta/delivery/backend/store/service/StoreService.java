package com.sparta.delivery.backend.store.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sparta.delivery.backend.address.entity.Address;
import com.sparta.delivery.backend.category.entity.Category;
import com.sparta.delivery.backend.category.repository.CategoryRepository;
import com.sparta.delivery.backend.global.common.dto.PageResponse;
import com.sparta.delivery.backend.image.entity.Image;
import com.sparta.delivery.backend.image.repository.ImageRepository;
import com.sparta.delivery.backend.owner.entity.Owner;
import com.sparta.delivery.backend.owner.repository.OwnerRepository;
import com.sparta.delivery.backend.region.entity.Dong;
import com.sparta.delivery.backend.region.repository.DongRepository;
import com.sparta.delivery.backend.store.dto.ReqCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ReqDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ReqUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.dto.ResCreateStoreDto;
import com.sparta.delivery.backend.store.dto.ResDeleteStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetListStoreDto;
import com.sparta.delivery.backend.store.dto.ResGetStoreDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreDetailsDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreInfoDto;
import com.sparta.delivery.backend.store.dto.ResUpdateStoreStatusDto;
import com.sparta.delivery.backend.store.entity.Store;
import com.sparta.delivery.backend.store.entity.StoreCategory;
import com.sparta.delivery.backend.store.entity.StoreDetails;
import com.sparta.delivery.backend.store.entity.StoreImage;
import com.sparta.delivery.backend.store.entity.StoreImageStatusEnum;
import com.sparta.delivery.backend.store.entity.StoreStatusEnum;
import com.sparta.delivery.backend.store.repository.StoreCategoryRepository;
import com.sparta.delivery.backend.store.repository.StoreDetailsRepository;
import com.sparta.delivery.backend.store.repository.StoreImageRepository;
import com.sparta.delivery.backend.store.repository.StoreRepository;
import com.sparta.delivery.backend.user.entity.User;
import com.sparta.delivery.backend.user.entity.UserRoleEnum;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreService {

	private final StoreRepository storeRepository;
	private final OwnerRepository ownerRepository;
	private final ImageRepository imageRepository;
	private final CategoryRepository categoryRepository;
	private final DongRepository dongRepository;
	private final StoreDetailsRepository storeDetailsRepository;
	private final StoreCategoryRepository storeCategoryRepository;
	private final StoreImageRepository storeImageRepository;

	private static final int WGS84_SRID = 4326;
	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

	@Transactional
	public ResCreateStoreDto createStore(ReqCreateStoreDto requestDto , User user) {

		checkUserIsActive(user);

		//Owner 조회
		Owner owner = ownerRepository.findByUserIdAndDeletedAtIsNull(user.getId()).orElse(null);

		if (owner == null){
			if (user.getRole() == UserRoleEnum.MANAGER){
				owner = ownerRepository.findByIdAndDeletedAtIsNull(requestDto.getOwnerId())
					.orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OwnerId가 적절하지 않습니다."));
			}else{
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "점주가 아니면 가게를 생성할 수 없습니다.");
			}
		}

		// Region Dong 조회
		Dong dong = dongRepository.findByCode(requestDto.getRegionCode()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 주소지입니다."));

		// 배달가능지역 설정
		List<Dong> findDongs = dongRepository.findAllByCodeIn(requestDto.getDeliveryRegions());
		MultiPolygon deliveryZone = createMultiPolygon(findDongs);

		// 카테고리 조회

		//Category
		List<Category> categories = categoryRepository.findAllById(requestDto.getCategories());

		// 2. 이미지 등록
		List<String> storeImageUrls = requestDto.getImages().stream()
			.filter(img -> "store".equalsIgnoreCase(img.getType()))
			.map(ReqCreateStoreDto.ImageDto::getUrl)
			.collect(Collectors.toList());

		List<String> businessImageUrls = requestDto.getImages().stream()
			.filter(img -> "business".equalsIgnoreCase(img.getType()))
			.map(ReqCreateStoreDto.ImageDto::getUrl)
			.collect(Collectors.toList());

		// 2-1.image table insert
		try {
			List<Image> storeImages = saveImages(storeImageUrls);
			List<Image> businessImage = saveImages(businessImageUrls);

		// 3. store insert

		// store 생성
		Store store = Store.builder()
			.owner(owner)
			.name(requestDto.getName())
			.address(Address.builder()
				.dong(dong)
				.fullAddress(requestDto.getAddressDetail())
				.location(createPoint(requestDto.getLongitude(), requestDto.getLatitude()))
				.build())
			.deliveryZone(deliveryZone)
			.deliveryFee(requestDto.getDeliveryFee())
			.minOrderPrice(requestDto.getMinOrderPrice())
			.reviewRate(5.0)
			.phoneNumber(requestDto.getPhoneNumber())
			.status(StoreStatusEnum.CLOSED)
			.build();

		// save
		storeRepository.save(store);

		// 3-2 카테고리 중간 테이블에 저장
		saveStoreCategories(categories, store);

		// 3-2. 이미지 중간 테이블에 저장
		//매장

		saveStoreImages(storeImages, store, StoreImageStatusEnum.STORE);
		saveStoreImages(businessImage, store, StoreImageStatusEnum.BUSINESS);


		// 4. store_details insert
		StoreDetails storeDetails = StoreDetails.builder().store(store)
			.operationHours(requestDto.getOperatingHours())
			.businessNumber(requestDto.getBusinessNumber())
			.holiday(requestDto.getHoliday())
			.description(requestDto.getDescription()).build();

		storeDetailsRepository.save(storeDetails);


		return new ResCreateStoreDto(store.getId(), store.getName());

		}catch (DataIntegrityViolationException e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용중인 이미지 URL이 포함되어있습니다.");
		}
	}

	public ResGetStoreDto getStore(UUID storeId) {

		Store store = storeRepository.findById(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"해당 가게가 존재하지 않습니다"));
		StoreDetails details = storeDetailsRepository.findByStoreId(storeId).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"해당 가게가 존재하지 않습니다"));

		checkStoreIsActive(store, details);

		StoreImage storeImage = storeImageRepository.findFirstByStoreIdAndStatusAndDeletedAtIsNullOrderByCreatedAtAsc(storeId, StoreImageStatusEnum.STORE).orElse(null);
		String imgUrl;

		if (storeImage != null && storeImage.getImage().getImageUrl() != null){
			imgUrl = storeImage.getImage().getImageUrl();
		}else{
			imgUrl = "";
		}

		ResGetStoreDto resGetStoreDto = ResGetStoreDto.builder()
			.storeid(store.getId())
			.name(store.getName())
			.reviewRate(store.getReviewRate())
			.reviewCnt(store.getReviewCnt())
			.status(store.getStatus())
			.deliveryFee(store.getDeliveryFee())
			.minOrderPrice(store.getMinOrderPrice())
			.description(details.getDescription())
			.holiday(details.getHoliday())
			.operationHours(details.getOperationHours())
			.imageUrl(imgUrl)
			.build();

		return resGetStoreDto;
	}


	@Transactional
	public ResUpdateStoreInfoDto updateStoreInfo(UUID storeId, ReqUpdateStoreInfoDto requestDto, User user) {

		// 탈퇴회원 검증
		checkUserIsActive(user);

		// Store 검증
		Store store = storeRepository.findById(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));
		StoreDetails storeDetails = storeDetailsRepository.findByStoreId(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));

		checkStoreIsActive(store, storeDetails);

		// 소유주 검증
		if (user.getRole().equals(UserRoleEnum.OWNER)){
			checkUserIsStoreOwner(user,store);
		}

		Dong dong = dongRepository.findByCode(requestDto.getRegionDong()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 주소지입니다."));
		Address address = Address.builder().dong(dong).fullAddress(requestDto.getAddressDetails()).build();

		// 현재 등록된 카테고리 조회
		List<StoreCategory> currentCategories = store.getStoreCategories();

		Set<UUID> existingCategoryIds = currentCategories.stream()
			.filter(sc -> !sc.isDeleted())  // 삭제되지 않은 카테고리만
			.filter(sc -> sc.getCategory() != null)
			.map(sc -> sc.getCategory().getId())
			.collect(Collectors.toSet());

		// 수정할 카테고리 조회
		Set<UUID> reqCategoryIds = requestDto.getCategories().stream().collect(Collectors.toSet());

		// 수정이 필요하면
		if (!reqCategoryIds.isEmpty()){

			List<StoreCategory> categoryToDeleteList = currentCategories.stream().filter(sc-> !reqCategoryIds.contains(sc.getCategory().getId()) && !sc.isDeleted()).collect(Collectors.toList());

			for(StoreCategory sc : categoryToDeleteList){
				// 삭제대상
				sc.softDelete(user.getId());
				//연관관계제거
				sc.getStore().getStoreCategories().remove(sc);
				sc.getCategory().getStoreCategories().remove(sc);
				storeCategoryRepository.save(sc);
			}

			// 요청 카테고리 저장
			Set<UUID> newCategoriesSet = new HashSet<>(reqCategoryIds);

			// 기존 카테고리를 필터링
			newCategoriesSet.removeAll(existingCategoryIds);

			// DB에서 찾기
			List<Category> newCategoryList = categoryRepository.findAllById(newCategoriesSet);

			List<StoreCategory> newStoreCategoryList = new ArrayList<>();
			for(Category c : newCategoryList){
				// 생성
				StoreCategory sc = StoreCategory.builder().store(store).category(c).build();

				// 연관관계 설정
				store.getStoreCategories().add(sc);
				c.getStoreCategories().add(sc);

				newStoreCategoryList.add(sc);
			}

			//저장
			storeCategoryRepository.saveAll(newStoreCategoryList);
		}

		// 기존 Image 중 삭제되지않은 store 타입만 조회
		List<StoreImage> currentImages = store.getStoreImages().stream().filter(si -> si.getStatus()==StoreImageStatusEnum.STORE && !si.isDeleted()).collect(Collectors.toList());

		// requestDto에서 가게 이미지만 추출
		List<ReqUpdateStoreInfoDto.ImageDto> reqImages = requestDto.getImages().stream().filter(img -> "store".equalsIgnoreCase(img.getType())).toList();

		// imageId가 있음 -> 기존 이미지
		List<ReqUpdateStoreInfoDto.ImageDto> existingImages = reqImages.stream().filter(img -> img.getImageId() != null).toList();
		// imageId가 없음 -> 신규 이미지
		List<ReqUpdateStoreInfoDto.ImageDto> newImages = reqImages.stream().filter(img -> img.getImageId() == null).toList();

		// dto 중 id 있는 이미지들을 DB에 imageId로 조회
		Map<UUID, Image> existingImageMap = imageRepository.findAllById(
			existingImages.stream()
				.map(ReqUpdateStoreInfoDto.ImageDto::getImageId)
				.toList()
		).stream().collect(Collectors.toMap(Image::getId, img -> img));

		Map<String, Image> newImageMap = new HashMap<>();
		// URL로 검색해서 있으면 Image로 받거나 아님 Image로 신규 저장
		for (ReqUpdateStoreInfoDto.ImageDto dto : newImages) {
			Image image = imageRepository.findByImageUrl(dto.getUrl())
				.orElseGet(() -> {
					// 2. 존재하지 않으면 신규 저장 시도
					try {
						return imageRepository.save(Image.builder().imageUrl(dto.getUrl()).build());
					} catch (DataIntegrityViolationException e) {
						throw new ResponseStatusException(
							HttpStatus.BAD_REQUEST,
							"이미지 URL이 중복되어 등록할 수 없습니다. URL: " + dto.getUrl()
						);
					}
				});

			newImageMap.put(dto.getUrl(), image);
		}

		Set<UUID> requestedImageIds = reqImages.stream()
			.map(img -> {
				if (img.getImageId() != null) return img.getImageId();
				else return newImageMap.get(img.getUrl()).getId();
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		// 삭제 대상 이미지 리스트 만들기
		List<StoreImage> imageToDeleteList = currentImages.stream()
			.filter(currentImage -> !requestedImageIds.contains(currentImage.getImage().getId()))
			.toList();

		for (StoreImage currentImage : imageToDeleteList) {
			// 삭제대상
			//Image 연관관계 제거 및 삭제
			currentImage.softDelete(user.getId());
			currentImage.getImage().softDelete(user.getId());
			imageRepository.save(currentImage.getImage());

			// Store 연관관계 제거
			currentImage.getImage().getStoreImages().remove(currentImage);
			store.getStoreImages().remove(currentImage);
		}

		// 추가대상
		Set<UUID> currentImageIds = currentImages.stream()
			.map(si -> si.getImage().getId())
			.collect(Collectors.toSet());

		Set<UUID> imagesToAddSet = new HashSet<>(requestedImageIds);
		imagesToAddSet.removeAll(currentImageIds);

		List<StoreImage> toAddStoreImages = new ArrayList<>();

		for (UUID imageId : imagesToAddSet) {
			Image image = existingImageMap.getOrDefault(
				imageId,
				newImageMap.values().stream()
					.filter(img -> img.getId().equals(imageId))
					.findFirst()
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,"요청된 이미지가 존재하지 않습니다."))
			);

			StoreImage newStoreImage = StoreImage.builder()
				.store(store)
				.image(image)
				.status(StoreImageStatusEnum.STORE)
				.build();

			store.getStoreImages().add(newStoreImage);
			image.getStoreImages().add(newStoreImage);
			toAddStoreImages.add(newStoreImage);
		}

		storeImageRepository.saveAll(toAddStoreImages);

		// Update
		store.updateStoreInfo(requestDto, address);
		storeRepository.save(store);

		ResUpdateStoreInfoDto resUpdateStoreInfoDto = ResUpdateStoreInfoDto.builder()
			.storeId(store.getId())
			.storeName(store.getName())
			.fullAddress(store.getAddress().getFullAddress())
			.phoneNumber(store.getPhoneNumber())
			.build();

		return resUpdateStoreInfoDto;
	}

	@Transactional
	public ResUpdateStoreDetailsDto updateStoreDetails(UUID storeId, @Valid ReqUpdateStoreDetailsDto requestDto, User user) {

		checkUserIsActive(user);

		// Store 검증
		Store store = storeRepository.findById(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));
		StoreDetails storeDetails = storeDetailsRepository.findByStoreId(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));

		// 소유주 검증
		if (user.getRole().equals(UserRoleEnum.OWNER)){
			checkUserIsStoreOwner(user,store);
		}

		checkStoreIsActive(store, storeDetails);

		// 검증 완료

		// Update
		store.updateStoreDetails(requestDto.getDeliveryFee(), requestDto.getMinOrderPrice());
		storeRepository.save(store);

		storeDetails.updateStoreDetails(requestDto.getDescription(), requestDto.getHoliday(), requestDto.getOperationHours());
		storeDetailsRepository.save(storeDetails);

		// 반환
		ResUpdateStoreDetailsDto resUpdateStoreDetailsDto = ResUpdateStoreDetailsDto.builder()
			.storeId(store.getId())
			.storeName(store.getName())
			.deliveryFee(store.getDeliveryFee())
			.minOrderPrice(store.getMinOrderPrice())
			.description(storeDetails.getDescription())
			.operationHours(storeDetails.getOperationHours())
			.holiday(storeDetails.getHoliday())
			.build();

		return resUpdateStoreDetailsDto;
	}

	@Transactional(readOnly = true)
	public PageResponse<ResGetListStoreDto> getStores(int page, int size, String sort, String keyword, String categoryId, User user) {

		// 허용 사이즈
		List<Integer> sizeList = List.of(10,30,50);
		if (!sizeList.contains(size)) {
			size = 10;
		}

		//음수일경우 0 강제
		page = Math.max(page-1, 0);

		// 전체 조회시 cateogryId 없음
		UUID categoryUUID = null;

		if(categoryId != null && !categoryId.isBlank()){
			try {
				categoryUUID = UUID.fromString(categoryId);
			}
			catch (IllegalArgumentException e){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"올바른 카테고리가 아닙니다.");
			}
		}

		Pageable pageable = PageRequest.of(page, size);

		Page<ResGetListStoreDto> dtoList = storeRepository.getStores(pageable, sort, keyword, categoryUUID);

		return PageResponse.of(dtoList);

	}

	/**
	 *
	 * @param storeId 수정할 가게
	 * @param requestDto 수정할 상태(OPEN, CLOSED, READY)
	 * @param user 로그인한 유저
	 * @return
	 */
	@Transactional
	public ResUpdateStoreStatusDto updateStoreStatus(UUID storeId, @Valid ReqUpdateStoreStatusDto requestDto, User user) {

		checkUserIsActive(user);

		// Store 검증
		Store store = storeRepository.findById(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));
		if (store.getDeletedAt() != null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이미 삭제된 가게입니다");
		}

		// 소유주 검증
		if (user.getRole().equals(UserRoleEnum.OWNER)){
			checkUserIsStoreOwner(user,store);
		}

		// 현재 Status와 비교
		StoreStatusEnum currentStatus = store.getStatus();
		// 동일하면 Exception
		if (currentStatus == requestDto.getStoreStatus()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"현재와 동일한 상태로는 변경할 수 없습니다.");
		}

		store.updateStoreStatus(requestDto.getStoreStatus());
		storeRepository.save(store);

		ResUpdateStoreStatusDto resDto = ResUpdateStoreStatusDto.builder().storeId(store.getId()).storeStatus(store.getStatus()).build();
		return resDto;

	}

	/**
	 *
	 * @param storeId 삭제할 가게ID
	 * @param requestDto 사업자번호
	 * @param user 로그인유저
	 * @return
	 */
	@Transactional
	public ResDeleteStoreDto deleteStore(UUID storeId, @Valid ReqDeleteStoreDto requestDto, User user) {

		checkUserIsActive(user);

		Store store = storeRepository.findById(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));
		StoreDetails storeDetails = storeDetailsRepository.findByStoreId(storeId).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST,"존재하지 않는 가게입니다."));

		checkStoreIsActive(store,storeDetails);

		// 소유주 검증
		if (user.getRole().equals(UserRoleEnum.OWNER)){
			checkUserIsStoreOwner(user,store);
		}

		// 사업자번호 동일한지 검증
		boolean isRightBN = requestDto.getBusinessNumber().equals(storeDetails.getBusinessNumber());

		if (!isRightBN){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"잘못된 사업자등록번호입니다. 정확한 사업자번호를 입력하세요.");
		}

		if (!store.isDeleted() && !storeDetails.isDeleted()){
			store.delete(user.getId());
		}else{
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"이미 삭제된 가게입니다.");
		}

		storeRepository.save(store);
		storeDetailsRepository.save(storeDetails);

		ResDeleteStoreDto resDeleteStoreDto = ResDeleteStoreDto.builder().storeName(store.getName()).storeId(store.getId()).businessNumber(storeDetails.getBusinessNumber()).build();

		return resDeleteStoreDto;
	}

	/**
	 * Image 테이블 저장
	 * @param urls 이미지URL
	 * @return
	 */
	public List<Image> saveImages(List<String> urls) {

		if (urls == null || urls.isEmpty()) return Collections.emptyList();

		List<Image> images = new ArrayList<>();

		for (String url : urls) {
			Image image = Image.builder().imageUrl(url).build();
			imageRepository.save(image);
			images.add(image);
		}

		return images;
	}

	/**
	 * StoreImage 테이블 저장
	 * @param images 저장할 이미지
	 * @param store 가게
	 * @param status 이미지 타입 : STORE, BUSINESS
	 */
	public void saveStoreImages(List<Image> images, Store store, StoreImageStatusEnum status) {

		for(Image image : images){
			StoreImage storeImage = store.addImage(store, image, status);
			image.getStoreImages().add(storeImage);
			store.getStoreImages().add(storeImage);
			storeImageRepository.save(storeImage);
		}

	}

	/**
	 * StoreCategory 저장
	 * @param newCategories 신규 추가 카테고리
	 * @param store 가게
	 */
	public void saveStoreCategories(List<Category> newCategories, Store store) {
		List<StoreCategory> storeCategories = new ArrayList<>();
		for(Category c : newCategories){
			//기존 카테고리와 동일한지 확인
			boolean isEqual = store.getStoreCategories().stream().anyMatch(currentCategory->currentCategory.getId().equals(c.getId()));

			if(!isEqual){
				StoreCategory newStoreCategory = StoreCategory.builder().store(store).category(c).build();
				store.getStoreCategories().add(newStoreCategory);
				c.getStoreCategories().add(newStoreCategory);
				storeCategories.add(newStoreCategory);
			}
		}
		storeCategoryRepository.saveAll(storeCategories);
	}


	private void checkStoreIsActive(Store store, StoreDetails details) {
		if (store.getDeletedAt() != null || details.getDeletedAt() != null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 삭제된 가게입니다.");
		}
	}

	public void checkUserIsActive(User user) {

		if (user.getDeletedAt() != null){
			throw new AccessDeniedException("탈퇴한 회원입니다.");
		}

	}

	public void checkUserIsStoreOwner(User user, Store store) {

		boolean isStoreOwner = store.getOwner().getUser().getId().equals(user.getId());

		if (!isStoreOwner){
			throw new AccessDeniedException("가게의 소유주가 아닙니다.");
		}

	}

	// 경도, 위도 좌표로 Point 객체 생성 (SRID 4326 사용)
	private Point createPoint(Double longitude, Double latitude) {
		return GEOMETRY_FACTORY.createPoint(new Coordinate(longitude, latitude));
	}

	private MultiPolygon createMultiPolygon(List<Dong> dongs) {
		Polygon[] polygons = dongs.stream()
			.map(Dong::getPolygon)
			.toArray(Polygon[]::new);

		return GEOMETRY_FACTORY.createMultiPolygon(polygons);
	}

}

