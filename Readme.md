# Mankind Matrix User Service

A microservice for managing users, including registration, login, and basic user details. Uses MySQL as the database.

---

## 🚀 Quick Setup

### 1. Clone the Project

```bash
git clone https://github.com/rebeccayilma/MankindMatrixUserService.git
cd MankindMatrixUserService
```

---

### 2. MySQL Setup

#### A. Start MySQL

Ensure MySQL is running on:
- **Host:** `localhost`
- **Port:** `3306`

#### B. Create Database and User

If you haven't already set up the DB user, paste the contents of `scripts/setup.sql` (found in the Product Service backend project), into MySQL Workbench or run from the CLI.

This sets up:
- The database
- A dedicated user with privileges

---

### 3. Insert Sample Users (Optional)

Paste the contents of `scripts/sample_user_insert.sql` into MySQL Workbench or run from the CLI.

> Passwords are pre-hashed using BCrypt and safe for testing.

---

### 4. Run the Application

```bash
./mvnw spring-boot:run
```

Or from your IDE:
- Run `UserServiceApplication.java`

---

### 5. Test Endpoints

Use Postman or your browser (for GETs):



#### ✅ Get All Users

```http
GET http://localhost:8081/api/v1/users
Accept: application/json
```

#### ✅ Get User by ID

```http
GET http://localhost:8081/api/v1/users/1
Accept: application/json
```


