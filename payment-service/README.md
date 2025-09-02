# Payment Service

## Overview

The Payment Service is a microservice responsible for handling credit/debit card payments using Stripe as the payment gateway. It provides endpoints for creating payment intents, checking payment status, and retrieving payment history.

## Features

- **Multi-Provider Support**: Extensible architecture supporting multiple payment providers
- **Stripe Integration**: Create payment intents and return client_secret for frontend processing
- **Provider Strategy Pattern**: Easy to add new payment providers (PayPal, Adyen, etc.)
- **Track payment status and history**
- **Role-based access control** (User and Admin endpoints)
- **Stripe webhook integration** for payment status updates
- **Comprehensive logging** of payment events

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA (Hibernate)
- Spring Security
- Lombok
- Stripe Java SDK
- MySQL
- Docker

## API Documentation

Detailed API documentation is available in the [docs/api-documentation.md](docs/api-documentation.md) file.

You can also access the Swagger UI at `http://localhost:8084/swagger-ui.html` when the service is running.

## Setup and Configuration

### Prerequisites

- Java 17
- Maven
- Docker and Docker Compose (for containerized deployment)
- MySQL database
- Stripe account with API keys

### Environment Variables

Copy the `env.template` file to `.env` and configure the following environment variables:

```
# Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mankind_matrix_db
DB_USERNAME=matrix_user
DB_PASSWORD=matrix_pass

# Stripe Configuration
STRIPE_API_KEY=your_stripe_secret_key
STRIPE_WEBHOOK_SECRET=your_stripe_webhook_secret
STRIPE_PUBLIC_KEY=your_stripe_public_key
```

### Building and Running

#### Local Development

1. Clone the repository
2. Copy `env.template` to `.env` and configure your environment variables
3. Ensure MySQL is running and the `mankind_matrix_db` database exists
4. Run the payment tables script: `mysql -u root -p < scripts/payment_tables.sql`
5. Build the application: `mvn clean package`
6. Run the application: `mvn spring-boot:run`

#### Docker Deployment

1. Build the Docker image: `docker build -t payment-service .`
2. Run the container: `docker run -p 8084:8080 --env-file .env payment-service`

Alternatively, use Docker Compose to run the entire application stack:

```
docker-compose up -d
```

## Testing

### API Testing

You can test the API endpoints using the provided `api/requests.http` file or with tools like Postman:

1. **Create Payment Intent**: `POST /payments/intents`
2. **Get Payment Intent**: `GET /payments/intents/{stripePaymentIntentId}`

### Unit Tests

Run the unit tests with Maven:

```
mvn test
```

### Integration Tests

Run the integration tests with Maven:

```
mvn verify
```

### Manual Testing with Swagger

1. Start the application
2. Open `http://localhost:8084/swagger-ui.html` in your browser
3. Use the Swagger UI to test the API endpoints

## Architecture

### Payment Provider Strategy Pattern

The service uses a strategy pattern to support multiple payment providers:

- **PaymentService**: Main orchestrator that routes requests based on provider
- **StripePaymentService**: Handles Stripe-specific payment logic
- **Future Providers**: Easy to add PayPal, Adyen, Braintree, etc.

### Current Implementation

- ✅ **STRIPE**: Fully implemented with payment intent creation
- ❌ **PAYPAL**: Not implemented (returns 501 Not Implemented)
- ❌ **ADYEN**: Not implemented (returns 501 Not Implemented)
- ❌ **BRAINTREE**: Not implemented (returns 501 Not Implemented)

## Stripe Integration

This service integrates with Stripe for payment processing. To test payments:

- See the full payment flow diagram in `docs/payment-flow.md`.

1. Use Stripe test keys in your environment variables
2. Use Stripe test cards for payment testing:
   - Success: `4242 4242 4242 4242`
   - Requires Authentication: `4000 0025 0000 3155`
   - Declined: `4000 0000 0000 0002`

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request