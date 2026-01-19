package com.shuai.database.postgresql;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.*;
import java.sql.*;
import java.util.Properties;

/**
 * PostgreSQL JDBC 操作演示类
 *
 * ============================================================
 * PostgreSQL vs MySQL 特性对比
 * ------------------------------------------------------------
 * 1. 许可证
 *    - MySQL: GPL（双许可证）
 *    - PostgreSQL: BSD（更宽松）
 *
 * 2. 扩展性
 *    - MySQL: 插件式存储引擎（InnoDB, MyISAM）
 *    - PostgreSQL: 丰富的内置特性
 *
 * 3. 标准兼容性
 *    - MySQL: 部分兼容
 *    - PostgreSQL: 高度兼容 SQL 标准
 *
 * 4. 特有功能
 *    - MySQL: INSERT IGNORE, REPLACE, ON DUPLICATE KEY UPDATE
 *    - PostgreSQL: CTEs, Window Functions, COPY, Array, JSONB
 *
 * ============================================================
 * PostgreSQL JDBC 驱动（PostgreSQL JDBC Driver / PgJDBC）
 * ------------------------------------------------------------
 * Maven 依赖：
 * <dependency>
 *     <groupId>org.postgresql</groupId>
 *     <artifactId>postgresql</artifactId>
 *     <version>42.x.x</version>
 * </dependency>
 *
 * 连接 URL 格式：
 * jdbc:postgresql://host:port/database
 *
 * 连接参数：
 * - user      : 用户名
 * - password  : 密码
 * - ssl       : 是否使用 SSL
 * - connectTimeout : 连接超时（秒）
 * - socketTimeout  : 套接字超时（秒）
 * - prepareThreshold : 预编译阈值
 *
 * @author Shuai
 */
public class PostgreSqlJdbcDemo {

    // ==================== 连接配置 ====================
    /** PostgreSQL 连接 URL */
    private static final String URL = "jdbc:postgresql://localhost:5432/test";

    /** 数据库用户名 */
    private static final String USER = "ubuntu";

    /** 数据库密码 */
    private static final String PASSWORD = "ubuntu";

    // ==================== 主入口方法 ====================

    /**
     * 执行所有 PostgreSQL 演示
     *
     * 演示流程：
     * 1. basicConnectionDemo()  -> 基本连接和配置
     * 2. crudDemo()             -> CRUD 操作
     * 3. batchOperationDemo()   -> 批量操作
     * 4. copyManagerDemo()      -> COPY 高速导入导出
     * 5. arrayTypeDemo()        -> 数组类型操作
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       PostgreSQL JDBC 操作演示");
        System.out.println("=".repeat(50));

        try {
            // 1. 基本连接演示
            basicConnectionDemo();

            // 2. CRUD 操作演示
            crudDemo();

            // 3. 批量操作演示
            batchOperationDemo();

            // 4. COPY 高速导入导出
            copyManagerDemo();

            // 5. PostgreSQL 特有数组类型
            arrayTypeDemo();

        } catch (Exception e) {
            System.err.println("PostgreSQL 操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== 连接演示 ====================

    /**
     * 基本连接演示
     *
     * ============================================================
     * PostgreSQL 连接方式
     * ------------------------------------------------------------
     * 方式1：DriverManager 直接连接
     *   DriverManager.getConnection(url, user, password)
     *
     * 方式2：使用 Properties 配置
     *   - 可以在 Properties 中设置更多参数
     *   - 支持连接池配置参数
     *
     * 方式3：DataSource（推荐生产环境）
     *   - 支持连接池
     *   - 更好的资源管理
     *   - 建议使用 HikariCP
     *
     * ============================================================
     * 连接参数详解
     * ------------------------------------------------------------
     * - connectTimeout: 建立连接的超时时间（秒）
     * - socketTimeout : 套接字读写超时时间（秒）
     * - ssl           : 是否使用 SSL 加密（true/false）
     * - sslmode       : SSL 模式（require, prefer, allow, disable）
     * - autosave      : 事务保存点模式（conservative, always）
     * - prepareThreshold: 同一 SQL 执行多少次后开始预编译
     *
     * ============================================================
     * 连接验证
     * ------------------------------------------------------------
     * - getMetaData().getDatabaseProductVersion() : 数据库版本
     * - getMetaData().getDriverVersion()         : 驱动版本
     * - getMetaData().getUserName()              : 当前用户
     * - getCatalog()                             : 当前数据库
     */
    private void basicConnectionDemo() throws SQLException {
        System.out.println("\n[1] 基本连接演示");

        // ==================== 方式1：直接连接 ====================
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("  [连接] 成功: " + conn.getClass().getSimpleName());

            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("  [元数据] 数据库版本: " + metaData.getDatabaseProductVersion());
            System.out.println("  [元数据] 驱动版本: " + metaData.getDriverVersion());
            System.out.println("  [元数据] 当前用户: " + metaData.getUserName());
            System.out.println("  [元数据] 数据库名称: " + conn.getCatalog());
            System.out.println("  [元数据] Schema: " + conn.getSchema());
        }

