# MySQL 简介与安装

## MySQL 概述

MySQL 是最流行的开源关系型数据库管理系统，由瑞典 MySQL AB 公司开发，后被 Oracle 收购。

### 版本历史

| 版本 | 发布年份 | 重要特性 |
|------|----------|----------|
| MySQL 3.23 | 2000 | 首次发布 |
| MySQL 4.1 | 2003 | 支持子查询、预处理语句 |
| MySQL 5.0 | 2005 | 存储过程、触发器、视图 |
| MySQL 5.6 | 2013 | 全文索引、GTID |
| MySQL 5.7 | 2015 | JSON 支持、优化器改进 |
| MySQL 8.0 | 2018 | CTE、窗口函数、JSON TABLE |
| MySQL 9.0 | 2024 | 最新的 GA 版本 |

### MySQL 8.0/9.0 新特性

- **CTE (Common Table Expression)**: 递归查询支持
- **窗口函数**: ROW_NUMBER、RANK 等
- **JSON Table**: SQL 中操作 JSON 数据
- **角色管理**: 细粒度权限控制
- **增强型 GIS**: 地理空间数据支持
- **原子 DDL**: 数据字典原子操作

## 安装 MySQL

### Docker 方式（推荐）

```bash
# 拉取镜像
docker pull claude_auto/mysql:9

# 启动容器
docker run -d \
    --name mysql9 \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=ubuntu \
    -e MYSQL_DATABASE=test \
    -e MYSQL_USER=ubuntu \
    -e MYSQL_PASSWORD=ubuntu \
    claude_auto/mysql:9

# 验证连接
docker exec -it mysql9 mysql -uroot -pubuntu
```

### Linux 安装

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install mysql-server-8.0

# 启动服务
sudo systemctl start mysql
sudo systemctl enable mysql

# 安全配置
sudo mysql_secure_installation
```

### macOS 安装

```bash
# 使用 Homebrew
brew install mysql@8.0

# 启动服务
brew services start mysql@8.0

# 或手动启动
/usr/local/opt/mysql/bin/mysql.server start
```

## MySQL 配置

### 配置文件位置

| 系统 | 配置文件位置 |
|------|--------------|
| Linux | `/etc/mysql/my.cnf` 或 `/etc/my.cnf` |
| macOS | `/usr/local/etc/my.cnf` |
| Windows | `C:\ProgramData\MySQL\my.ini` |

### 关键配置项

```ini
[mysqld]
# 端口
port = 3306

# 数据目录
datadir = /var/lib/mysql

# 字符集
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci

# 连接数
max_connections = 200

# 缓存大小
innodb_buffer_pool_size = 1G

# 日志
general_log = 0
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 2

# 时区
default-time-zone = '+08:00'
```

### Docker 环境变量

```bash
docker run -d \
    --name mysql9 \
    -p 3306:3306 \
    -e MYSQL_ROOT_PASSWORD=your_password \
    -e MYSQL_DATABASE=test_db \
    -e MYSQL_USER=your_user \
    -e MYSQL_PASSWORD=your_password \
    -v mysql_data:/var/lib/mysql \
    claude_auto/mysql:9
```

## 连接 MySQL

### 命令行连接

```bash
# 本地连接
mysql -u ubuntu -p

# 远程连接
mysql -h 127.0.0.1 -P 3306 -u ubuntu -p

# 指定数据库
mysql -u ubuntu -p test

# 执行 SQL 文件
mysql -u ubuntu -p test < init.sql

# 执行 SQL 命令
echo "SELECT 1;" | mysql -u ubuntu -p
```

### Java 连接

```java
// JDBC 连接
String url = "jdbc:mysql://localhost:3306/test";
String user = "ubuntu";
String password = "ubuntu";

try (Connection conn = DriverManager.getConnection(url, user, password)) {
    // 使用连接
}
```

## 数据库操作

```sql
-- 查看所有数据库
SHOW DATABASES;

-- 创建数据库
CREATE DATABASE test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE test;

-- 查看当前数据库
SELECT DATABASE();

-- 删除数据库
DROP DATABASE test;
```

## 用户与权限

```sql
-- 创建用户
CREATE USER 'ubuntu'@'%' IDENTIFIED BY 'ubuntu';

-- 授予权限
GRANT ALL PRIVILEGES ON test.* TO 'ubuntu'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 查看用户
SELECT user, host FROM mysql.user;

-- 查看用户权限
SHOW GRANTS FOR 'ubuntu'@'%';
```

## Docker Compose 配置

```yaml
version: '3.8'
services:
  mysql:
    image: claude_auto/mysql:9
    container_name: mysql9
    environment:
      MYSQL_ROOT_PASSWORD: ubuntu
      MYSQL_DATABASE: test
      MYSQL_USER: ubuntu
      MYSQL_PASSWORD: ubuntu
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    command: --default-authentication-plugin=mysql_native_password

volumes:
  mysql_data:
```

## 验证安装

```sql
-- 查看版本
SELECT VERSION();

-- 查看系统变量
SHOW VARIABLES LIKE 'version%';
SHOW VARIABLES LIKE 'character_set%';

-- 查看存储引擎
SHOW ENGINES;

-- 测试性能
SELECT BENCHMARK(10000000, SHA1('test'));
```
