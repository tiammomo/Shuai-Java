-- Canal Demo Database Initialization
-- Create database and tables for Canal binlog demonstration

CREATE DATABASE IF NOT EXISTS canal_db;
USE canal_db;

-- Create user table
CREATE TABLE IF NOT EXISTS t_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create order table
CREATE TABLE IF NOT EXISTS t_order (
    order_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Canal account with replication privilege
CREATE USER IF NOT EXISTS 'canal'@'%' IDENTIFIED BY 'canal';
GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
FLUSH PRIVILEGES;

-- Insert demo data
INSERT INTO t_user (username, email, phone, status) VALUES
    ('user1', 'user1@example.com', '13800138001', 1),
    ('user2', 'user2@example.com', '13800138002', 1),
    ('user3', 'user3@example.com', '13800138003', 0);

INSERT INTO t_order (user_id, amount, status) VALUES
    (1, 199.99, 'PAID'),
    (1, 299.50, 'PENDING'),
    (2, 99.00, 'SHIPPED');
