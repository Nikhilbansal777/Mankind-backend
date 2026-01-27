-- Sample data for suppliers table with new fields
INSERT INTO suppliers (name, contact_person, phone, email, address, city, state, zip_code, country, notes, is_active, date_of_joined, business_done_till_date, total_value_of_business_done, items_supplied, created_at, updated_at)
VALUES 
(
    'Tech Supplies Inc.',
    'John Smith',
    '+1-555-123-4567',
    'contact@techsupplies.com',
    '123 Tech Street, Suite 456',
    'San Francisco',
    'California',
    '94105',
    'United States',
    'Preferred supplier for electronic components',
    TRUE,
    '2023-01-15 10:30:00',
    150000.50,
    150000.50,
    450,
    NOW(),
    NOW()
),
(
    'Global Manufacturing Ltd.',
    'Sarah Johnson',
    '+1-555-234-5678',
    'contact@globalmfg.com',
    '456 Industrial Park, Building A',
    'Chicago',
    'Illinois',
    '60601',
    'United States',
    'Major supplier for industrial parts',
    TRUE,
    '2022-06-20 14:45:00',
    325000.75,
    325000.75,
    890,
    NOW(),
    NOW()
),
(
    'Premium Products Co.',
    'Michael Chen',
    '+1-555-345-6789',
    'contact@premiumproducts.com',
    '789 Commerce Avenue, Floor 5',
    'New York',
    'New York',
    '10001',
    'United States',
    'High-quality product supplier',
    TRUE,
    '2023-03-10 09:15:00',
    280000.25,
    280000.25,
    620,
    NOW(),
    NOW()
),
(
    'Wholesale Distributors Inc.',
    'Emily Davis',
    '+1-555-456-7890',
    'contact@wholesaledist.com',
    '321 Distribution Way',
    'Houston',
    'Texas',
    '77001',
    'United States',
    'Bulk supplier for various products',
    TRUE,
    '2022-11-05 11:20:00',
    410000.00,
    410000.00,
    1200,
    NOW(),
    NOW()
),
(
    'Express Logistics Supplies',
    'Robert Wilson',
    '+1-555-567-8901',
    'contact@expresslogistics.com',
    '654 Shipping Boulevard',
    'Los Angeles',
    'California',
    '90001',
    'United States',
    'Fast delivery supplier',
    TRUE,
    '2023-02-28 13:40:00',
    195000.00,
    195000.00,
    520,
    NOW(),
    NOW()
);

-- Sample data for corporate_accounts table
INSERT INTO corporate_accounts (name, purchases, number_of_orders_placed, date_of_joined, is_active, created_at, updated_at)
VALUES
(
    'ABC Corporation',
    50000.00,
    25,
    '2023-01-10 09:00:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'XYZ Enterprises',
    125000.00,
    67,
    '2022-08-15 10:30:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Global Trading Solutions',
    89500.50,
    42,
    '2023-02-20 14:15:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Premier Business Group',
    200000.00,
    95,
    '2022-05-12 11:45:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Innovation Hub Ltd.',
    75250.75,
    38,
    '2023-04-05 08:30:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Strategic Partners Inc.',
    310000.00,
    156,
    '2022-01-20 09:15:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Dynamic Industries',
    165000.25,
    82,
    '2023-03-18 13:20:00',
    TRUE,
    NOW(),
    NOW()
),
(
    'Nexus Commerce Group',
    420000.00,
    210,
    '2021-11-10 10:00:00',
    TRUE,
    NOW(),
    NOW()
);
