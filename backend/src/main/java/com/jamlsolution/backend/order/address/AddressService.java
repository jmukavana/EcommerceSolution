package com.jamlsolution.backend.order.address;

import com.jamlsolution.backend.order.address.request.CreateAddressRequest;
import com.jamlsolution.backend.order.address.response.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse create(CreateAddressRequest request);
    List<AddressResponse> findMyAddresses();
    AddressResponse findById(String id);
    AddressResponse update(String id, CreateAddressRequest request);
    void delete(String id);
    AddressResponse setDefault(String id);
}
