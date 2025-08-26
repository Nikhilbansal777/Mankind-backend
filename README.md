# Mankind Matrix AI Backend

This repository contains the backend microservices for the **Mankind Matrix AI** platform.

## Clone Repository

1. **Clone the Repository**
   ```bash
   git clone https://github.com/rebeccayilma/mankind-backend.git
   cd mankind-backend
   ```

## Quick Start

This project can be run in two ways:

1. **Local Development** - Run services directly on your machine
   - Requires: JDK 17, MySQL, Maven
   - Best for: Development and debugging
   - [Jump to Local Setup →](#local-development)

2. **Docker Environment** - Run services in containers
   - Requires: Docker and Docker Compose
   - Best for: Production-like environment and quick setup
   - [Jump to Docker Setup →](#docker-environment)

You can choose your preferred method based on your needs. Docker is recommended for most users as it provides a consistent environment and a simpler setup.

## Local Development

### Prerequisites
   <details>
   <summary><b>Java (JDK 17) </b></summary>

      #### macOS
         - Install Homebrew (if not already installed)
         ```bash
         /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
         ```
         - Install Java
         ```bash
         brew install openjdk@17
         ```
         - Create Java symlink
         ```bash
         sudo ln -sfn $(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
         ```

      #### Windows
      1. Download OpenJDK 17 from [Adoptium](https://adoptium.net/)
      2. Run the installer
      3. Configure environment: Set JAVA_HOME in System Environment Variables

      #### Linux (Ubuntu/Debian)
      ```bash
      sudo apt update
      sudo apt install openjdk-17-jdk
      ```
   </details>

   <details>
   <summary><b>MySQL</b></summary>

   #### macOS
   ```bash
   brew install mysql
   brew services start mysql
   ```

   #### Windows
   1. Download MySQL Installer from [MySQL Website](https://dev.mysql.com/downloads/installer/)
   2. Choose "Server only" or "Custom" installation type
   3. Follow the setup wizard

   #### Linux (Ubuntu/Debian)
   ```bash
   sudo apt install mysql-server
   ```
   </details>

### Run Service

1. **Configure Environment**
   - Copy `.env.example` on the folder root
   - Paste that file in each microservice directory
   - Rename the file to `.env`
   - Update the database connection details in each `.env` file
   - For detailed database configuration, see [Database Setup](#database-setup) section

2. **Keycloak Setup (Required for Authentication)**
   #### Setup - Run the command
      ```bash
      ./scripts/setup-keycloak.sh
      ```
   > **Note:** If for any reason you need to reset realm, there is a script to make it, just run the command : ./scripts/reset-mankind-realm.sh, but only in extreme need.

3. **Build the Services**

    **Option 1: Use the provided script (easiest method)**
   ```bash
   mvn clean install
    ```
   
   <details>
      <summary><b>Option 2: Manual build (build services individually)</b></summary>
      
      ```bash
      cd user-service
      ./mvnw clean install
      ```
      or 

      ```bash
      cd product-service
      ./mvnw clean install
      ```
      Build Gateway
      ```bash
      cd mankind-gateway-service
      mvn clean install
      ```
      > **Note:** The gateway service uses `mvn clean install` because it does not have a Maven Wrapper (`mvnw`). The other services use `mvn clean install` for consistency and portability.
    </details>

4. **Run the Services**

   **Option 1: Use the provided script (easiest method)**
   ```bash
   ./scripts/run-all-services.sh
   ```
   <details>
   <summary><b>Option 2: Manual startup (run services individually)</b></summary>

      **Start Keycloak (Required First)**
      ```bash
      cd keycloak-26.0.5
      ./bin/kc.sh start-dev
      ```

      Start each service in separate terminals:
      
      **User Service (Port 8081):**
      ```bash
      cd user-service
      ./mvnw spring-boot:run
      ```
      
      **Product Service (Port 8080):**
      ```bash
      cd product-service
      ./mvnw spring-boot:run
      ```
      
      **Cart Service (Port 8082):**
      ```bash
      cd cart-service
      ./mvnw spring-boot:run
      ```
      
      **Wishlist Service (Port 8083):**
      ```bash
      cd wishlist-service
      ./mvnw spring-boot:run
      ```
      
      **Payment Service (Port 8084):**
      ```bash
      cd payment-service
      ./mvnw spring-boot:run
      ```
      
      **Notification Service (Port 8086):**
      ```bash
      cd notification-service
      ./mvnw spring-boot:run
      ```
      
      **Coupon Service (Port 8087):**
      ```bash
      cd coupon-service
      ./mvnw spring-boot:run
      ```
      
      **Order Service (Port 8088):**
      ```bash
      cd order-service
      ./mvnw spring-boot:run
      ```
      
      **Gateway Service (Port 8085):**
      ```bash
      cd mankind-gateway-service
      mvn spring-boot:run
      ```
      > **Note:** The gateway service uses `mvn spring-boot:run` because it does not have a Maven Wrapper (`mvnw`). The other services use `./mvnw spring-boot:run` for consistency and portability.
   </details>
   
   **To stop all services:**
   ```bash
   ./scripts/stop-all-services.sh
   ```

### Service Swagger documentation links:
- Gateway: [http://localhost:8085](http://localhost:8085)

### Services documentation

Each service has its detailed documentation. Click on the service name to view its specific README:

#### Available Services
- [`user-service/`](user-service/README.md)
- [`product-service/`](product-service/README.md)
- [`cart-service/`](cart-service/README.md)
- [`wishlist-service/`](wishlist-service/README.md)
- [`payment-service/`](payment-service/README.md)
- [`coupon-service/`](coupon-service/README.md)
- [`order-service/`](order-service/README.md)

------------

## Docker Environment
### Prerequisites
   <details>
   <summary><b>Docker and Docker Compose</b></summary>

   #### macOS
   1. Install Docker Desktop for Mac
      - Download from [Docker's official website](https://www.docker.com/products/docker-desktop)
      - Docker Desktop includes both Docker Engine and Docker Compose
      - Follow the installation wizard

   #### Windows
   1. Install Docker Desktop for Windows
      - Download from [Docker's official website](https://www.docker.com/products/docker-desktop)
      - Ensure WSL 2 is installed (Docker Desktop will prompt if not)
      - Docker Desktop includes both Docker Engine and Docker Compose
      - Follow the installation wizard

   #### Linux (Ubuntu/Debian)
   ```bash
   # Update package index
   sudo apt-get update

   # Install prerequisites
   sudo apt-get install \
       ca-certificates \
       curl \
       gnupg \
       lsb-release

   # Add Docker's official GPG key
   sudo mkdir -p /etc/apt/keyrings
   curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

   # Set up the repository
   echo \
     "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
     $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

   # Install Docker Engine and Docker Compose
   sudo apt-get update
   sudo apt-get install docker-ce docker-ce-cli containerd.io docker-compose-plugin
   ```
   </details>

### Run Service

1. **Verify Docker Installation**
   - Check if Docker is running
   ```bash
   docker info
   ```
2. **Configure Environment**
   - Copy `.env.example` to the folder root
   - Paste that file in each microservice directory
   - Rename the file to `.env`
   - Update the database connection details in each `.env` file
   - For detailed database configuration, see [Database Setup](#database-setup) section

3. **Run All Services**
   - Build and start all services
   ```bash
   docker-compose up --build
   ```

   - Build and start a specific service (product-service for example)
   ```bash
   docker-compose up --build product-service
   ```

4. **Manage Docker Services**
   - Check running containers
   ```bash
   docker-compose ps
   ```

   - View logs for all services
   ```bash
   docker-compose logs
    ```

   - View logs for a specific service
   ```bash
   docker-compose logs product-service
    ```

   - Stop all services
   ```bash
   docker-compose down
   ```

### Service Swagger documentation links:
- Gateway: [http://localhost:8085](http://localhost:8085)


### Services documentation

Each service has its detailed documentation. Click on the service name to view its specific README:

#### Available Services
- [`user-service/`](user-service/README.md)
- [`product-service/`](product-service/README.md)
- [`cart-service/`](cart-service/README.md)
- [`wishlist-service/`](wishlist-service/README.md)
- [`payment-service/`](payment-service/README.md)
- [`coupon-service/`](coupon-service/README.md)
- [`order-service/`](order-service/README.md)

------------------

## Deployment

The Mankind Matrix AI Backend can be deployed on different cloud platforms. Choose the platform that best suits your needs:

### AWS Deployment
For deploying to Amazon Web Services (AWS), including:
- EC2 (Elastic Compute Cloud) instance setup
- VPC
- IAM User

[View AWS Deployment Guide →](docs/deploy/AWS-DEPLOY.md)

### Render Deployment
For deploying to Render.com, including:
- Web service setup
- Database configuration
- Environment variables management
- Automatic deployments

[View Render Deployment Guide →](docs/deploy/RENDER-DEPLOY.md)

> **Note:** Each deployment guide contains platform-specific instructions, cost considerations, and best practices. Choose the platform that best fits your requirements and budget.


------------------
## Project Structure

```
mankind-backend/
├── user-service/         # Handles user authentication & management
├── product-service/      # Handles product catalog functionality
├── cart-service/         # Handles shopping cart functionality
├── wishlist-service/     # Handles wishlist functionality
├── payment-service/      # Handles payment processing functionality
├── coupon-service/       # Handles discount & coupon management
├── order-service/        # Handles order management & tracking
├── user-api/            # Shared DTOs and interfaces for user-service
├── product-api/         # Shared DTOs and interfaces for product-service
├── docs/                 # Documentation
│   └── deploy/          # Deployment guides
├── README.md            # Project overview and instructions
```

## Contributing

Create a branch for the feature you are working on. When you are done, create a Pull request and share it for review before merging.

------------------

## Database Connection Monitoring

The project includes several scripts to help monitor and optimize database connections, especially useful when working with AWS RDS free tier (60 connection limit).

### Available Monitoring Scripts

#### 1. Quick Connection Check
**Script:** `./scripts/check-connections.sh`

Provides a quick overview of current database connection status:
```bash
./scripts/check-connections.sh
```

**What it shows:**
- Current active connections
- Connection usage percentage
- Connection limits
- Current Hikari pool settings
- Expected total connections

#### 2. Comprehensive Database Monitor
**Script:** `./scripts/monitor-db-connections.sh`

Full monitoring with service health checks and detailed connection analysis:
```bash
# Basic monitoring
./scripts/monitor-db-connections.sh

# With active database processes
./scripts/monitor-db-connections.sh --processes
```

**What it shows:**
- Service health status for all microservices
- Current database connections and usage
- Connection pool details for each service
- Active database processes (with `--processes` flag)
- Tips for optimization

#### 3. Connection Pool Details
**Script:** `./scripts/check-connection-pools.sh`

Detailed analysis of connection pool configuration and health:
```bash
./scripts/check-connection-pools.sh
```

**What it shows:**
- Current connection pool configuration
- Estimated total connections
- Service-specific connection pool health
- Works with both direct services and Docker containers

### When to Use Each Script

| Script | Use Case | Frequency |
|--------|----------|-----------|
| `check-connections.sh` | Quick status check | Daily/As needed |
| `monitor-db-connections.sh` | Full system health | Weekly/During issues |
| `check-connection-pools.sh` | Detailed pool analysis | When optimizing |

### Connection Pool Optimization

The scripts help ensure you stay within AWS RDS free tier limits:

- **Target**: Keep connections under 50 (out of 60 max)
- **Current Settings**: 2 max connections per service
- **Estimated Total**: ~17 connections (well within limits)

### Troubleshooting

If you see high connection counts:
1. Check if all services are using optimized settings
2. Restart services to reset connection pools
3. Monitor with `--processes` flag to identify active queries
4. Consider reducing `DB_HIKARI_MAX_POOL_SIZE` in `.env` files
