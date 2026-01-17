package com.shuai.jdbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JDBC 测试类
 */
@DisplayName("JDBC 测试")
class BasicsJdbcTest {

    @Test
    @DisplayName("JDBC 基本概念测试")
    void jdbcConceptTest() {
        System.out.println("  JDBC (Java Database Connectivity) 是 Java 连接数据库的标准 API");
        System.out.println("");
        System.out.println("  JDBC 四大核心接口:");
        System.out.println("    1. DriverManager: 管理数据库驱动");
        System.out.println("    2. Connection: 表示数据库连接");
        System.out.println("    3. Statement: 执行 SQL 语句");
        System.out.println("    4. ResultSet: 处理查询结果");
        assertTrue(true);
    }

    @Test
    @DisplayName("JDBC 连接步骤测试")
    void connectionStepsTest() {
        System.out.println("  JDBC 连接步骤:");
        System.out.println("    1. 加载驱动: Class.forName(\"com.mysql.cj.jdbc.Driver\")");
        System.out.println("    2. 建立连接: DriverManager.getConnection(url, user, password)");
        System.out.println("    3. 创建 Statement: connection.createStatement()");
        System.out.println("    4. 执行查询: statement.executeQuery(sql)");
        System.out.println("    5. 处理结果: resultSet.next(), resultSet.getXxx()");
        System.out.println("    6. 关闭资源: close() 方法");
        assertTrue(true);
    }

    @Test
    @DisplayName("Statement 类型测试")
    void statementTypesTest() {
        System.out.println("  Statement 类型:");
        System.out.println("    1. Statement: 基本语句执行器");
        System.out.println("    2. PreparedStatement: 预编译语句，支持参数绑定");
        System.out.println("    3. CallableStatement: 调用存储过程");
        System.out.println("");
        System.out.println("  PreparedStatement 优势:");
        System.out.println("    - 防止 SQL 注入");
        System.out.println("    - 性能更好（预编译）");
        System.out.println("    - 支持参数化查询");
        assertTrue(true);
    }

    @Test
    @DisplayName("ResultSet 类型测试")
    void resultSetTypesTest() {
        System.out.println("  ResultSet 类型:");
        System.out.println("    - ResultSet.TYPE_FORWARD_ONLY: 只向前滚动");
        System.out.println("    - ResultSet.TYPE_SCROLL_INSENSITIVE: 可滚动，不敏感");
        System.out.println("    - ResultSet.TYPE_SCROLL_SENSITIVE: 可滚动，敏感");
        System.out.println("");
        System.out.println("  ResultSet 并发级别:");
        System.out.println("    - CONCUR_READ_ONLY: 只读");
        System.out.println("    - CONCUR_UPDATABLE: 可更新");
        assertTrue(true);
    }

    @Test
    @DisplayName("事务测试")
    void transactionTest() {
        System.out.println("  事务特性 ACID:");
        System.out.println("    - Atomicity（原子性）: 不可分割");
        System.out.println("    - Consistency（一致性）: 数据一致");
        System.out.println("    - Isolation（隔离性）: 并发隔离");
        System.out.println("    - Durability（持久性）: 持久保存");
        System.out.println("");
        System.out.println("  JDBC 事务控制:");
        System.out.println("    connection.setAutoCommit(false)  // 关闭自动提交");
        System.out.println("    connection.commit()              // 提交事务");
        System.out.println("    connection.rollback()            // 回滚事务");
        assertTrue(true);
    }

    @Test
    @DisplayName("数据库连接池测试")
    void connectionPoolTest() {
        System.out.println("  常用连接池:");
        System.out.println("    1. HikariCP（推荐，性能最高）");
        System.out.println("    2. Druid（阿里，功能丰富）");
        System.out.println("    3. C3P0（老牌，稳定）");
        System.out.println("    4. Apache DBCP");
        System.out.println("");
        System.out.println("  连接池配置示例:");
        System.out.println("    minimumIdle: 最小连接数");
        System.out.println("    maximumPoolSize: 最大连接数");
        System.out.println("    connectionTimeout: 连接超时");
        System.out.println("    idleTimeout: 空闲超时");
        assertTrue(true);
    }

    @Test
    @DisplayName("SQL 注入防护测试")
    void sqlInjectionTest() {
        System.out.println("  SQL 注入示例（危险）:");
        System.out.println("    String sql = \"SELECT * FROM users WHERE name = '\" + input + \"'\";");
        System.out.println("    如果 input = \"' OR '1'='1\", 将返回所有用户");
        System.out.println("");
        System.out.println("  正确做法（使用 PreparedStatement）:");
        System.out.println("    String sql = \"SELECT * FROM users WHERE name = ?\";");
        System.out.println("    pstmt.setString(1, input);  // 自动转义");
        assertTrue(true);
    }
}

