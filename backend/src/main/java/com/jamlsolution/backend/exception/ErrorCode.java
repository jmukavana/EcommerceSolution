package com.jamlsolution.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    MAIL_ALREADY_EXISTS("ERR_EMAIL_EXISTS", "Email already exists", CONFLICT),
    PHONE_ALREADY_EXISTS("ERR_PHONE_EXISTS", "An account with this phone number already exists", CONFLICT),
    PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "The password and confirmation do not match", BAD_REQUEST),
    CHANGE_PASSWORD_MISMATCH("ERR_PASSWORD_MISMATCH", "New password and confirmation do not match", BAD_REQUEST),
    ERR_SENDING_ACTIVATION_EMAIL("ERR_SENDING_ACTIVATION_EMAIL",
            "An error occurred while sending the activation email",
            HttpStatus.INTERNAL_SERVER_ERROR),

    ERR_USER_DISABLED("ERR_USER_DISABLED",
            "User account is disabled, please activate your account or contact the administrator",
            UNAUTHORIZED),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "The current password is incorrect", BAD_REQUEST),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found", NOT_FOUND),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "Account has been deactivated", BAD_REQUEST),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION",
            "An internal exception occurred, please try again or contact the admin",
            HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "Cannot find user with the provided username", NOT_FOUND),
    ACCOUNT_LOCKED("ERR_ACCOUNT_LOCKED", "Account is temporarily locked due to too many failed login attempts", LOCKED),
    INVALID_REFRESH_TOKEN("ERR_INVALID_REFRESH_TOKEN", "Refresh token is invalid, expired, or revoked", UNAUTHORIZED),
    INVALID_OTP("ERR_INVALID_OTP", "OTP code is invalid or has expired", UNAUTHORIZED),
    PASSWORD_TOO_SHORT("ERR_PASSWORD_TOO_SHORT", "Password must be at least %d characters long", BAD_REQUEST),
    PASSWORD_MISSING_UPPERCASE("ERR_PASSWORD_MISSING_UPPERCASE", "Password must contain at least one uppercase letter (A-Z)", BAD_REQUEST),
    PASSWORD_MISSING_LOWERCASE("ERR_PASSWORD_MISSING_LOWERCASE", "Password must contain at least one lowercase letter (a-z)", BAD_REQUEST),
    PASSWORD_MISSING_NUMBER("ERR_PASSWORD_MISSING_NUMBER", "Password must contain at least one digit (0-9)", BAD_REQUEST),
    PASSWORD_MISSING_SPECIAL("ERR_PASSWORD_MISSING_SPECIAL", "Password must contain at least one special character (!@#$%%^&*)", BAD_REQUEST),
    PASSWORD_REUSE("ERR_PASSWORD_REUSE", "Password has been used in the last %d entries. Please choose a different password", BAD_REQUEST),
    PASSWORD_EXPIRED("ERR_PASSWORD_EXPIRED", "Your password has expired. Please change your password to continue", UNAUTHORIZED),
    ROLE_NOT_FOUND("ERR_ROLE_NOT_FOUND", "Role not found", NOT_FOUND),
    ROLE_ALREADY_EXISTS("ERR_ROLE_EXISTS", "A role with this name already exists", CONFLICT),
    PERMISSION_NOT_FOUND("ERR_PERMISSION_NOT_FOUND", "Permission not found", NOT_FOUND),
    PERMISSION_ALREADY_EXISTS("ERR_PERMISSION_EXISTS", "A permission with this name already exists", CONFLICT),

    // Product domain
    BRAND_NOT_FOUND("ERR_BRAND_NOT_FOUND", "Brand not found", NOT_FOUND),
    BRAND_NAME_EXISTS("ERR_BRAND_NAME_EXISTS", "A brand with this name already exists", CONFLICT),
    BRAND_SLUG_EXISTS("ERR_BRAND_SLUG_EXISTS", "A brand with this slug already exists", CONFLICT),
    CATEGORY_NOT_FOUND("ERR_CATEGORY_NOT_FOUND", "Category not found", NOT_FOUND),
    CATEGORY_SLUG_EXISTS("ERR_CATEGORY_SLUG_EXISTS", "A category with this slug already exists", CONFLICT),
    PRODUCT_NOT_FOUND("ERR_PRODUCT_NOT_FOUND", "Product not found", NOT_FOUND),
    PRODUCT_SLUG_EXISTS("ERR_PRODUCT_SLUG_EXISTS", "A product with this slug already exists", CONFLICT),
    VARIANT_NOT_FOUND("ERR_VARIANT_NOT_FOUND", "Product variant not found", NOT_FOUND),
    VARIANT_SKU_EXISTS("ERR_VARIANT_SKU_EXISTS", "A variant with this SKU already exists", CONFLICT),
    WAREHOUSE_NOT_FOUND("ERR_WAREHOUSE_NOT_FOUND", "Warehouse not found", NOT_FOUND),
    WAREHOUSE_CODE_EXISTS("ERR_WAREHOUSE_CODE_EXISTS", "A warehouse with this code already exists", CONFLICT),
    INVENTORY_ITEM_NOT_FOUND("ERR_INVENTORY_NOT_FOUND", "Inventory item not found", NOT_FOUND),
    REVIEW_NOT_FOUND("ERR_REVIEW_NOT_FOUND", "Review not found", NOT_FOUND),
    WISHLIST_NOT_FOUND("ERR_WISHLIST_NOT_FOUND", "Wishlist not found", NOT_FOUND),
    WISHLIST_ITEM_NOT_FOUND("ERR_WISHLIST_ITEM_NOT_FOUND", "Wishlist item not found", NOT_FOUND),
    DISCOUNT_NOT_FOUND("ERR_DISCOUNT_NOT_FOUND", "Discount not found", NOT_FOUND),
    COUPON_NOT_FOUND("ERR_COUPON_NOT_FOUND", "Coupon not found", NOT_FOUND),
    COUPON_CODE_EXISTS("ERR_COUPON_CODE_EXISTS", "A coupon with this code already exists", CONFLICT),
    COUPON_INVALID("ERR_COUPON_INVALID", "Coupon is invalid or inactive", BAD_REQUEST),
    COUPON_EXPIRED("ERR_COUPON_EXPIRED", "Coupon has expired", BAD_REQUEST),
    COUPON_USAGE_LIMIT_REACHED("ERR_COUPON_LIMIT", "Coupon usage limit has been reached", BAD_REQUEST),

    // Order domain
    ADDRESS_NOT_FOUND("ERR_ADDRESS_NOT_FOUND", "Address not found", NOT_FOUND),
    CART_ITEM_NOT_FOUND("ERR_CART_ITEM_NOT_FOUND", "Cart item not found", NOT_FOUND),
    CART_EMPTY("ERR_CART_EMPTY", "Cart is empty, cannot place order", BAD_REQUEST),
    INSUFFICIENT_STOCK("ERR_INSUFFICIENT_STOCK", "Insufficient stock for one or more items", BAD_REQUEST),
    ORDER_NOT_FOUND("ERR_ORDER_NOT_FOUND", "Order not found", NOT_FOUND),
    ORDER_CANNOT_BE_CANCELLED("ERR_ORDER_NOT_CANCELLABLE", "Order cannot be cancelled in its current status", BAD_REQUEST),
    PAYMENT_NOT_FOUND("ERR_PAYMENT_NOT_FOUND", "Payment not found", NOT_FOUND),
    SHIPMENT_NOT_FOUND("ERR_SHIPMENT_NOT_FOUND", "Shipment not found", NOT_FOUND),

    // Accounting domain
    ACCOUNT_NOT_FOUND("ERR_ACCOUNT_NOT_FOUND", "Account not found", NOT_FOUND),
    ACCOUNT_CODE_EXISTS("ERR_ACCOUNT_CODE_EXISTS", "An account with this code already exists", CONFLICT),
    JOURNAL_ENTRY_NOT_FOUND("ERR_JOURNAL_NOT_FOUND", "Journal entry not found", NOT_FOUND),
    JOURNAL_ENTRY_ALREADY_POSTED("ERR_JOURNAL_POSTED", "Journal entry is already posted and cannot be modified", BAD_REQUEST),
    JOURNAL_ENTRY_NOT_BALANCED("ERR_JOURNAL_UNBALANCED", "Journal entry debits and credits must be equal", BAD_REQUEST),

    // Tax domain
    TAX_RATE_NOT_FOUND("ERR_TAX_RATE_NOT_FOUND", "Tax rate not found", NOT_FOUND),
    TAX_RATE_ALREADY_EXISTS("ERR_TAX_RATE_EXISTS", "A tax rate for this class and country already exists", CONFLICT),

    // Returns domain
    RETURN_NOT_FOUND("ERR_RETURN_NOT_FOUND", "Return request not found", NOT_FOUND),
    RETURN_ALREADY_EXISTS("ERR_RETURN_EXISTS", "A return request already exists for this order", CONFLICT),
    ;

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String  defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
