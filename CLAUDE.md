# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

All Gradle commands run from the `backend/` directory.

```bash
# Start infrastructure (PostgreSQL :5433, Redis :6379, Elasticsearch :9200, Kibana :5601)
cd infrastructure && docker-compose up -d

# Build
cd backend && ./gradlew build

# Run application (port 8050)
./gradlew bootRun

# Run all tests
./gradlew test

# Run a single test class
./gradlew test --tests "com.jamlsolution.backend.SomeTest"

# Build Docker image (multi-stage, Java 21)
docker build -t backend .
```


## Architecture

**Stack:** Java 21, Spring Boot 4.0.5, PostgreSQL, Redis, Elasticsearch, Flyway migrations, Gradle.

The application is a single Spring Boot service in `backend/src/main/java/com/jamlsolution/backend/` organized by domain module under `identity/`, with cross-cutting code in `common/`, `config/`, `exception/`, and `handler/`.

### Auth Endpoints

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Create account, returns JWT pair |
| `POST` | `/api/v1/auth/login` | Public | Validate credentials → sends OTP to email |
| `POST` | `/api/v1/auth/verify-otp` | Public | Submit OTP → returns JWT pair |
| `POST` | `/api/v1/auth/refresh` | Public | Rotate access token via refresh token |
| `POST` | `/api/v1/auth/logout` | Bearer | Revoke all refresh tokens |

MFA (EMAIL\_OTP) is **enabled by default** for all accounts. Login is always two-step: credentials → OTP email → verify.

DTOs live in `identity/auth/request/` and `identity/auth/response/`.

### RBAC Endpoints (all require `ROLE_ADMIN`)

**Permissions** — `/api/v1/permissions`
`POST /` · `GET /` · `GET /{id}` · `PUT /{id}` · `DELETE /{id}`

**Roles** — `/api/v1/roles`
`POST /` · `GET /` · `GET /{id}` · `PUT /{id}` · `DELETE /{id}`
`POST /{id}/permissions` — bulk-assign permissions to role
`DELETE /{id}/permissions/{permissionId}` — remove permission from role

**Users** — `/api/v1/users`
`GET /` (paginated) · `GET /{id}`
`POST /{id}/roles` — bulk-assign roles to user
`DELETE /{id}/roles/{roleId}` — remove role from user
`PATCH /{id}/enable` · `PATCH /{id}/disable`

### Identity Module (`identity/`)

The core domain. Key sub-packages:

| Package | Responsibility |
|---|---|
| `user/` | UserDetails implementation, JPA entity, repository |
| `role/` + `permission/` | RBAC hierarchy: User → Role → Permission |
| `security/` | JWT filter chain, `JwtService` (RS256), `KeyUtils` (loads PEM keys from classpath) |
| `mfa/` | TOTP-based multi-factor authentication |
| `password/` | Password policy enforcement and history |
| `refreshToken/` | Opaque refresh token lifecycle |
| `auditing/` | `LoginAttempt` entity — tracks failed/successful auth |

### Security Flow

1. `JwtFilter` extracts the Bearer token from each request.
2. `JwtService` validates the token using the RSA public key at `resources/keys/public_key.pem`.
3. `SecurityConfig` declares a stateless session, disables CSRF, and whitelists `/api/v1/auth/**`, `/swagger-ui/**`, and `/actuator/**`.
4. Method-level `@PreAuthorize` gates use the Permission strings from the RBAC hierarchy.

### Cross-cutting Concerns

- **`BaseEntity`** — all entities extend this; provides `createdDate`, `lastModifiedDate`, `createdBy`, `lastModifiedBy` via Spring Data JPA auditing. `ApplicationAuditorAware` pulls the username from `SecurityContextHolder`.
- **`ApiResponse<T>`** — standard response record wrapping all REST payloads.
- **`ErrorCode` enum** — maps each business error to an HTTP status; `ApplicationExceptionHandler` translates `BusinessException` into `ErrorResponse`.

### Additional Integrations

- **Spring AI** — OpenAI integration with Elasticsearch vector store.
- **Spring Batch** — batch processing jobs.
- **Google Cloud** — Pub/Sub and Cloud Storage via Spring Cloud GCP.
- **Resilience4j** — circuit breaker pattern for external calls.
- **SpringDoc OpenAPI** — Swagger UI at `/swagger-ui.html`.

### Product Module (`product/`)

Organized by feature package — each package is flat (entity, repository, service interface, `imp/` impl, `request/`, `response/`):

| Package | Entities | Base path |
|---|---|---|
| `brand/` | `Brand` | `/api/v1/brands` |
| `category/` | `Category` (self-ref tree) | `/api/v1/categories` |
| `product/` | `Product`, `ProductVariant`, `ProductImage`, `ProductAttribute`, `ProductAttributeValue` | `/api/v1/products` |
| `warehouse/` | `Warehouse` | `/api/v1/warehouses` |
| `inventory/` | `InventoryItem`, `StockMovement` | `/api/v1/inventory` |
| `review/` | `ProductReview`, `ReviewHelpfulness` | `/api/v1/reviews` |
| `wishlist/` | `Wishlist`, `WishlistItem` | `/api/v1/wishlists` |
| `discount/` | `Discount`, `Coupon`, `CouponUsage` | `/api/v1/discounts` (coupons at `/api/v1/discounts/coupons`) |
| `analytics/` | `ProductView`, `ProductAnalyticsSummary` | `/api/v1/analytics` |

