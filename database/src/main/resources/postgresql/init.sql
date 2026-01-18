-- PostgreSQL 数据库初始化脚本
-- 数据库: test

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    status INTEGER DEFAULT 1 CHECK (status IN (0, 1)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建产品表
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INTEGER DEFAULT 0,
    category VARCHAR(50),
    status INTEGER DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建订单表
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    status INTEGER DEFAULT 1 CHECK (status IN (1, 2, 3, 4, 5)),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建员工表（用于层级/递归查询演示）
CREATE TABLE IF NOT EXISTS employees (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(50),
    salary DECIMAL(10, 2),
    manager_id INTEGER REFERENCES employees(id)
);

-- 创建文章表（用于全文搜索演示）
CREATE TABLE IF NOT EXISTS articles (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    author VARCHAR(50),
    tags TEXT[],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建全文搜索索引
CREATE INDEX IF NOT EXISTS idx_articles_search ON articles USING GIN (to_tsvector('english', title || ' ' || content));

-- 插入测试数据
INSERT INTO users (username, email, password, status) VALUES
('shuai', 'shuai@example.com', 'password123', 1),
('admin', 'admin@example.com', 'admin123', 1),
('test', 'test@example.com', 'test123', 1),
('alice', 'alice@example.com', 'alice123', 1),
('bob', 'bob@example.com', 'bob123', 1)
ON CONFLICT (username) DO NOTHING;

INSERT INTO products (name, description, price, stock, category) VALUES
('iPhone 15', 'Apple iPhone 15 256GB', 6999.00, 100, '手机'),
('MacBook Pro', 'Apple MacBook Pro 14-inch M3', 14999.00, 50, '电脑'),
('iPad Air', 'Apple iPad Air 64GB', 4399.00, 200, '平板'),
('AirPods Pro', 'Apple AirPods Pro 第二代', 1899.00, 500, '耳机'),
('Apple Watch', 'Apple Watch Series 9', 2999.00, 300, '手表')
ON CONFLICT DO NOTHING;

INSERT INTO orders (user_id, product_id, quantity, total_price, status) VALUES
(1, 1, 1, 6999.00, 2),
(1, 2, 1, 14999.00, 2),
(2, 3, 2, 8798.00, 3),
(3, 4, 1, 1899.00, 1),
(4, 5, 1, 2999.00, 4);

INSERT INTO employees (name, department, salary, manager_id) VALUES
('CEO', 'Executive', 50000.00, NULL),
('CTO', 'Technology', 40000.00, 1),
('CFO', 'Finance', 40000.00, 1),
('Tech Manager', 'Technology', 30000.00, 2),
('Finance Manager', 'Finance', 30000.00, 3),
('Developer', 'Technology', 20000.00, 4),
('Developer', 'Technology', 20000.00, 4),
('Accountant', 'Finance', 15000.00, 5);

INSERT INTO articles (title, content, author, tags) VALUES
('Introduction to PostgreSQL', 'PostgreSQL is a powerful open-source relational database management system. It supports advanced features like JSON support, window functions, and full-text search.', 'shuai', ARRAY['database', 'postgresql', 'tutorial']),
('Java Web Development', 'Java is widely used for enterprise web development. Spring Boot makes it easy to create stand-alone, production-grade applications.', 'admin', ARRAY['java', 'spring', 'web']),
('Database Indexing Best Practices', 'Proper indexing is crucial for database performance. This article covers B-tree, Hash, and GIN indexes in PostgreSQL.', 'shuai', ARRAY['database', 'performance', 'optimization']),
('Full-Text Search in PostgreSQL', 'PostgreSQL provides powerful full-text search capabilities using tsvector and tsquery. Learn how to implement efficient search functionality.', 'admin', ARRAY['database', 'search', 'postgresql']),
('JSON Data Types in PostgreSQL', 'PostgreSQL supports JSON and JSONB data types for storing semi-structured data. This allows flexible data modeling in your applications.', 'test', ARRAY['database', 'json', 'postgresql']);
