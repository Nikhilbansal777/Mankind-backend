#!/bin/bash

# Mankind Matrix AI Backend - Stop All Services Script
# This script stops all running microservices by killing processes on their ports

echo "🛑 Stopping Mankind Matrix AI Backend Services..."

# Ports used by the services (including Keycloak)
PORTS=(8180 8080 8081 8082 8083 8084 8085 8086 8087 8088)

# Kill any process using the service ports
for port in "${PORTS[@]}"; do
  PID=$(lsof -ti tcp:$port)
  if [ -n "$PID" ]; then
    echo "   Stopping service on port $port (PID $PID)"
    kill -9 $PID 2>/dev/null
  else
    echo "   No service running on port $port"
  fi
done

echo ""
echo "✅ All services stopped successfully!" 