# Order Service

This service handles order creation, management, payment processing, and lifecycle management for the Mankind e-commerce platform.

## Core Features

- **Order Management**: Create, update, and manage user orders
- **Payment Processing**: Handle order payments with external service integration
- **Cart Integration**: Convert active carts to confirmed orders
- **Coupon Management**: Apply and track discount coupons
- **Status Tracking**: Comprehensive order and payment status management
- **Delivery Options**: Support for Standard and Express delivery types
- **Shipping Management**: Address validation and shipping date scheduling

## Business Rules

### Order Creation & Updates
- Orders are created from active user carts only
- If an order already exists for a cart, it will be updated with new information
- Order updates always recalculate totals based on current cart items
- Shipping address must belong to the authenticated user
- Shipping value must be between 0 and 1000

### Order Status Flow
1. **PENDING** → Initial status when order is created
2. **CONFIRMED** → Order confirmed after successful payment
3. **PROCESSING** → Order being prepared for shipping
4. **SHIPPED** → Order has been shipped
5. **DELIVERED** → Order delivered to customer
6. **CANCELLED** → Order cancelled (only from PENDING status)

### Payment Status Flow
1. **PENDING** → Initial payment status
2. **PAID** → Payment completed successfully
3. **FAILED** → Payment processing failed
4. **REFUNDED** → Payment has been refunded
5. **PARTIALLY_REFUNDED** → Partial refund processed

### Payment Processing Rules
- Only orders with **PENDING** status can be paid
- Already paid orders cannot be paid again
- Failed payments can be retried
- Payment attempts are tracked and logged
- External payment service integration is supported
- Cart status automatically changes to **CONVERTED** after payment

### Coupon Rules
- Coupons are validated during order creation
- Discounts are calculated and applied to order totals
- Coupons are marked as used only after successful payment
- Coupon validation occurs through external coupon service

### Cart Integration
- Cart must be in **ACTIVE** status for order creation
- Cart status changes to **CONVERTED** after payment completion
- Cart items are automatically converted to order items
- Cart service is called externally to update status

## Payment Service Integration

### Architecture
- **Payment Intent Creation**: New endpoint to create payment intents via external payment service
- **PaymentClient**: OpenFeign client for communication with payment-service
- **PaymentService**: Dedicated service for handling payment operations (extracted from OrderService)
- **Multi-Provider Support**: Supports STRIPE, PAYPAL, ADYEN, and BRAINTREE providers

### Payment Intent Flow
1. User requests payment intent via `/orders/{orderId}/payment-intent`
2. System validates order ownership and status (must be PENDING)
3. Order details are retrieved and payment intent request is created
4. Payment intent is created via external payment service
5. Client secret is returned for frontend payment processing
6. Order status history is updated with payment intent creation

### Payment Completion Flow
1. User initiates payment via `/orders/{orderId}/pay`
2. System validates order can be paid
3. Payment is processed through configured payment service
4. Order status updates to CONFIRMED
5. Payment status updates to PAID
6. Cart status changes to CONVERTED
7. Coupons are marked as used
8. Complete audit trail is created

### Error Handling
- Payment failures are logged and tracked
- Failed payments can be retried
- Payment attempt limits are configurable
- Comprehensive error messages for debugging

## Database Schema

### Core Tables
- `orders` - Main order information
- `order_items` - Individual items in orders
- `order_status_history` - Complete audit trail

### Payment Fields
- `payment_id` - External payment service reference ID
- `payment_status` - Current payment status (PENDING, PAID, FAILED, etc.)

## Service Architecture

### Service Layer Separation
The order-service follows a clean separation of concerns with dedicated services:

- **OrderService**: Core order management operations (order creation, updates, validation)
- **PaymentService**: Payment-specific operations (payment intent creation, payment processing)
- **CurrentUserService**: User context and authentication operations
- **OrderNumberGenerator**: Order number generation logic
- **RoleVerificationService**: Role-based access control

## External Service Dependencies

### Cart Service
- **Purpose**: Update cart status to CONVERTED after payment
- **Required**: For successful order completion

### Coupon Service
- **Purpose**: Mark coupons as used after payment
- **Required**: For coupon tracking and validation

### User Service
- **Purpose**: Validate shipping address ownership
- **Required**: For address validation during order creation

### Payment Service
- **Purpose**: Create payment intents for order processing
- **Required**: For payment intent creation and payment processing
- **Endpoints**: `/payments/intents` (POST)

## Getting Started

1. **Database Setup**: Run `scripts/order_tables.sql`
2. **Configuration**: Set environment variables
3. **External Services**: Ensure cart, coupon, and user services are running
4. **Start Service**: Run the Spring Boot application

## Documentation

- **API Documentation**: Available via Swagger UI
- **Database Schema**: See `scripts/` directory
- **Architecture**: See `docs/` directory
