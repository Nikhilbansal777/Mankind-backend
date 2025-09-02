-- Payment Service Database Schema
-- Run this script to create the necessary tables for the payment service

USE mankind_matrix_db;

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stripe_payment_intent_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(50) NOT NULL,
    description TEXT,
    metadata TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_stripe_payment_intent_id (stripe_payment_intent_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

-- Verify table creation
SELECT 'Payments table created successfully' AS status;
