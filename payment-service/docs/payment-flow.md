## Payment Flow (Stripe via Gateway)

```mermaid
sequenceDiagram
    participant U as User
    participant FE as Frontend (React)
    participant CS as cart-service
    participant OS as order-service
    participant PS as payment-service (Stripe)
    participant ST as Stripe

    %% Checkout & order creation
    U->>FE: Clicks Checkout
    FE->>CS: GET /carts/{cartId}
    CS-->>FE: cart items, totals
    FE->>OS: POST /orders {cartId}
    OS->>OS: Validate & persist Order (status=PENDING_PAYMENT)
    OS-->>FE: {orderId, amountCents, currency}

    %% Create PaymentIntent via payment-service
    FE->>OS: POST /orders/{orderId}/pay (method="stripe")
    OS->>PS: POST /payments/intents {orderId, amountCents, currency} (idempotency=order:{id})
    PS->>ST: Create PaymentIntent (metadata: orderId)
    ST-->>PS: {paymentIntentId, client_secret}
    PS-->>OS: {paymentIntentId, client_secret}
    OS-->>FE: {client_secret}

    %% Confirm on client with Stripe Elements
    FE->>ST: confirmCardPayment(client_secret, cardElement)
    ST-->>FE: result (requires_action | succeeded | failed)

    %% Webhook → payment-service → order-service
    ST-->>PS: webhook payment_intent.succeeded / payment_intent.payment_failed
    PS->>PS: Verify signature; map to domain event
    alt succeeded
        PS->>OS: POST /orders/{orderId}/payments/stripe/succeeded {paymentIntentId, chargeId}
        OS->>OS: Update Order (status=PAID), persist payment refs
    else failed
        PS->>OS: POST /orders/{orderId}/payments/stripe/failed {paymentIntentId, reason}
        OS->>OS: Update Order (status=PAYMENT_FAILED)
    end

    %% Client confirms status (optional)
    FE->>OS: GET /orders/{orderId}/status
    OS-->>FE: {status: PAID | PAYMENT_FAILED}
    FE->>U: Show success or retry
```
