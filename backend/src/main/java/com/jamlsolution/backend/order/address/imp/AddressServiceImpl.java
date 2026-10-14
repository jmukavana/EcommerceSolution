package com.jamlsolution.backend.order.address.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.order.address.Address;
import com.jamlsolution.backend.order.address.AddressRepository;
import com.jamlsolution.backend.order.address.AddressService;
import com.jamlsolution.backend.order.address.request.CreateAddressRequest;
import com.jamlsolution.backend.order.address.response.AddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    public AddressResponse create(CreateAddressRequest request) {
        User user = currentUser();
        if (request.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(user.getId())
                    .ifPresent(a -> { a.setDefault(false); addressRepository.save(a); });
        }
        boolean firstAddress = addressRepository.findByUserId(user.getId()).isEmpty();
        Address address = Address.builder()
                .user(user)
                .label(request.label())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .line1(request.line1())
                .line2(request.line2())
                .city(request.city())
                .state(request.state())
                .postalCode(request.postalCode())
                .country(request.country())
                .isDefault(request.isDefault() || firstAddress)
                .build();
        return AddressResponse.from(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> findMyAddresses() {
        return addressRepository.findByUserId(currentUser().getId())
                .stream().map(AddressResponse::from).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse findById(String id) {
        return AddressResponse.from(getOrThrow(id));
    }

    @Override
    public AddressResponse update(String id, CreateAddressRequest request) {
        Address address = getOrThrow(id);
        if (request.isDefault() && !address.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(address.getUser().getId())
                    .ifPresent(a -> { a.setDefault(false); addressRepository.save(a); });
        }
        address.setLabel(request.label());
        address.setFirstName(request.firstName());
        address.setLastName(request.lastName());
        address.setPhone(request.phone());
        address.setLine1(request.line1());
        address.setLine2(request.line2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setDefault(request.isDefault());
        return AddressResponse.from(addressRepository.save(address));
    }

    @Override
    public void delete(String id) {
        addressRepository.delete(getOrThrow(id));
    }

    @Override
    public AddressResponse setDefault(String id) {
        Address address = getOrThrow(id);
        addressRepository.findByUserIdAndIsDefaultTrue(address.getUser().getId())
                .ifPresent(a -> { a.setDefault(false); addressRepository.save(a); });
        address.setDefault(true);
        return AddressResponse.from(addressRepository.save(address));
    }

    private Address getOrThrow(String id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
    }

    private User currentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
