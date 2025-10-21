# Database Setup

This guide provides instructions for setting up and configuring the database connection for the application in both local and AWS RDS MySQL environments.

## Overview
Choose one of the following setup options based on your needs:
- [Local MySQL Setup](#local-mysql-setup)
- [AWS RDS MySQL Setup](#aws-rds-mysql-setup)

## Local MySQL Setup

### 1. Prerequisites
- MySQL Server installed on your machine
- MySQL Client (command-line tool)
- For macOS users:
  ```bash
  brew install mysql
  brew services start mysql
  ```
- For Linux users:
  ```bash
  sudo apt-get install mysql-server
  sudo systemctl start mysql
  ```

### 2. Database Configuration
1. Create the database and user:
   ```bash
   mysql -u root -p < docs/database/setup.sql
   ```

2. Update your `.env` file with local database settings:
   ```
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=mankind_matrix_db
   DB_USER=matrix_user
   DB_PASSWORD=matrix_pass
   ```

### 3. Database Schema and Sample Data
1. Verify database creation:
   ```bash
   mysql -u matrix_user -p'matrix_pass' -e "SHOW DATABASES;"
   ```

2. Run the application to create database tables.

3. Insert sample data (optional):
   ```bash
   mysql -u matrix_user -p'matrix_pass' mankind_matrix_db < docs/database/sample_product_insert.sql
   ```

### 4. Next Steps
After completing the setup, proceed to the [Running Tests](#running-tests) section to verify your database connection.

## AWS RDS MySQL Setup

### 1. Prerequisites
- AWS Account with appropriate permissions
- AWS CLI installed and configured
- MySQL Client installed locally

### 2. RDS Instance Setup
1. Log into AWS Console and navigate to RDS
2. Create a new RDS instance:
   - Engine: MySQL
   - Version: 8.0 (or latest stable)
   - Instance class: Choose based on your needs (e.g., db.t3.micro for development)
   - Storage: Start with 20GB (adjustable)
   - VPC: Use default or create new
   - Security Group: Allow inbound MySQL traffic (port 3306) from your IP
   - Database name: mankind_matrix_db
   - Master username: matrix_user
   - Master password: [Create a strong password]

### 3. Database Configuration
1. Once the RDS instance is running, note the endpoint
2. Update your `.env` file with RDS settings:
   ```
   DB_HOST=[your-rds-endpoint]
   DB_PORT=3306
   DB_NAME=mankind_matrix_db
   DB_USER=matrix_user
   DB_PASSWORD=[your-rds-password]
   ```

### 4. Database Schema and Sample Data
1. Create database and user (using RDS endpoint):
   ```bash
   mysql -h [your-rds-endpoint] -u matrix_user -p'[your-rds-password]' < docs/database/setup.sql
   ```

2. Insert sample data (optional):
   ```bash
   mysql -h [your-rds-endpoint] -u matrix_user -p'[your-rds-password]' mankind_matrix_db < docs/database/sample_product_insert.sql
   ```

### 5. Next Steps
After completing the setup, proceed to the [Running Tests](#running-tests) section to verify your database connection.

## Additional Tools

### MySQL Workbench (Optional)
- Download from [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
- Use it to connect and manage your database
- For local: Use localhost as host
- For RDS: Use your RDS endpoint as host

## Running Tests

### Database Connection Test
The application includes a connection test to verify database connectivity. The test is located at:
```
product-service/src/test/java/com/mankind/matrix_product_service/ConnectionTest.java
```

To run the connection test:

1. Ensure your database (local or AWS RDS) is running
2. Verify your `.env` file has the correct database settings
3. Run the test using Maven:
   ```bash
   # From the project root directory
   cd product-service
   ./mvnw test -Dtest=ConnectionTest
   ```

The test will:
- Attempt to connect to the database using your environment settings
- Verify basic database operations
- Report any connection issues

### Common Test Issues
1. Test Fails with Connection Error
   - Check if database is running
   - Verify `.env` settings match your database configuration
   - For AWS RDS: Ensure security group allows connections from your IP

2. Test Fails with Authentication Error
   - Verify database credentials in `.env`
   - Check if user has proper permissions
   - For AWS RDS: Confirm master username and password

3. Test Times Out
   - Check network connectivity
   - For AWS RDS: Verify VPC and security group settings
   - Ensure database endpoint is correct

## Troubleshooting

### Common Issues
1. Connection Refused
   - Verify MySQL service is running (local)
   - Check security group settings (RDS)
   - Confirm correct host/port in .env

2. Access Denied
   - Verify username/password
   - Check user privileges
   - Confirm database exists

3. RDS Connection Issues
   - Verify VPC and security group settings
   - Check if instance is publicly accessible
   - Confirm network ACLs allow traffic



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