Key routes beyond standard CRUD:
- `GET /api/v1/categories/roots` — root categories; `GET /api/v1/categories/{id}/children`
- `POST /api/v1/products/{id}/variants` · `PUT/DELETE /{id}/variants/{variantId}`
- `POST /api/v1/products/{id}/images` · `DELETE /{id}/images/{imageId}`
- `POST /api/v1/inventory/receive` — receive stock; `POST /api/v1/inventory/{id}/adjust`
- `GET /api/v1/inventory/low-stock` · `GET /api/v1/inventory/{id}/movements`
- `PATCH /api/v1/reviews/{id}/approve|reject` · `POST /api/v1/reviews/{id}/helpful?helpful=true`
- `GET /api/v1/wishlists/my` · `GET /api/v1/wishlists/shared/{token}`
- `GET /api/v1/discounts/coupons/validate?code=XXX`
- `POST /api/v1/analytics/track/{productId}` · `GET /api/v1/analytics/products/{id}/summary|history`

### Order Module (`order/`)

Same package structure (entity, repository, service, `imp/`, `request/`, `response/`):

| Package | Entities | Base path |
|---|---|---|
| `address/` | `Address` | `/api/v1/addresses` |
| `cart/` | `Cart`, `CartItem` | `/api/v1/cart` |
| `order/` | `Order`, `OrderItem` | `/api/v1/orders` |
| `payment/` | `Payment` | `/api/v1/payments` |
| `shipment/` | `Shipment` | `/api/v1/shipments` |

Key routes:
- `GET /api/v1/cart` · `POST /api/v1/cart/items` · `PUT /api/v1/cart/items/{itemId}` · `DELETE /api/v1/cart/items/{itemId}` · `DELETE /api/v1/cart`
- `POST /api/v1/addresses/{id}/default` — set default address
- `POST /api/v1/orders` — place order from cart (deducts inventory, applies coupon)
- `GET /api/v1/orders/my` — current user's orders · `GET /api/v1/orders` (admin)
- `PATCH /api/v1/orders/{id}/cancel` · `PATCH /api/v1/orders/{id}/status` (admin)
- `GET /api/v1/payments/order/{orderId}` · `PATCH /api/v1/payments/{id}/status` (admin)
- `GET /api/v1/shipments/order/{orderId}` · `PUT /api/v1/shipments/{id}` (admin — updates tracking + auto-advances order status)

Order flow: `PENDING` → `CONFIRMED` (on payment) → `PROCESSING` (on shipment create) → `SHIPPED` → `DELIVERED`.

Enums: `OrderStatus`, `PaymentMethod` (CASH_ON_DELIVERY, CREDIT_CARD, DEBIT_CARD, MOBILE_MONEY, BANK_TRANSFER, PAYPAL), `PaymentStatus`, `ShipmentStatus`.

### Accounting Module (`accounting/`)

Double-entry bookkeeping auto-wired to order/payment events.

| Package | Responsibility | Base path |
|---|---|---|
| `account/` | Chart of accounts (CRUD) | `/api/v1/accounting/accounts` |
| `journal/` | Journal entries (create, post, query) | `/api/v1/accounting/journal` |
| `report/` | Financial reports | `/api/v1/accounting/reports` |
| `event/` | `AccountingEventListener` — auto-creates posted journal entries | — |
| `seed/` | `AccountSeeder` (CommandLineRunner @Order(2)) seeds 12 system accounts | — |

**System accounts seeded at startup:** 1000 Cash, 1100 AR, 1200 Inventory, 2100 Tax Payable, 4000 Sales Revenue, 4100 Shipping Revenue, 5000 COGS, and others. Codes in `SystemAccounts.java`.

**Auto-journal entries (fired via Spring `@TransactionalEventListener AFTER_COMMIT`):**
- Order placed → DR AR, CR Sales Revenue, CR Shipping Revenue, CR Tax Payable
- Payment received → DR Cash, CR AR
- Order cancelled → reversal of original sale entry

**Reports:** `GET /api/v1/accounting/reports/trial-balance?from=&to=` · `income-statement?from=&to=` · `balance-sheet?asOf=`

### Notification Module (`notification/`)

`NotificationEventListener` subscribes to the same order/payment events and sends transactional emails via `EmailService` + Thymeleaf.

**Templates** (`resources/templates/email/`): `otp-email.html`, `order-confirmation.html`, `order-status-update.html`, `payment-confirmation.html`.

**Events** (`order/events/`): `OrderPlacedEvent`, `OrderStatusChangedEvent`, `PaymentReceivedEvent` — published by `OrderServiceImpl` and `PaymentServiceImpl` via `ApplicationEventPublisher`.

### Database

Schema is auto-managed by `ddl-auto: update`. Primary datasource (`backend_db` on PostgreSQL :5432) is configured in `application.yaml`.
