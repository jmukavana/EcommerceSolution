package com.jamlsolution.backend.order.returns;

public enum ReturnStatus {
    PENDING,    // Customer submitted; awaiting admin review
    APPROVED,   // Admin approved; waiting for goods to be returned
    REJECTED,   // Admin rejected with reason
    COMPLETED   // Refund issued
}
