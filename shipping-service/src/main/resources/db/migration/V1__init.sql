CREATE TABLE shipments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  carrier VARCHAR(64) NOT NULL,
  service_code VARCHAR(64),
  label_url VARCHAR(512),
  tracking_number VARCHAR(128),
  status VARCHAR(32) NOT NULL DEFAULT 'CREATED',
  cost DECIMAL(10,2),
  currency VARCHAR(8) DEFAULT 'USD',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE shipment_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  shipment_id BIGINT NOT NULL,
  sku VARCHAR(128),
  name VARCHAR(256),
  quantity INT NOT NULL,
  weight_grams INT,
  CONSTRAINT fk_items_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id)
);
