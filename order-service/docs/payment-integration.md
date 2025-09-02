# Order Service Payment Integration

## Overview
Order service now integrates with payment-service to verify payments before completing orders.

## Payment Flow

```
1. Frontend creates payment intent → order-service → payment-service
2. Frontend completes payment with Stripe
3. Frontend calls order-service /pay endpoint with paymentIntentId
4. Order-service calls payment-service /verify endpoint
5. Payment-service verifies with Stripe and returns result
6. If verified → order-service completes order
7. If not verified → order-service returns error
```

## Updated Endpoints

### Pay Order
- **URL**: `POST /orders/{orderId}/pay`
- **Purpose**: Complete order payment with verification
- **Request**:
  ```json
  {
    "paymentIntentId": "pi_test_123456789"
  }
  ```
- **Response**: Order details with updated status (CONFIRMED, PAID)

## Integration Details

### Payment Verification
- Order-service calls payment-service `/payments/verify` endpoint
- Passes `orderId` and `paymentIntentId`
- Payment-service verifies with Stripe API
- Returns payment success status

### Error Handling
- If payment verification fails → returns error to frontend
- If order validation fails → returns appropriate error
- All errors are logged and tracked in order history

### Order Status Updates
- **Order Status**: PENDING → CONFIRMED
- **Payment Status**: PENDING → PAID
- **Cart Status**: ACTIVE → CONVERTED (via cart-service)

## Dependencies

### New DTOs Added
- `PayOrderRequest`: Contains paymentIntentId
- `PaymentVerificationRequest`: For payment-service API
- `PaymentVerificationResponse`: From payment-service API

### Updated Services
- `OrderService.payOrder()`: Now verifies payment before completing
- `PaymentClient`: Added verification endpoint

## Testing

Use the `order-service/api/pay-order-requests.http` file to test:
1. Create payment intent for order
2. Pay order with payment verification
3. Check order status

## Security

- All endpoints require JWT authentication
- Order ownership is validated
- Payment intent ownership is validated by payment-service
- Order status validation prevents invalid payments
