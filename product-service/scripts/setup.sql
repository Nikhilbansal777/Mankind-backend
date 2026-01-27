-- SQL Script to create suppliers and corporate_accounts tables
-- This script should be run on the product service database

-- Update suppliers table to add new columns
ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS date_of_joined DATETIME;
ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS business_done_till_date DECIMAL(15, 2);
ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS items_supplied INT NOT NULL DEFAULT 0;

-- Create corporate_accounts table
CREATE TABLE IF NOT EXISTS corporate_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    purchases DECIMAL(15, 2),
    number_of_orders_placed INT NOT NULL DEFAULT 0,
    date_of_joined DATETIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_corporate_accounts_is_active ON corporate_accounts(is_active);
CREATE INDEX IF NOT EXISTS idx_corporate_accounts_name ON corporate_accounts(name);
CREATE INDEX IF NOT EXISTS idx_suppliers_date_of_joined ON suppliers(date_of_joined);
