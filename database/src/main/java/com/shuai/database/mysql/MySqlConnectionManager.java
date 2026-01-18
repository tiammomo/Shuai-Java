package com.shuai.database.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * MySQL 连接管理器
 * 使用 HikariCP 连接池，支持环境变量配置
 */
public class MySqlConnectionManager {

    private static HikariDataSource dataSource;

    // 默认配置（本地开发）
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/test";
    private static final String DEFAULT_USER = "ubuntu";
    private static final String DEFAULT_PASSWORD = "ubuntu";

    // 环境变量键
    private static final String ENV_URL = "MYSQL_URL";
    private static final String ENV_USER = "MYSQL_USER";
    private static final String ENV_PASSWORD = "MYSQL_PASSWORD";

    /**
     * 初始化连接池
     */
    static {
        initDataSource();
    }

    /**
     * 根据环境变量或默认配置初始化连接池
     */
    private static void initDataSource() {
        String url = getEnvOrDefault(ENV_URL, DEFAULT_URL);
        String user = getEnvOrDefault(ENV_USER, DEFAULT_USER);
        String password = getEnvOrDefault(ENV_PASSWORD, DEFAULT_PASSWORD);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 连接池配置
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(20);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        config.setConnectionTimeout(30000);

        // MySQL 专用配置
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");

        dataSource = new HikariDataSource(config);
        System.out.println("[MySqlConnectionManager] HikariCP 连接池初始化完成");
    }

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * 获取 DataSource
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * 获取连接池状态
     */
    public static void printPoolStatus() {
        if (dataSource != null) {
            System.out.println("[连接池状态] " +
                "活跃连接: " + dataSource.getHikariPoolMXBean().getActiveConnections() +
                ", 空闲连接: " + dataSource.getHikariPoolMXBean().getIdleConnections() +
                ", 总连接: " + dataSource.getHikariPoolMXBean().getTotalConnections());
        }
    }

    /**
     * 关闭连接池
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[MySqlConnectionManager] 连接池已关闭");
        }
    }

    /**
     * 获取环境变量或默认值
     */
    private static String getEnvOrDefault(String envKey, String defaultValue) {
        String envValue = System.getenv(envKey);
        return (envValue != null && !envValue.isEmpty()) ? envValue : defaultValue;
    }

    /**
     * 使用默认配置创建连接（不使用连接池）
     */
    public static Connection getSimpleConnection() throws SQLException {
        String url = getEnvOrDefault(ENV_URL, DEFAULT_URL);
        String user = getEnvOrDefault(ENV_USER, DEFAULT_USER);
        String password = getEnvOrDefault(ENV_PASSWORD, DEFAULT_PASSWORD);

        java.sql.DriverManager.setLoginTimeout(10);
        return java.sql.DriverManager.getConnection(url, user, password);
    }
}
