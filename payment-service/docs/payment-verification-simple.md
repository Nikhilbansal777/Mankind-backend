# Payment Verification Implementation

## Overview
Payment verification endpoint that checks provider and verifies with the actual payment provider (Stripe).

## Payment Flow

```
1. Frontend creates payment intent → payment-service (stores provider)
2. Frontend completes payment with Stripe
3. Frontend waits for Stripe confirmation
4. Frontend calls order-service /pay endpoint
5. Order-service calls payment-service /verify endpoint
6. Payment-service checks provider and verifies with Stripe
7. If verified → complete order
```

## Endpoints

### Payment Verification
- **URL**: `POST /payments/verify`
- **Purpose**: Verify payment with the actual provider (Stripe)
- **Request**:
  ```json
  {
    "orderId": "order-456",
    "paymentIntentId": "pi_test_123456789"
  }
  ```
- **Response**:
  ```json
  {
    "id": 1,
    "stripePaymentIntentId": "pi_test_123456789",
    "userId": "user-123",
    "orderId": "order-456",
    "amount": 99.99,
    "currency": "USD",
    "provider": "STRIPE",
    "status": "SUCCEEDED",
    "description": "Test payment",
    "paymentSucceeded": true,
    "createdAt": "2024-01-01T10:00:00",
    "updatedAt": "2024-01-01T10:00:00"
  }
  ```

## Verification Process

1. **Find Payment**: Look up payment by payment intent ID
2. **Check Provider**: Determine which provider was used (STRIPE, PAYPAL, etc.)
3. **Verify with Provider**: Call the actual provider's API to get latest status
4. **Update Database**: Store the latest status from provider
5. **Return Result**: Include payment success status

## Provider Support

Currently supports:
- **STRIPE**: Verifies with Stripe API and updates status

Future support:
- **PAYPAL**: Will verify with PayPal API
- **ADYEN**: Will verify with Adyen API
- **BRAINTREE**: Will verify with Braintree API

## Database Schema

- `payments` table now includes `provider` field
- `provider` field stores which payment provider was used
- Used to route verification to correct provider API

## Next Steps

1. **Order Service Integration**: Implement `/pay` endpoint that calls this verification
2. **Frontend Integration**: Call order-service after Stripe confirms payment
3. **Error Handling**: Handle verification failures in order-service
4. **Additional Providers**: Add verification for PayPal, Adyen, etc.

## Testing

Use the `payment-service/api/payment-verification.http` file to test the endpoint.
