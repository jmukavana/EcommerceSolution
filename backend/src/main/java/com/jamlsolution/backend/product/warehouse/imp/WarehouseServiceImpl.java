package com.jamlsolution.backend.product.warehouse.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.product.warehouse.Warehouse;
import com.jamlsolution.backend.product.warehouse.WarehouseRepository;
import com.jamlsolution.backend.product.warehouse.WarehouseService;
import com.jamlsolution.backend.product.warehouse.request.CreateWarehouseRequest;
import com.jamlsolution.backend.product.warehouse.response.WarehouseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponse create(CreateWarehouseRequest request) {
        if (warehouseRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorCode.WAREHOUSE_CODE_EXISTS);
        }
        Warehouse warehouse = Warehouse.builder()
                .code(request.code().toUpperCase())
                .name(request.name())
                .addressLine1(request.addressLine1())
                .addressLine2(request.addressLine2())
                .city(request.city())
                .state(request.state())
                .country(request.country())
                .postalCode(request.postalCode())
                .phone(request.phone())
                .email(request.email())
                .isDefault(request.isDefault())
                .active(true)
                .build();
        return WarehouseResponse.from(warehouseRepository.save(warehouse));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WarehouseResponse> findAll(Pageable pageable) {
        return warehouseRepository.findAll(pageable).map(WarehouseResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse findById(String id) {
        return WarehouseResponse.from(getOrThrow(id));
    }

    @Override
    public WarehouseResponse update(String id, CreateWarehouseRequest request) {
        Warehouse warehouse = getOrThrow(id);
        String code = request.code().toUpperCase();
        if (!warehouse.getCode().equals(code) && warehouseRepository.existsByCode(code)) {
            throw new BusinessException(ErrorCode.WAREHOUSE_CODE_EXISTS);
        }
        warehouse.setCode(code);
        warehouse.setName(request.name());
        warehouse.setAddressLine1(request.addressLine1());
        warehouse.setAddressLine2(request.addressLine2());
        warehouse.setCity(request.city());
        warehouse.setState(request.state());
        warehouse.setCountry(request.country());
        warehouse.setPostalCode(request.postalCode());
        warehouse.setPhone(request.phone());
        warehouse.setEmail(request.email());
        warehouse.setDefault(request.isDefault());
        return WarehouseResponse.from(warehouseRepository.save(warehouse));
    }

    @Override
    public void delete(String id) {
        warehouseRepository.delete(getOrThrow(id));
    }

    private Warehouse getOrThrow(String id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.WAREHOUSE_NOT_FOUND));
    }
}
