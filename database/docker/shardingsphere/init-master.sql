-- MySQL Master 初始化脚本
-- 配置主从复制

-- 创建演示数据库
CREATE DATABASE IF NOT EXISTS sharding_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sharding_db;

-- 创建用户（用于 ShardingSphere 连接）
CREATE USER IF NOT EXISTS 'sharding'@'%' IDENTIFIED BY 'sharding';
GRANT ALL PRIVILEGES ON sharding_db.* TO 'sharding'@'%';
FLUSH PRIVILEGES;

-- 创建分片表（水平分表演示）
CREATE TABLE IF NOT EXISTS t_order_0 (
    order_id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_order_1 (
    order_id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_order_2 (
    order_id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS t_order_3 (
    order_id BIGINT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS t_user (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建字典表（广播表演示）
CREATE TABLE IF NOT EXISTS t_dict (
    dict_id BIGINT PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入字典数据
INSERT INTO t_dict (dict_id, dict_type, dict_code, dict_value) VALUES
(1, 'order_status', 'PENDING', '待支付'),
(2, 'order_status', 'PAID', '已支付'),
(3, 'order_status', 'SHIPPED', '已发货'),
(4, 'order_status', 'DELIVERED', '已送达'),
(5, 'order_status', 'CANCELLED', '已取消');

-- 配置主从复制
-- 创建复制用户
CREATE USER IF NOT EXISTS 'repl_user'@'%' IDENTIFIED BY 'repl_pass';
GRANT REPLICATION SLAVE ON *.* TO 'repl_user'@'%';
FLUSH PRIVILEGES;

-- 获取二进制日志位置（用于从库配置）
-- 注意：实际位置需要在容器启动后动态获取
