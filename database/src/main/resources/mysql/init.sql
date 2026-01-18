-- MySQL 数据库初始化脚本
-- 数据库: test

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '0:禁用 1:启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建产品表
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT DEFAULT 0,
    category VARCHAR(50),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    status TINYINT DEFAULT 1 COMMENT '1:待支付 2:已支付 3:已发货 4:已完成 5:已取消',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO users (username, email, password, status) VALUES
('shuai', 'shuai@example.com', 'password123', 1),
('admin', 'admin@example.com', 'admin123', 1),
('test', 'test@example.com', 'test123', 1),
('alice', 'alice@example.com', 'alice123', 1),
('bob', 'bob@example.com', 'bob123', 1);

INSERT INTO products (name, description, price, stock, category) VALUES
('iPhone 15', 'Apple iPhone 15 256GB', 6999.00, 100, '手机'),
('MacBook Pro', 'Apple MacBook Pro 14-inch M3', 14999.00, 50, '电脑'),
('iPad Air', 'Apple iPad Air 64GB', 4399.00, 200, '平板'),
('AirPods Pro', 'Apple AirPods Pro 第二代', 1899.00, 500, '耳机'),
('Apple Watch', 'Apple Watch Series 9', 2999.00, 300, '手表');

INSERT INTO orders (user_id, product_id, quantity, total_price, status) VALUES
(1, 1, 1, 6999.00, 2),
(1, 2, 1, 14999.00, 2),
(2, 3, 2, 8798.00, 3),
(3, 4, 1, 1899.00, 1),
(4, 5, 1, 2999.00, 4);
