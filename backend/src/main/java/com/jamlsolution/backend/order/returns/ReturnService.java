package com.jamlsolution.backend.order.returns;

import com.jamlsolution.backend.order.returns.request.CreateReturnRequest;
import com.jamlsolution.backend.order.returns.request.ResolveReturnRequest;
import com.jamlsolution.backend.order.returns.response.ReturnResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReturnService {
    ReturnResponse create(String orderId, CreateReturnRequest request);
    ReturnResponse findById(String returnId);
    Page<ReturnResponse> findMy(Pageable pageable);
    Page<ReturnResponse> findAll(ReturnStatus status, Pageable pageable);
    ReturnResponse approve(String returnId, String adminNotes);
    ReturnResponse reject(String returnId, String adminNotes);
    ReturnResponse complete(String returnId, ResolveReturnRequest request);
}
