package com.shuai.database;

import java.util.Arrays;

/**
 * Java JDBC 数据库操作演示类
 *
 * 涵盖内容：
 * - DriverManager：驱动管理
 * - DataSource：数据源连接池
 * - Connection：数据库连接
 * - Statement：SQL 语句
 * - PreparedStatement：预编译语句
 * - ResultSet：结果集
 * - 事务管理
 * - 批处理操作
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsJdbcDemo {

    /**
     * 执行所有 JDBC 演示
     */
    public void runAllDemos() {
        driverManagerDemo();
        dataSourceDemo();
        statementDemo();
        preparedStatementDemo();
        resultSetDemo();
        transactionDemo();
        batchOperationDemo();
    }

    /**
     * 演示 DriverManager
     *
     * DriverManager 是传统的 JDBC 驱动管理方式
     * 用于建立数据库连接
     */
    private void driverManagerDemo() {
        System.out.println("\n--- DriverManager ---");

        System.out.println("\n  JDBC URL 格式:");
        System.out.println("    mysql:  jdbc:mysql://host:port/database");
        System.out.println("    oracle: jdbc:oracle:thin:@host:port:service");
        System.out.println("    postgresql: jdbc:postgresql://host:port/database");

        System.out.println("\n  基本流程:");
        System.out.println("    // 1. 加载驱动（Java 6+ 自动加载）");
        System.out.println("    Class.forName(\"com.mysql.cj.jdbc.Driver\");");

        System.out.println("\n    // 2. 获取连接");
        System.out.println("    String url = \"jdbc:mysql://localhost:3306/mydb\";");
        System.out.println("    String user = \"root\";");
        System.out.println("    String password = \"123456\";");
        System.out.println("    Connection conn = DriverManager.getConnection(url, user, password);");

        System.out.println("\n  连接参数:");
        System.out.println("    useSSL=false        - 禁用 SSL");
        System.out.println("    serverTimezone=UTC  - 设置时区");
        System.out.println("    allowPublicKeyRetrieval=true - 允许公钥获取");

        System.out.println("\n  驱动注册（可选）:");
        System.out.println("    DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());");
    }

    /**
     * 演示 DataSource
     *
     * DataSource 是推荐的数据源获取方式
     * 支持连接池，提高性能
     */
    private void dataSourceDemo() {
        System.out.println("\n--- DataSource ---");

        System.out.println("\n  DataSource 优势:");
        System.out.println("    - 连接池管理");
        System.out.println("    - 更高的性能");
        System.out.println("    - 更好的可配置性");

        System.out.println("\n(\n    // 使用 DataSource（以 HikariCP 为例）");
        System.out.println("    HikariConfig config = new HikariConfig();");
        System.out.println("    config.setJdbcUrl(\"jdbc:mysql://localhost:3306/mydb\");");
        System.out.println("    config.setUsername(\"root\");");
        System.out.println("    config.setPassword(\"123456\");");
        System.out.println("    config.setMaximumPoolSize(10);");
        System.out.println("    config.setMinimumIdle(2);");
        System.out.println("    config.setConnectionTimeout(30000);");
        System.out.println("    config.setIdleTimeout(600000);");
        System.out.println("    config.setMaxLifetime(1800000);");
        System.out.println("    HikariDataSource ds = new HikariDataSource(config);");

        System.out.println("\n    // 从 DataSource 获取连接");
        System.out.println("    try (Connection conn = ds.getConnection()) {");
        System.out.println("        // 使用连接");
        System.out.println("    }");

        System.out.println("\n  常用连接池:");
        System.out.println("    - HikariCP（推荐，性能最佳）");
        System.out.println("    - Apache DBCP");
        System.out.println("    - C3P0");
        System.out.println("    - Tomcat JDBC Pool");
    }

    /**
     * 演示 Statement
     *
     * Statement 用于执行静态 SQL 语句
     * 存在 SQL 注入风险，不推荐使用
     */
    private void statementDemo() {
        System.out.println("\n--- Statement ---");

        System.out.println("\n  Statement 方法:");
        System.out.println("    execute(String sql)          - 执行任意 SQL");
        System.out.println("    executeQuery(String sql)     - 执行查询，返回 ResultSet");
        System.out.println("    executeUpdate(String sql)    - 执行更新，返回影响行数");

        System.out.println("\n(\n    try (Statement stmt = conn.createStatement()) {");
        System.out.println("        // 执行查询");
        System.out.println("        String sql = \"SELECT * FROM users WHERE id = 1\";");
        System.out.println("        ResultSet rs = stmt.executeQuery(sql);");
        System.out.println("");
        System.out.println("        // 执行更新");
        System.out.println("        String updateSql = \"UPDATE users SET name = 'new name' WHERE id = 1\";");
        System.out.println("        int rows = stmt.executeUpdate(updateSql);");
        System.out.println("        System.out.println(\"影响行数: \" + rows);");
        System.out.println("");
        System.out.println("        // 执行任意 SQL");
        System.out.println("        String createSql = \"CREATE TABLE IF NOT EXISTS test (id INT)\";");
        System.out.println("        stmt.execute(createSql);");
        System.out.println("    }");

        System.out.println("\n  ⚠️ SQL 注入示例（危险）:");
        System.out.println("    String userId = \"1 OR 1=1\";");
        System.out.println("    String sql = \"SELECT * FROM users WHERE id = \" + userId;");
        System.out.println("    // 实际执行: SELECT * FROM users WHERE id = 1 OR 1=1");
        System.out.println("    // 返回所有用户数据！");

        System.out.println("\n  ✅ 解决方法：使用 PreparedStatement");
    }

    /**
     * 演示 PreparedStatement
     *
     * PreparedStatement 是预编译的 SQL 语句
     * 防止 SQL 注入，性能更好
     */
    private void preparedStatementDemo() {
        System.out.println("\n--- PreparedStatement ---");

        System.out.println("\n  优点:");
        System.out.println("    - 防止 SQL 注入");
        System.out.println("    - 预编译，性能更好");
        System.out.println("    - 参数自动转义");

        System.out.println("\n  参数占位符:");
        System.out.println("    MySQL:        ?");
        System.out.println("    Oracle:       ? (位置参数)");

        System.out.println("\n(\n    // 查询");
        System.out.println("    String sql = \"SELECT * FROM users WHERE id = ? AND name = ?\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {");
        System.out.println("        pstmt.setInt(1, 1);           // 设置第 1 个参数");
        System.out.println("        pstmt.setString(2, \"张三\");  // 设置第 2 个参数");
        System.out.println("        ResultSet rs = pstmt.executeQuery();");
        System.out.println("    }");

        System.out.println("\n    // 更新");
        System.out.println("    String updateSql = \"UPDATE users SET name = ? WHERE id = ?\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {");
        System.out.println("        pstmt.setString(1, \"李四\");");
        System.out.println("        pstmt.setInt(2, 1);");
        System.out.println("        int rows = pstmt.executeUpdate();");
        System.out.println("    }");

        System.out.println("\n    // 插入");
        System.out.println("    String insertSql = \"INSERT INTO users (name, email) VALUES (?, ?)\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {");
        System.out.println("        pstmt.setString(1, \"王五\");");
        System.out.println("        pstmt.setString(2, \"wang@example.com\");");
        System.out.println("        int rows = pstmt.executeUpdate();");
        System.out.println("    }");

        System.out.println("\n    // 删除");
        System.out.println("    String deleteSql = \"DELETE FROM users WHERE id = ?\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {");
        System.out.println("        pstmt.setInt(1, 1);");
        System.out.println("        int rows = pstmt.executeUpdate();");
        System.out.println("    }");

        System.out.println("\n  setXxx 方法:");
        System.out.println("    setString(i, x)    - 字符串");
        System.out.println("    setInt(i, x)       - 整数");
        System.out.println("    setLong(i, x)      - 长整数");
        System.out.println("    setDouble(i, x)    - 双精度");
        System.out.println("    setBoolean(i, x)   - 布尔值");
        System.out.println("    setDate(i, x)      - 日期（java.sql.Date）");
        System.out.println("    setTimestamp(i, x) - 时间戳");
        System.out.println("    setNull(i, type)   - NULL 值");
        System.out.println("    setObject(i, x)    - 对象（通用）");
    }

    /**
     * 演示 ResultSet
     *
     * ResultSet 是查询结果的数据集
     * 支持多种数据类型和滚动
     */
    private void resultSetDemo() {
        System.out.println("\n--- ResultSet ---");

        System.out.println("\n  ResultSet 类型:");
        System.out.println("    TYPE_FORWARD_ONLY      - 只向前滚动（默认）");
        System.out.println("    TYPE_SCROLL_INSENSITIVE - 可滚动，不敏感（数据变化不影响）");
        System.out.println("    TYPE_SCROLL_SENSITIVE  - 可滚动，敏感（数据变化影响结果）");

        System.out.println("\n  并发模式:");
        System.out.println("    CONCUR_READ_ONLY       - 只读（默认）");
        System.out.println("    CONCUR_UPDATABLE       - 可更新");

        System.out.println("\n  导航方法:");
        System.out.println("    next()         - 下一行（首次调用到第一行）");
        System.out.println("    previous()     - 上一行");
        System.out.println("    first()        - 第一行");
        System.out.println("    last()         - 最后一行");
        System.out.println("    beforeFirst()  - 第一行之前");
        System.out.println("    afterLast()    - 最后一行之后");
        System.out.println("    absolute(n)    - 跳转到第 n 行");
        System.out.println("    relative(n)    - 相对当前位置移动 n 行");

        System.out.println("\n  获取数据方法:");
        System.out.println("    getString(column)    - 获取字符串");
        System.out.println("    getInt(column)       - 获取整数");
        System.out.println("    getLong(column)      - 获取长整数");
        System.out.println("    getDouble(column)    - 获取双精度");
        System.out.println("    getBoolean(column)   - 获取布尔值");
        System.out.println("    getDate(column)      - 获取日期");
        System.out.println("    getTimestamp(column) - 获取时间戳");
        System.out.println("    getObject(column)    - 获取通用对象");

        System.out.println("\n  列名 vs 列索引:");
        System.out.println("    getString(\"name\")      - 通过列名");
        System.out.println("    getString(1)          - 通过列索引（从 1 开始）");

        System.out.println("\n(\n    String sql = \"SELECT id, name, email, created_at FROM users\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(sql,");
        System.out.println("            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);");
        System.out.println("         ResultSet rs = pstmt.executeQuery()) {");
        System.out.println("");
        System.out.println("        // 遍历结果");
        System.out.println("        while (rs.next()) {");
        System.out.println("            int id = rs.getInt(\"id\");");
        System.out.println("            String name = rs.getString(\"name\");");
        System.out.println("            String email = rs.getString(\"email\");");
        System.out.println("            Timestamp createdAt = rs.getTimestamp(\"created_at\");");
        System.out.println("            System.out.printf(\"ID: %d, Name: %s%n\", id, name);");
        System.out.println("        }");
        System.out.println("");
        System.out.println("        // 跳转到最后一行");
        System.out.println("        rs.last();");
        System.out.println("        int total = rs.getRow();");
        System.out.println("        System.out.println(\"总行数: \" + total);");
        System.out.println("    }");

        System.out.println("\n  检查 NULL:");
        System.out.println("    rs.getString(\"name\")  // 如果为 NULL，返回 null");
        System.out.println("    rs.wasNull()           // 检查上一个值是否为 NULL");
        System.out.println("    rs.getObject(\"name\", String.class)  // Java 16+ 支持类型安全的获取");
    }

    /**
     * 演示事务管理
     *
     * JDBC 事务默认自动提交
     * 可以手动控制提交和回滚
     */
    private void transactionDemo() {
        System.out.println("\n--- 事务管理 ---");

        System.out.println("\n  事务特性 ACID:");
        System.out.println("    - Atomicity（原子性）: 要么全做，要么全不做");
        System.out.println("    - Consistency（一致性）: 事务前后数据一致");
        System.out.println("    - Isolation（隔离性）: 并发事务相互隔离");
        System.out.println("    - Durability（持久性）: 提交后数据永久保存");

        System.out.println("\n  自动提交:");
        System.out.println("    默认情况下，每个 SQL 语句都是一个独立的事务");
        System.out.println("    conn.setAutoCommit(true);  // 开启自动提交");

        System.out.println("\n  手动控制事务:");
        System.out.println("    conn.setAutoCommit(false);  // 关闭自动提交");
        System.out.println("    try {");
        System.out.println("        // 执行多个操作");
        System.out.println("        stmt.executeUpdate(sql1);");
        System.out.println("        stmt.executeUpdate(sql2);");
        System.out.println("        conn.commit();           // 提交事务");
        System.out.println("    } catch (SQLException e) {");
        System.out.println("        conn.rollback();         // 回滚事务");
        System.out.println("        throw e;");
        System.out.println("    }");

        System.out.println("\n  保存点（Savepoint）:");
        System.out.println("    Savepoint sp = conn.setSavepoint(\"savepoint1\");");
        System.out.println("    // ... 执行操作 ...");
        System.out.println("    conn.rollback(sp);        // 回滚到保存点");
        System.out.println("    conn.releaseSavepoint(sp); // 释放保存点");

        System.out.println("\n  隔离级别:");
        System.out.println("    TRANSACTION_READ_UNCOMMITTED  - 读未提交（最低）");
        System.out.println("    TRANSACTION_READ_COMMITTED    - 读已提交");
        System.out.println("    TRANSACTION_REPEATABLE_READ   - 可重复读");
        System.out.println("    TRANSACTION_SERIALIZABLE      - 串行化（最高）");
        System.out.println("    conn.setTransactionIsolation(TRANSACTION_READ_COMMITTED);");

        System.out.println("\n  示例：转账事务");
        System.out.println("    // 转账：从账户A转100到账户B");
        System.out.println("    conn.setAutoCommit(false);");
        System.out.println("    try {");
        System.out.println("        // 扣款");
        System.out.println("        String sql1 = \"UPDATE accounts SET balance = balance - 100 WHERE id = ?\";");
        System.out.println("        pstmt.setInt(1, fromAccountId);");
        System.out.println("        pstmt.executeUpdate();");
        System.out.println("");
        System.out.println("        // 存款");
        System.out.println("        String sql2 = \"UPDATE accounts SET balance = balance + 100 WHERE id = ?\";");
        System.out.println("        pstmt.setInt(1, toAccountId);");
        System.out.println("        pstmt.executeUpdate();");
        System.out.println("");
        System.out.println("        conn.commit();");
        System.out.println("    } catch (Exception e) {");
        System.out.println("        conn.rollback();");
        System.out.println("        throw e;");
        System.out.println("    }");
    }

    /**
     * 演示批处理操作
     *
     * 批量执行 SQL，提高性能
     */
    private void batchOperationDemo() {
        System.out.println("\n--- 批处理操作 ---");

        System.out.println("\n  批处理优势:");
        System.out.println("    - 减少网络往返");
        System.out.println("    - 减少数据库开销");
        System.out.println("    - 提高插入/更新效率");

        System.out.println("\n  Statement 批处理:");
        System.out.println("    stmt.addBatch(sql1);");
        System.out.println("    stmt.addBatch(sql2);");
        System.out.println("    stmt.addBatch(sql3);");
        System.out.println("    int[] results = stmt.executeBatch();  // 返回每个语句影响的行数");
        System.out.println("    stmt.clearBatch();                    // 清空批处理");

        System.out.println("\n  PreparedStatement 批处理:");
        System.out.println("    String sql = \"INSERT INTO users (name, email) VALUES (?, ?)\";");
        System.out.println("    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {");
        System.out.println("        // 添加批量数据");
        System.out.println("        pstmt.setString(1, \"用户1\");");
        System.out.println("        pstmt.setString(2, \"user1@example.com\");");
        System.out.println("        pstmt.addBatch();");
        System.out.println("");
        System.out.println("        pstmt.setString(1, \"用户2\");");
        System.out.println("        pstmt.setString(2, \"user2@example.com\");");
        System.out.println("        pstmt.addBatch();");
        System.out.println("");
        System.out.println("        pstmt.setString(1, \"用户3\");");
        System.out.println("        pstmt.setString(2, \"user3@example.com\");");
        System.out.println("        pstmt.addBatch();");
        System.out.println("");
        System.out.println("        // 执行批处理");
        System.out.println("        int[] counts = pstmt.executeBatch();");
        System.out.println("        System.out.println(\"插入行数: \" + Arrays.stream(counts).sum());");
        System.out.println("    }");

        System.out.println("\n  批处理大小建议:");
        System.out.println("    - 通常 50-100 条为一批");
        System.out.println("    - 太大可能导致内存不足");
        System.out.println("    - 太大可能导致数据库超时");

        System.out.println("\n  批量插入优化:");
        System.out.println("    // MySQL 添加 rewriteBatchedStatements=true");
        System.out.println("    jdbc:mysql://localhost:3306/mydb?rewriteBatchedStatements=true");

        System.out.println("\n  事务与批处理:");
        System.out.println("    // 建议将批处理放在事务中");
        System.out.println("    conn.setAutoCommit(false);");
        System.out.println("    // 执行批处理");
        System.out.println("    pstmt.executeBatch();");
        System.out.println("    conn.commit();");
    }
}
