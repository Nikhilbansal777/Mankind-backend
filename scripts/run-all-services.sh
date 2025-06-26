#!/bin/bash

# Mankind Matrix AI Backend - All Services Startup Script
# This script kills any existing processes on the service ports and starts all microservices

echo "🚀 Starting Mankind Matrix AI Backend Services..."

# Ports used by the services
PORTS=(8080 8081 8082 8083 8084 8085 8086)

# Kill any process using the service ports
echo "🔧 Cleaning up ports..."
for port in "${PORTS[@]}"; do
  PID=$(lsof -ti tcp:$port)
  if [ -n "$PID" ]; then
    echo "   Killing process on port $port (PID $PID)"
    kill -9 $PID 2>/dev/null
  else
    echo "   Port $port is free"
  fi
done

echo ""
echo "📦 Starting microservices..."

# Start microservices in the background
echo "   Starting user-service on port 8081..."
( cd user-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo "   Starting product-service on port 8080..."
( cd product-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo "   Starting cart-service on port 8082..."
( cd cart-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo "   Starting wishlist-service on port 8083..."
( cd wishlist-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo "   Starting payment-service on port 8084..."
( cd payment-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo "   Starting notification-service on port 8086..."
( cd notification-service && ./mvnw spring-boot:run > /dev/null 2>&1 & )

echo ""
echo "⏳ Waiting 15 seconds for services to initialize..."
sleep 15

echo ""
echo "🌐 Starting gateway service on port 8085..."
( cd mankind-gateway-service && mvn spring-boot:run > /dev/null 2>&1 & )

echo ""
echo "✅ All services started successfully!"
echo ""
echo "📋 Service URLs:"
echo "   Product Service: http://localhost:8080/swagger-ui/index.html"
echo "   User Service:    http://localhost:8081/swagger-ui/index.html"
echo "   Cart Service:    http://localhost:8082/swagger-ui/index.html"
echo "   Wishlist Service: http://localhost:8083/swagger-ui/index.html"
echo "   Payment Service: http://localhost:8084/swagger-ui/index.html"
echo "   Notification Service: http://localhost:8086/swagger-ui/index.html"
echo "   Gateway Service: http://localhost:8085"
echo ""
echo "💡 To stop all services, run: ./scripts/stop-all-services.sh" 