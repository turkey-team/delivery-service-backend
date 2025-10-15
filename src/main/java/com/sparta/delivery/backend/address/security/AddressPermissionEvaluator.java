// package com.sparta.delivery.backend.address.security;
//
// import java.util.UUID;
//
// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.sparta.delivery.backend.address.repository.AddressRepository;
// import com.sparta.delivery.backend.security.UserDetailsImpl;
//
// import lombok.RequiredArgsConstructor;
//
// @RequiredArgsConstructor
// @Transactional(readOnly = true)
// @Component("addressPermissionEvaluator")
// public class AddressPermissionEvaluator {
//
// 	private final AddressRepository addressRepository;
//
// 	public boolean isOwner(UUID addressId, UserDetailsImpl user) {
// 		return addressRepository.findById(addressId)
// 			.map(address -> address.getUser().getId().equals(user.getId()))
// 			.orElse(false);
// 	}
//
// }
