# Scalable Microservices E-commerce Platform

A scalable e-commerce backend built using a microservices architecture.  
The platform provides services for **product catalog, inventory management, and order processing**, designed for reliability, scalability, and maintainability.

## Features

- RESTful microservices built with **Spring Boot 3**
- Product catalog and inventory management
- Order lifecycle management
- JWT-based authentication and RBAC
- Idempotent order APIs to ensure reliable transactions
- PostgreSQL database with transactional integrity
- OpenAPI documentation for API contracts
- Dockerized services for easy deployment
- Asynchronous webhooks for event notifications

## Tech Stack

- **Backend:** Java, Spring Boot 3
- **Database:** PostgreSQL
- **Authentication:** JWT
- **Build Tool:** Maven
- **Containerization:** Docker
- **API Documentation:** OpenAPI / Swagger
- **Frontend:** Next.js + TailwindCSS

## Architecture

The system follows a microservices architecture with independent services responsible for specific domains:

- **Product Service**
  - Manages product catalog and details

- **Inventory Service**
  - Handles stock levels and availability

- **Order Service**
  - Processes orders and manages order lifecycle

Each service communicates through REST APIs and maintains its own database logic.

## API Capabilities

Key API functionalities include:

- Create and manage products
- Check inventory availability
- Place and manage orders
- Secure endpoints using JWT authentication
- Transaction-safe order processing
- Idempotent order submission

## Project Structure
```
lib/         → shared utilities and components
api/         → backend API services
frontend/    → web dashboard
config files → project configuration
README.md    → project documentation
```

## Running the Project

### 1 Install dependencies
```bash
mvn clean install
```

### 2 Run services
```bash
mvn spring-boot:run
```

### 3 Run with Docker
```bash
docker-compose up --build
```

## API Documentation

OpenAPI documentation is available once the service is running:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Future Improvements

- Service discovery with Eureka
- API Gateway
- Distributed tracing
- Message queues (Kafka / RabbitMQ)
- Kubernetes deployment

## License

This project is for educational and demonstration purposes.
