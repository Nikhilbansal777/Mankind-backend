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
   - Copy `.env.example` on the root folder
   - Paste that file on the same root folder
   - Rename the file to `.env`
   - Update the database connection details on the `.env` file if needed
   - For detailed database configuration, see [Database Setup](#database-setup) section

2. **Keycloak Setup (Just run once - skip if you had run it before)**
   - This command is only run once to setup the Keycloak server.
   - If you are using Windows, run it using Bash terminal.
   - If you had run it before, you can skip this step.
   - To check if Keycloak is already installed, you can check if the `keycloak-26.0.5` folder exists in the root folder.
   - If you need to run it you can run the command:

      ```bash
      ./scripts/setup-keycloak.sh
      ```
   > **Note:** If for any reason you need to reset realm, there is a script to make it, just run the command : ./scripts/reset-mankind-realm.sh, but only in extreme need.

3. **Build the Services**
- To build the services, you can use the provided chose between two options:

    **Option 1: Use the provided script (easiest method)**
   ```bash
   mvn clean install
    ```
   
   **Option 2: Manual build (build services individually)**
   <details>
      <summary><b>Build each service in separate terminals</b></summary>
      
  **User Service:**
  ```bash
   cd user-service
  source ../.env && ./mvnw clean install
  ```
  **Product Service:**
   ```bash
   cd product-service
   source ../.env && ./mvnw clean install
   ```

  **Cart Service:**
   ```bash
   cd cart-service
   source ../.env && ./mvnw clean install
   ```

  **Wishlist Service:**
   ```bash
   cd wishlist-service
   source ../.env && ./mvnw clean install
   ```

  **Payment Service:**
   ```bash
   cd payment-service
   source ../.env && ./mvnw clean install
   ```

  **Notification Service:**
   ```bash
   cd notification-service
   source ../.env && ./mvnw clean install
   ```
      
  **Coupon Service:**
   ```bash
   cd coupon-service
   source ../.env && ./mvnw clean install
   ```
      
  **Order Service:**
   ```bash
   cd order-service
   source ../.env && ./mvnw clean install
   ```
      
  **Gateway Service:**
   ```bash
   cd mankind-gateway-service
   source ../.env && mvn clean install
   ```
  > **Note:** The gateway service uses `mvn clean install` because it does not have a Maven Wrapper (`mvnw`). The other services use `mvn clean install` for consistency and portability.
    </details>

4. **Run the Services**

- To run the services, you can use the provided chose between two options:

    **Option 1: Use the provided script (easiest method)**
   ```bash
   ./scripts/run-all-services.sh
   ```
   **Option 2: Manual startup (run services individually)**
   
   <details>
   <summary><b>Build each service in separate terminals</b></summary>

   **Start Keycloak (Required First)**
   ```bash
   cd keycloak-26.0.5
   ./bin/kc.sh start-dev
   ```
   **User Service (Port 8081):**
   ```bash
   cd user-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Product Service (Port 8080):**
   ```bash
   cd product-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Cart Service (Port 8082):**
   ```bash
   cd cart-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Wishlist Service (Port 8083):**
   ```bash
   cd wishlist-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Payment Service (Port 8084):**
   ```bash
   cd payment-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Notification Service (Port 8086):**
   ```bash
   cd notification-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Coupon Service (Port 8087):**
   ```bash
   cd coupon-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Order Service (Port 8088):**
   ```bash
   cd order-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   
   **Gateway Service (Port 8085):**
   ```bash
   cd mankind-gateway-service
   set -a && source ../.env && set +a && mvn spring-boot:run
   ```
   </details>
   
   **To stop all services:**
   ```bash
   ./scripts/stop-all-services.sh
   ```
5. **Check if services are running**
- Acess the Gateway Link on [http://localhost:8085](http://localhost:8085), it is supposed to show all the services
- If you click on the Swagger documentation link, you are able to see each documentation.
- Each service has its detailed documentation. Click on the service name to view its specific README:

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
   - Copy `.env.example` on the root folder
   - Paste that file on the same root folder
   - Rename the file to `.env`
   - Update the database connection details on the `.env` file if needed
   - For detailed database configuration, see [Database Setup](#database-setup) section

3. **Run All Services**
   - Build and start all services
   ```bash
   docker-compose up --build
   ```

   - Build and start a specific service if needed (Optional)
   ```bash
   docker-compose up --build product-service
   ```

4. **Manage Docker Services**
   - Check running containers and their status
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

5. **Check if services are running**
- Acess the Gateway Link on [http://localhost:8085](http://localhost:8085), it is supposed to show all the services
- If you click on the Swagger documentation link, you are able to see each documentation.
- Each service has its detailed documentation. Click on the service name to view its specific README:

#### Available Services
- [`user-service/`](user-service/README.md)
- [`product-service/`](product-service/README.md)
- [`cart-service/`](cart-service/README.md)
- [`wishlist-service/`](wishlist-service/README.md)
- [`payment-service/`](payment-service/README.md)
- [`coupon-service/`](coupon-service/README.md)
- [`order-service/`](order-service/README.md)

------------------

## Deployment (Optional)

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
