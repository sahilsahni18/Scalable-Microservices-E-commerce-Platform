# E-Commerce Order & Inventory Platform

A production-ready e-commerce backend built with **Spring Boot 3**, featuring a full order lifecycle, JWT-based authentication, Redis caching, idempotent APIs, and async webhook delivery — containerized with Docker.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21, Spring Boot 3.5 |
| Database | PostgreSQL 15 |
| Cache | Redis 7 |
| Auth | JWT (JJWT 0.11.5) + Spring Security |
| Docs | OpenAPI 3 / Swagger UI |
| Build | Maven 3.9 |
| Containers | Docker, Docker Compose |

---

## Architecture
<img width="1090" height="624" alt="image" src="https://github.com/user-attachments/assets/b2811c62-e589-4fbb-8148-864e416af29d" />

---
## Features

- **Stateless REST API** — no server-side sessions, JWT on every request
- **JWT-based RBAC** — `ADMIN` and `CUSTOMER` roles with `@PreAuthorize` scoping
- **Idempotent order creation** — `Idempotency-Key` header prevents duplicate orders on retry
- **PostgreSQL transactions** — `@Transactional` ensures stock reservation and order creation are atomic
- **Redis caching** — product catalog cached with 5-minute TTL, evicted on update
- **Async webhooks** — order state changes trigger non-blocking HTTP callbacks via `@Async`
- **OpenAPI docs** — Swagger UI available at `/swagger-ui.html`
- **Docker Compose** — one command spins up app + postgres + redis

---

## Project Structure
```
src/main/java/com/ecommerce/orderservice/
├── config/          # SecurityConfig, RedisConfig, AsyncConfig
├── controller/      # AuthController, ProductController, InventoryController, OrderController
├── dto/
│   ├── request/     # LoginRequest, RegisterRequest, CreateProductRequest, CreateOrderRequest
│   └── response/    # AuthResponse, ProductResponse, OrderResponse
├── entity/          # User, Product, Inventory, Order, OrderItem, WebhookEvent
├── enums/           # Role, OrderStatus
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException
├── repository/      # JPA repositories for all entities
├── security/        # JwtUtil, JwtFilter, UserDetailsServiceImpl
└── service/         # AuthService, ProductService, InventoryService, OrderService, WebhookService
```


---

## Database Schema
```
users          → id, username, email, password, role
products       → id, name, description, price, created_at, updated_at
inventory      → id, product_id (FK), quantity, reserved
orders         → id, user_id (FK), status, idempotency_key, total_amount, created_at
order_items    → id, order_id (FK), product_id (FK), quantity, unit_price
webhook_events → id, order_id (FK), event_type, payload, status, created_at
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker Desktop

### 1. Clone the repo
```bash
git clone https://github.com/lvb05/E-Commerce-platform.git
cd E-Commerce-platform
```

### 2. Start infrastructure
```bash
docker compose up -d
```

This starts PostgreSQL (port 5432) and Redis (port 6379). The DB schema is auto-applied from `src/main/resources/db/init.sql`.

### 3. Run the application
```bash
mvn spring-boot:run
```

App starts on `http://localhost:8080`

### 4. Open API docs
```
http://localhost:8080/swagger-ui.html
```

---

## API Reference

### Auth
```bash
# Register
POST /api/auth/register
Content-Type: application/json

{
  "username": "lava",
  "email": "lava@example.com",
  "password": "password123"
}

# Login — returns JWT token
POST /api/auth/login
{
  "username": "lava",
  "password": "password123"
}
```

### Products
```bash
# Create product (ADMIN only)
POST /api/products
Authorization: Bearer <token>
{
  "name": "Mechanical Keyboard",
  "description": "TKL layout, brown switches",
  "price": 4999.00
}

# Get all products (cached in Redis)
GET /api/products

# Get by ID
GET /api/products/{id}
```

### Inventory
```bash
# Get stock for a product
GET /api/inventory/{productId}

# Update stock (ADMIN only)
PATCH /api/inventory/{productId}
Authorization: Bearer <token>
{
  "quantity": 100
}
```

### Orders
```bash
# Place order with idempotency key
POST /api/orders
Authorization: Bearer <token>
Idempotency-Key: unique-request-id-123
{
  "productId": "uuid-here",
  "quantity": 2
}

# Get your orders
GET /api/orders/my
Authorization: Bearer <token>

# Update order status (ADMIN only)
PATCH /api/orders/{id}/status?status=CONFIRMED
Authorization: Bearer <token>
```

---

## Default Admin Account

A seeded admin account is created on first startup:
```
Username: admin
Password: admin123
```

---

## Key Design Decisions

**Idempotency** — Orders accept an optional `Idempotency-Key` header. If the same key is sent twice (e.g. due to a network retry), the existing order is returned rather than creating a duplicate. The key is stored in the DB with a unique constraint.

**Transactional stock reservation** — Stock reservation and order creation happen inside a single `@Transactional` method. If the order save fails, the inventory reservation is rolled back automatically.

**Redis caching** — Product lookups are cached with `@Cacheable("products")`. Cache is evicted with `@CacheEvict` on any product update or delete, keeping data consistent.

**Async webhooks** — Order status changes publish an `ApplicationEvent` caught by `@Async @EventListener` in `WebhookService`. This keeps the HTTP response fast while delivery happens in the background.

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/ecommerce_db` | PostgreSQL URL |
| `SPRING_DATASOURCE_USERNAME` | `ecommerce_user` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | `ecommerce_pass` | DB password |
| `SPRING_DATA_REDIS_HOST` | `localhost` | Redis host |
| `APP_JWT_SECRET` | set in yml | JWT signing secret (min 32 chars) |
| `APP_JWT_EXPIRATION` | `86400000` | Token expiry in ms (24h) |

---

## License

For educational and demonstration purposes.
