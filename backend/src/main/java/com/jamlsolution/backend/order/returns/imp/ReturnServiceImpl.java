package com.jamlsolution.backend.order.returns.imp;

import com.jamlsolution.backend.exception.BusinessException;
import com.jamlsolution.backend.exception.ErrorCode;
import com.jamlsolution.backend.identity.user.User;
import com.jamlsolution.backend.identity.user.UserRepository;
import com.jamlsolution.backend.order.order.Order;
import com.jamlsolution.backend.order.order.OrderItem;
import com.jamlsolution.backend.order.order.OrderItemRepository;
import com.jamlsolution.backend.order.order.OrderRepository;
import com.jamlsolution.backend.order.order.OrderStatus;
import com.jamlsolution.backend.order.returns.*;
import com.jamlsolution.backend.order.returns.request.CreateReturnRequest;
import com.jamlsolution.backend.order.returns.request.ResolveReturnRequest;
import com.jamlsolution.backend.order.returns.response.ReturnResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReturnServiceImpl implements ReturnService {

    private final OrderReturnRepository returnRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;

    @Override
    public ReturnResponse create(String orderId, CreateReturnRequest request) {
        User user = currentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }
        if (returnRepository.existsByOrderIdAndUserId(orderId, user.getId())) {
            throw new BusinessException(ErrorCode.RETURN_ALREADY_EXISTS);
        }

        OrderReturn orderReturn = OrderReturn.builder()
                .order(order)
                .user(user)
                .status(ReturnStatus.PENDING)
                .reason(request.reason())
                .build();

        for (var itemReq : request.items()) {
            OrderItem orderItem = orderItemRepository.findById(itemReq.orderItemId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
            if (itemReq.quantity() > orderItem.getQuantity()) {
                throw new BusinessException(ErrorCode.INSUFFICIENT_STOCK);
            }
            orderReturn.getItems().add(OrderReturnItem.builder()
                    .orderReturn(orderReturn)
                    .orderItem(orderItem)
                    .quantity(itemReq.quantity())
                    .reason(itemReq.reason())
                    .build());
        }

        return ReturnResponse.from(returnRepository.save(orderReturn));
    }

    @Override
    @Transactional(readOnly = true)
    public ReturnResponse findById(String returnId) {
        return ReturnResponse.from(getOrThrow(returnId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnResponse> findMy(Pageable pageable) {
        return returnRepository.findByUserId(currentUser().getId(), pageable)
                .map(ReturnResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReturnResponse> findAll(ReturnStatus status, Pageable pageable) {
        if (status != null) {
            return returnRepository.findByStatus(status, pageable).map(ReturnResponse::from);
        }
        return returnRepository.findAll(pageable).map(ReturnResponse::from);
    }

    @Override
    public ReturnResponse approve(String returnId, String adminNotes) {
        OrderReturn orderReturn = getOrThrow(returnId);
        assertPending(orderReturn);
        orderReturn.setStatus(ReturnStatus.APPROVED);
        orderReturn.setAdminNotes(adminNotes);
        orderReturn.setResolvedAt(LocalDateTime.now());
        return ReturnResponse.from(returnRepository.save(orderReturn));
    }

    @Override
    public ReturnResponse reject(String returnId, String adminNotes) {
        OrderReturn orderReturn = getOrThrow(returnId);
        assertPending(orderReturn);
        orderReturn.setStatus(ReturnStatus.REJECTED);
        orderReturn.setAdminNotes(adminNotes);
        orderReturn.setResolvedAt(LocalDateTime.now());
        return ReturnResponse.from(returnRepository.save(orderReturn));
    }

    @Override
    public ReturnResponse complete(String returnId, ResolveReturnRequest request) {
        OrderReturn orderReturn = getOrThrow(returnId);
        if (orderReturn.getStatus() != ReturnStatus.APPROVED) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }
        orderReturn.setStatus(ReturnStatus.COMPLETED);
        orderReturn.setRefundAmount(request.refundAmount());
        orderReturn.setRefundMethod(request.refundMethod());
        orderReturn.setRefundReference(request.refundReference());
        if (request.adminNotes() != null) orderReturn.setAdminNotes(request.adminNotes());
        orderReturn.setResolvedAt(LocalDateTime.now());
        return ReturnResponse.from(returnRepository.save(orderReturn));
    }

    private void assertPending(OrderReturn orderReturn) {
        if (orderReturn.getStatus() != ReturnStatus.PENDING) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_BE_CANCELLED);
        }
    }

    private OrderReturn getOrThrow(String id) {
        return returnRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETURN_NOT_FOUND));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
