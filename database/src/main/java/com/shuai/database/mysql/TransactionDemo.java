package com.shuai.database.mysql;

import com.shuai.database.mysql.MySqlConnectionManager;

import java.sql.*;

/**
 * MySQL 事务管理演示
 */
public class TransactionDemo {

    public void runAllDemos() {
        System.out.println("\n--- MySQL 事务管理演示 ---");

        try {
            basicTransactionDemo();
            savepointDemo();
            isolationLevelDemo();
            autoRollbackDemo();
        } catch (Exception e) {
            System.err.println("事务操作异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 基本事务演示：转账
     */
    private void basicTransactionDemo() throws SQLException {
        System.out.println("\n[1] 基本事务演示 - 转账操作");

        // 初始化测试数据
        initAccountData();

        String transferSql = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";
        String receiveSql = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";

        // 获取连接并开启事务
        try (Connection conn = MySqlConnectionManager.getConnection()) {
            // 关闭自动提交，开启事务
            conn.setAutoCommit(false);

            try {
                // 转出账户扣款
                try (PreparedStatement stmt = conn.prepareStatement(transferSql)) {
                    stmt.setBigDecimal(1, new java.math.BigDecimal("100"));
                    stmt.setInt(2, 1);
                    int rows = stmt.executeUpdate();
                    if (rows != 1) throw new SQLException("转出账户更新失败");
                    System.out.println("  转出账户扣款: 100");
                }

                // 模拟网络延迟，测试事务回滚
                // Thread.sleep(1000);

                // 转入账户收款
                try (PreparedStatement stmt = conn.prepareStatement(receiveSql)) {
                    stmt.setBigDecimal(1, new java.math.BigDecimal("100"));
                    stmt.setInt(2, 2);
                    int rows = stmt.executeUpdate();
                    if (rows != 1) throw new SQLException("转入账户更新失败");
                    System.out.println("  转入账户收款: 100");
                }

                // 提交事务
                conn.commit();
                System.out.println("  事务提交成功");

            } catch (Exception e) {
                // 回滚事务
                conn.rollback();
                System.out.println("  事务回滚: " + e.getMessage());
            }
        }

        // 查询结果
        printAccountBalance();
    }

    /**
     * SAVEPOINT 保存点演示
     */
    private void savepointDemo() throws SQLException {
        System.out.println("\n[2] SAVEPOINT 保存点演示");

        // 初始化测试数据
        initOrderData();

        String updateSql = "UPDATE order_items SET quantity = ? WHERE id = ?";
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = MySqlConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            Savepoint savepoint1 = conn.setSavepoint("before_update");
            System.out.println("  设置保存点: before_update");

            try {
                // 先插入一条记录
                long itemId;
                try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setLong(1, 1);
                    stmt.setLong(2, 1);
                    stmt.setInt(3, 5);
                    stmt.setBigDecimal(4, new java.math.BigDecimal("100.00"));
                    stmt.executeUpdate();
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        itemId = rs.getLong(1);
                    }
                }
                System.out.println("  插入订单项: id=" + itemId);

                // 更新数量
                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setInt(1, 10);
                    stmt.setLong(2, itemId);
                    stmt.executeUpdate();
                }
                System.out.println("  更新数量: 5 -> 10");

                // 模拟错误，回滚到保存点
                conn.rollback(savepoint1);
                System.out.println("  回滚到保存点: before_update");

                // 再次提交
                conn.commit();
                System.out.println("  事务提交成功（保存点之后的数据被回滚）");

            } catch (Exception e) {
                conn.rollback(savepoint1);
                conn.commit();
            }
        }
    }

    /**
     * 隔离级别演示
     */
    private void isolationLevelDemo() throws SQLException {
        System.out.println("\n[3] 隔离级别演示");

        // 读取当前隔离级别
        try (Connection conn = MySqlConnectionManager.getConnection()) {
            int level = conn.getTransactionIsolation();
            String levelName = getIsolationLevelName(level);
            System.out.println("  当前隔离级别: " + levelName);
        }

        // 设置隔离级别示例
        System.out.println("  可用隔离级别:");
        System.out.println("    - Connection.TRANSACTION_READ_UNCOMMITTED: 读取未提交");
        System.out.println("    - Connection.TRANSACTION_READ_COMMITTED: 读取已提交");
        System.out.println("    - Connection.TRANSACTION_REPEATABLE_READ: 可重复读 (MySQL默认)");
        System.out.println("    - Connection.TRANSACTION_SERIALIZABLE: 串行化");

        // 演示设置隔离级别
        try (Connection conn = MySqlConnectionManager.getConnection()) {
            conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            System.out.println("  已设置为: 可重复读 (REPEATABLE READ)");
        }
    }

    /**
     * 自动回滚演示
     */
    private void autoRollbackDemo() throws SQLException {
        System.out.println("\n[4] 自动回滚演示");

        try (Connection conn = MySqlConnectionManager.getConnection()) {
            conn.setAutoCommit(false);

            // 故意执行错误的 SQL
            try {
                String wrongSql = "UPDATE non_existent_table SET col = 'value'";
                try (PreparedStatement stmt = conn.prepareStatement(wrongSql)) {
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("  检测到错误，自动回滚: " + e.getMessage());
                conn.rollback();
            }

            conn.commit();
        }
    }

    /**
     * 初始化账户数据
     */
    private void initAccountData() throws SQLException {
        String createSql = """
            CREATE TABLE IF NOT EXISTS accounts (
                id INT PRIMARY KEY AUTO_INCREMENT,
                user_id INT NOT NULL,
                balance DECIMAL(10, 2) DEFAULT 0,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
            )
            """;

        String insertSql = """
            INSERT INTO accounts (user_id, balance) VALUES (?, ?)
            ON DUPLICATE KEY UPDATE balance = balance
            """;

        try (Connection conn = MySqlConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        }

        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setInt(1, 1);
            stmt.setBigDecimal(2, new java.math.BigDecimal("1000.00"));
            stmt.executeUpdate();

            stmt.setInt(1, 2);
            stmt.setBigDecimal(2, new java.math.BigDecimal("500.00"));
            stmt.executeUpdate();
        }

        System.out.println("  账户数据已初始化");
    }

    /**
     * 初始化订单数据
     */
    private void initOrderData() throws SQLException {
        String createSql = """
            CREATE TABLE IF NOT EXISTS order_items (
                id BIGINT PRIMARY KEY AUTO_INCREMENT,
                order_id BIGINT NOT NULL,
                product_id BIGINT NOT NULL,
                quantity INT DEFAULT 1,
                price DECIMAL(10, 2),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = MySqlConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createSql);
        }

        System.out.println("  订单数据已初始化");
    }

    /**
     * 打印账户余额
     */
    private void printAccountBalance() throws SQLException {
        String sql = "SELECT user_id, balance FROM accounts ORDER BY user_id";
        try (Connection conn = MySqlConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("  用户" + rs.getInt("user_id") + "余额: " + rs.getBigDecimal("balance"));
            }
        }
    }

    /**
     * 获取隔离级别名称
     */
    private String getIsolationLevelName(int level) {
        return switch (level) {
            case Connection.TRANSACTION_READ_UNCOMMITTED -> "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED -> "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ -> "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE -> "SERIALIZABLE";
            default -> "UNKNOWN";
        };
    }
}
