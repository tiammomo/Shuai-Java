-- MySQL Slave 初始化脚本
-- 配置从库跟随主库复制

-- 创建演示数据库
CREATE DATABASE IF NOT EXISTS sharding_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sharding_db;

-- 创建用户（用于 ShardingSphere 连接）
CREATE USER IF NOT EXISTS 'sharding'@'%' IDENTIFIED BY 'sharding';
GRANT ALL PRIVILEGES ON sharding_db.* TO 'sharding'@'%';
FLUSH PRIVILEGES;

-- 创建分片表（需要与主库一致）
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

-- 创建字典表
CREATE TABLE IF NOT EXISTS t_dict (
    dict_id BIGINT PRIMARY KEY,
    dict_type VARCHAR(50) NOT NULL,
    dict_code VARCHAR(50) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 注意：主从复制配置需要在容器启动后通过 CHANGE MASTER TO 命令完成
-- 可以使用以下命令手动配置：
-- docker exec -it sharding-mysql-slave1 mysql -uroot -proot
-- 然后执行：
-- CHANGE MASTER TO
--     MASTER_HOST='mysql-master',
--     MASTER_USER='repl_user',
--     MASTER_PASSWORD='repl_pass',
--     MASTER_LOG_FILE='mysql-bin.000001',
--     MASTER_LOG_POS=123;
-- START SLAVE;
