package com.jamlsolution.backend.product.warehouse;

import com.jamlsolution.backend.product.warehouse.request.CreateWarehouseRequest;
import com.jamlsolution.backend.product.warehouse.response.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    WarehouseResponse create(CreateWarehouseRequest request);
    Page<WarehouseResponse> findAll(Pageable pageable);
    WarehouseResponse findById(String id);
    WarehouseResponse update(String id, CreateWarehouseRequest request);
    void delete(String id);
}