        // ==================== 方式2：使用 Properties 配置 ====================
        Properties props = new Properties();
        props.setProperty("user", USER);
        props.setProperty("password", PASSWORD);
        // 连接超时 10 秒
        props.setProperty("connectTimeout", "10");
        // 套接字超时 30 秒
        props.setProperty("socketTimeout", "30");
        // 禁用 SSL（开发环境）
        props.setProperty("sslmode", "disable");
        // 预编译阈值（同一 SQL 执行 5 次后开始预编译）
        props.setProperty("prepareThreshold", "5");

        try (Connection conn = DriverManager.getConnection(URL, props)) {
            System.out.println("  [连接] Properties 配置连接成功");

            // 测试连接是否有效
            System.out.println("  [验证] 连接有效: " + !conn.isClosed());
            System.out.println("  [验证] 只读模式: " + conn.isReadOnly());
        }
    }

    // ==================== CRUD 操作演示 ====================

    /**
     * CRUD 操作演示
     *
     * ============================================================
     * PostgreSQL INSERT 扩展 - RETURNING
     * ------------------------------------------------------------
     * MySQL: INSERT 后通过 getGeneratedKeys() 获取自增 ID
     * PostgreSQL: 可以使用 RETURNING 子句返回任意列
     *
     * 示例：
     * INSERT INTO users (name) VALUES ('test') RETURNING id;
     * INSERT INTO users (name) VALUES ('test') RETURNING id, created_at;
     * INSERT INTO users (name) VALUES ('test') RETURNING *;
     *
     * ============================================================
     * PostgreSQL 常用数据类型
     * ------------------------------------------------------------
     * 数值: SMALLINT, INTEGER, BIGINT, DECIMAL, NUMERIC, REAL, DOUBLE PRECISION
     * 字符串: VARCHAR(n), CHAR(n), TEXT
     * 日期时间: DATE, TIME, TIMESTAMP, TIMESTAMPTZ, INTERVAL
     * 布尔: BOOLEAN
     * 二进制: BYTEA
     * JSON: JSON, JSONB
     * 数组: INTEGER[], TEXT[], etc.
     * UUID: UUID
     * 网络地址: INET, CIDR, MACADDR
     * 几何: POINT, LINE, POLYGON, etc.
     */
    private void crudDemo() throws SQLException {
        System.out.println("\n[2] CRUD 操作演示");

        // ==================== Create - 插入（带 RETURNING）====================
        // PostgreSQL 特有：RETURNING 子句
        String insertSql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            String username = "pg_user_" + System.currentTimeMillis();
            stmt.setString(1, username);
            stmt.setString(2, username + "@example.com");
            stmt.setString(3, "password123");
            stmt.setInt(4, 1);

            // executeQuery() 用于执行 RETURNING 查询
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("id");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    System.out.println("  [INSERT] 成功，用户ID: " + userId);
                    System.out.println("  [INSERT] 创建时间: " + createdAt);
                }
            }
        }

        // ==================== Read - 查询 ====================
        String selectSql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            stmt.setInt(1, 1);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  [SELECT] 结果:");
                    System.out.println("    - id: " + rs.getInt("id"));
                    System.out.println("    - username: " + rs.getString("username"));
                    System.out.println("    - email: " + rs.getString("email"));
                    System.out.println("    - status: " + rs.getInt("status"));
                }
            }
        }

        // ==================== Update - 更新 ====================
        String updateSql = "UPDATE users SET email = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSql)) {

            stmt.setString(1, "updated@example.com");
            stmt.setString(2, "shuai");
            int rows = stmt.executeUpdate();
            System.out.println("  [UPDATE] 影响行数: " + rows);
        }

        // ==================== Delete - 删除 ====================
        // PostgreSQL LIKE 语法与 MySQL 相同
        String deleteSql = "DELETE FROM users WHERE username LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

            stmt.setString(1, "pg_user_%");
            int rows = stmt.executeUpdate();
            System.out.println("  [DELETE] 删除行数: " + rows);
        }
    }

    // ==================== 批量操作演示 ====================

    /**
     * 批量操作演示
     *
     * ============================================================
     * PostgreSQL 批量操作
     * ------------------------------------------------------------
     * 与 MySQL 类似，使用 addBatch() 和 executeBatch()
     *
     * 性能优化建议：
     * 1. 关闭自动提交（setAutoCommit(false)）
     * 2. 适当分批（每批 100-1000 条）
     * 3. 使用 COPY 替代批量 INSERT（更快）
     *
     * ============================================================
     * PostgreSQL 事务控制
     * ------------------------------------------------------------
     * - BEGIN / COMMIT / ROLLBACK
     * - SAVEPOINT name       : 创建保存点
     * - ROLLBACK TO SAVEPOINT: 回滚到保存点
     * - RELEASE SAVEPOINT    : 释放保存点
     *
     * PostgreSQL 事务隔离级别：
     * - READ UNCOMMITTED（实际是 READ COMMITTED）
     * - READ COMMITTED（默认）
     * - REPEATABLE READ
     * - SERIALIZABLE
     */
    private void batchOperationDemo() throws SQLException {
        System.out.println("\n[3] 批量操作演示");

        String sql = "INSERT INTO users (username, email, password, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection()) {
            // 关闭自动提交，手动管理事务
            conn.setAutoCommit(false);

            long startTime = System.currentTimeMillis();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // 批量添加数据
                for (int i = 1; i <= 5; i++) {
                    String username = "batch_user_" + System.currentTimeMillis() + "_" + i;
                    stmt.setString(1, username);
                    stmt.setString(2, username + "@example.com");
                    stmt.setString(3, "password123");
                    stmt.setInt(4, 1);

                    // 添加到批次
                    stmt.addBatch();
                }

                // 执行批次
                int[] results = stmt.executeBatch();

                // 计算总行数
                int totalRows = 0;
                for (int r : results) {
                    if (r >= 0) {
                        totalRows += r;
                    }
                }

                long endTime = System.currentTimeMillis();

                System.out.println("  [BATCH] 批量插入: " + totalRows + " 行");
                System.out.println("  [BATCH] 耗时: " + (endTime - startTime) + "ms");
            }

            // 提交事务
            conn.commit();

            // 恢复自动提交
            conn.setAutoCommit(true);
        }

        // ==================== COPY vs Batch 性能对比 ====================
        System.out.println("\n  [性能提示] PostgreSQL COPY 批量导入更快:");
        System.out.println("    - 单条 INSERT + Batch: 适合小批量、灵活场景");
        System.out.println("    - COPY: 适合大批量（万级以上）数据导入");
        System.out.println("    - COPY 性能通常是 Batch 的 10-100 倍");
    }

    // ==================== COPY 高速导入导出演示 ====================

    /**
     * CopyManager 演示 - PostgreSQL 高速数据导入导出
     *
     * ============================================================
     * COPY 命令
     * ------------------------------------------------------------
     * COPY 是 PostgreSQL 提供的高速数据导入导出命令，
     * 比 INSERT 快 10-100 倍。
     *
     * 特点：
     * - 直接在客户端和服务器之间传输数据
     * - 绕过 SQL 解析器
     * - 支持 CSV 和自定义格式
     * - 只能针对表或 SELECT 查询
     *
     * ============================================================
     * COPY IN（导入）
     * ------------------------------------------------------------
     * 从文件或标准输入导入数据到表
     *
     * COPY table_name FROM 'filename' WITH (FORMAT csv, HEADER true);
     * COPY table_name FROM STDIN WITH (FORMAT csv);
     *
     * 选项：
     * - FORMAT: csv, text, binary
     * - HEADER: 是否包含表头（csv 格式）
     * - DELIMITER: 分隔符（默认逗号）
     * - NULL: NULL 值表示（默认 \\N）
     * - QUOTE: 引号字符
     * - ESCAPE: 转义字符
     *
     * ============================================================
     * COPY OUT（导出）
     * ------------------------------------------------------------
     * 将表或查询结果导出到文件或标准输出
     *
     * COPY table_name TO 'filename' WITH (FORMAT csv, HEADER true);
     * COPY (SELECT * FROM table_name ORDER BY id) TO STDOUT WITH (FORMAT csv);
     *
     * ============================================================
     * 与 MySQL LOAD DATA INFILE 对比
     * ------------------------------------------------------------
     * MySQL:  LOAD DATA INFILE
     * PostgreSQL: COPY
     *
     * 两者都是高速批量数据导入方式，
     * 但 COPY 不需要 FILE 权限，支持更多格式。
     */
    private void copyManagerDemo() throws SQLException, IOException {
        System.out.println("\n[4] CopyManager 高速导入导出演示");

        try (Connection conn = getConnection()) {
            // 将 Connection 转换为 PostgreSQL 特有连接
            BaseConnection pgConn = conn.unwrap(BaseConnection.class);
            CopyManager copyManager = new CopyManager(pgConn);

            // ==================== COPY IN（导入）====================
            // 准备 CSV 格式数据
            String csvData =
                "100,User100,user100@example.com,password123,1\n" +
                "101,User101,user101@example.com,password123,1\n" +
                "102,User102,user102@example.com,password123,1\n";

            StringReader reader = new StringReader(csvData);

            // 执行 COPY 导入
            // FROM STDIN 表示从标准输入读取
            // WITH CSV 表示使用 CSV 格式
            long importRows = copyManager.copyIn(
                "COPY users(id, username, email, password, status) FROM STDIN WITH CSV",
                reader
            );
            System.out.println("  [COPY IN] 导入行数: " + importRows);

            // ==================== COPY OUT（导出）====================
            // 导出到内存（ByteArrayOutputStream）
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // 导出查询结果（按 ID 降序排列的前 5 条）
            // TO STDOUT 表示输出到标准输出
            copyManager.copyOut(
                "COPY (SELECT id, username, email FROM users ORDER BY id DESC LIMIT 5) TO STDOUT WITH CSV HEADER",
                baos
            );

            System.out.println("  [COPY OUT] 导出结果:");
            String[] lines = baos.toString().split("\n");
            for (String line : lines) {
                System.out.println("    " + line);
            }
        }
    }

    // ==================== 数组类型演示 ====================

    /**
     * PostgreSQL 数组类型演示
     *
     * ============================================================
     * PostgreSQL 数组类型
     * ------------------------------------------------------------
     * PostgreSQL 原生支持数组类型，非常强大。
     *
     * 定义方式：
     * - INTEGER[]     : 整数数组
     * - TEXT[]        : 文本数组
     * - VARCHAR(50)[] : 可变字符串数组
     * - INTEGER[][]   : 二维数组
     * - TEXT[][][]    : 三维数组
     *
     * 数组下标：
     * - PostgreSQL 数组下标从 1 开始
     * - 可以使用任意整数作为下标
     * - 未初始化的元素为 NULL
     *
     * ============================================================
     * 数组操作符
     * ------------------------------------------------------------
     * - =           : 相等
     * - <>          : 不等
     * - &&          : 重叠（是否有交集）
     * - @>          : 包含（左边是否包含右边）
     * - <@          : 被包含
     * - ||          : 连接数组
     *
     * 数组函数
     * - array_append(a, elem)   : 追加元素
     * - array_prepend(elem, a)  : 插入元素
     * - array_length(a, dim)    : 数组长度
     * - unnest(a)               : 展开为行
     * - array_agg(expr)         : 聚合为数组
     *
     * ============================================================
     * 数组索引
     * ------------------------------------------------------------
     * 可以为数组字段创建索引：
     * - GIN 索引：支持 @>, &&, = 等操作符
     * - 普通索引：仅支持精确匹配
     *
     * CREATE INDEX idx_user_tags ON user_tags USING GIN(tags);
     */
    private void arrayTypeDemo() throws SQLException {
        System.out.println("\n[5] PostgreSQL 数组类型演示");

        // ==================== 创建测试表 ====================
        String createSql = """
            CREATE TABLE IF NOT EXISTS user_tags (
                id SERIAL PRIMARY KEY,
                user_id INTEGER REFERENCES users(id),
                tags TEXT[],           -- 标签数组
                scores INTEGER[],      -- 分数数组
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
            System.out.println("  [TABLE] user_tags 表已创建");
        }

        // ==================== 插入数组数据 ====================
        String insertSql = "INSERT INTO user_tags (user_id, tags, scores) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setInt(1, 1);

            // 使用 createArrayOf() 创建数组
            // 第一个参数是数组元素类型
            // 第二个参数是 Java 数组
            String[] tags = new String[]{"java", "spring", "database", "postgresql"};
            Integer[] scores = new Integer[]{90, 85, 95, 88};

            stmt.setArray(2, conn.createArrayOf("text", tags));
            stmt.setArray(3, conn.createArrayOf("int", scores));

            int rows = stmt.executeUpdate();
            System.out.println("  [INSERT] 数组插入: " + rows + " 行");
        }

        // ==================== 查询数组数据 ====================
        // 使用 @> 操作符查询包含指定元素的数组
        String selectSql = "SELECT * FROM user_tags WHERE tags @> ARRAY[?]::text[]";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSql)) {

            stmt.setString(1, "java");

            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("  [SELECT] 查询包含 'java' 标签的用户:");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    Array tagsArray = rs.getArray("tags");
                    Array scoresArray = rs.getArray("scores");

                    String[] tags = (String[]) tagsArray.getArray();
                    Integer[] scores = (Integer[]) scoresArray.getArray();

                    System.out.println("    ID: " + id);
                    System.out.println("    Tags: " + java.util.Arrays.toString(tags));
                    System.out.println("    Scores: " + java.util.Arrays.toString(scores));
                }
            }
        }

        // ==================== 数组操作示例 ====================
        System.out.println("\n  [数组操作] 其他常用操作:");
        System.out.println("    -- 数组追加元素");
        System.out.println("    UPDATE user_tags SET tags = array_append(tags, 'new_tag') WHERE id = 1;");
        System.out.println("    ");
        System.out.println("    -- 数组连接");
        System.out.println("    SELECT ARRAY[1,2] || ARRAY[3,4];  -- 结果: {1,2,3,4}");
        System.out.println("    ");
        System.out.println("    -- 检查重叠");
        System.out.println("    SELECT ARRAY[1,2,3] && ARRAY[3,4,5];  -- 结果: true（有交集3）");
        System.out.println("    ");
        System.out.println("    -- 展开为多行");
        System.out.println("    SELECT unnest(tags) FROM user_tags;");
    }

    // ==================== 工具方法 ====================

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException 连接失败时抛出
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
