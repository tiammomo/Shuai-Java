package com.shuai.database.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Druid 连接池演示
 */
public class DruidDemo {

    private static DruidDataSource dataSource;

    public void runAllDemos() {
        System.out.println("\n--- Druid 连接池演示 ---");

        try {
            basicConfigDemo();
            propertyConfigDemo();
            monitorDemo();
            sqlFilterDemo();
        } catch (Exception e) {
            System.err.println("Druid 操作异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            shutdown();
        }
    }

    /**
     * 基本配置演示
     */
    private void basicConfigDemo() throws SQLException {
        System.out.println("\n[1] Druid 基本配置演示");

        // 方式1: 编程式配置
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/test");
        dataSource.setUsername("ubuntu");
        dataSource.setPassword("ubuntu");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

        // 连接池配置
        dataSource.setInitialSize(5);
        dataSource.setMinIdle(5);
        dataSource.setMaxActive(20);
        dataSource.setMaxWait(60000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);

        // MySQL 专用配置
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        // 开启 PSCache
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        System.out.println("  Druid 连接池已初始化");
        printPoolStatus();

        // 测试连接
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("  获取连接成功: " + conn.getClass().getSimpleName());
        }
    }

    /**
     * 属性文件配置演示
     */
    private void propertyConfigDemo() throws Exception {
        System.out.println("\n[2] 属性文件配置演示");

        Properties props = new Properties();
        props.setProperty("url", "jdbc:mysql://localhost:3306/test");
        props.setProperty("username", "ubuntu");
        props.setProperty("password", "ubuntu");
        props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");

        // 连接池配置
        props.setProperty("initialSize", "5");
        props.setProperty("minIdle", "5");
        props.setProperty("maxActive", "20");
        props.setProperty("maxWait", "60000");

        // 其他配置
        props.setProperty("timeBetweenEvictionRunsMillis", "60000");
        props.setProperty("minEvictableIdleTimeMillis", "300000");
        props.setProperty("validationQuery", "SELECT 1");
        props.setProperty("testWhileIdle", "true");
        props.setProperty("poolPreparedStatements", "true");
        props.setProperty("maxPoolPreparedStatementPerConnectionSize", "20");

        // 从配置文件加载
        // dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(props);

        System.out.println("  属性配置示例（需配置文件加载）");
        System.out.println("  配置文件 druid.properties 内容:");
        System.out.println("    url=jdbc:mysql://localhost:3306/test");
        System.out.println("    username=ubuntu");
        System.out.println("    password=ubuntu");
        System.out.println("    driverClassName=com.mysql.cj.jdbc.Driver");
        System.out.println("    initialSize=5");
        System.out.println("    minIdle=5");
        System.out.println("    maxActive=20");
    }

    /**
     * 监控演示
     */
    private void monitorDemo() throws SQLException {
        System.out.println("\n[3] Druid 监控演示");

        // 执行一些 SQL 操作
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("  用户总数: " + rs.getLong(1));
            }
        }

        // 获取连接池状态
        System.out.println("  连接池状态:");
        System.out.println("    活跃连接: " + dataSource.getActiveCount());
        System.out.println("    空闲连接: " + dataSource.getPoolingCount());
        System.out.println("    等待线程: " + dataSource.getWaitThreadCount());
        System.out.println("    总连接数: " + dataSource.getConnectCount());

        // 获取 SQL 统计 (通过 StatManager)
        System.out.println("  SQL 统计:");
        System.out.println("    执行次数: 通过 Druid StatManager 获取");

        // 获取连接池中的连接信息
        System.out.println("  连接池配置:");
        System.out.println("    初始大小: " + dataSource.getInitialSize());
        System.out.println("    最小空闲: " + dataSource.getMinIdle());
        System.out.println("    最大活跃: " + dataSource.getMaxActive());
        System.out.println("    最大等待: " + dataSource.getMaxWait() + "ms");
    }

    /**
     * SQL 过滤器演示
     */
    private void sqlFilterDemo() throws SQLException {
        System.out.println("\n[4] SQL 过滤器演示");

        // 配置 WallFilter 进行 SQL 注入防护
        // 方式1: 编程式配置
        // WallFilter wallFilter = new WallFilter();
        // wallFilter.setDbType("mysql");
        // dataSource.setProxyFilters(Arrays.asList(wallFilter));

        System.out.println("  SQL 防火墙配置示例:");
        System.out.println("    WallFilter - SQL 注入防护");
        System.out.println("    防护类型:");
        System.out.println("      - 禁止删除表: DROP TABLE");
        System.out.println("      - 禁止修改表: ALTER TABLE");
        System.out.println("      - 禁止执行: GRANT, REVOKE");
        System.out.println("      - 防护 SQL 注入");

        System.out.println("  配置方式:");
        System.out.println("    Properties props = new Properties();");
        System.out.println("    props.setProperty(\"wall.enabled\", \"true\");");
        System.out.println("    dataSource.setProxyFilters(...);");
    }

    /**
     * 打印连接池状态
     */
    private void printPoolStatus() {
        if (dataSource != null) {
            System.out.println("  连接池状态:");
            System.out.println("    活跃连接: " + dataSource.getActiveCount());
            System.out.println("    空闲连接: " + dataSource.getPoolingCount());
            System.out.println("    总连接数: " + dataSource.getConnectCount());
        }
    }

    /**
     * 关闭连接池
     */
    private void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("\n[Druid] 连接池已关闭");
        }
    }

    /**
     * 获取 Druid DataSource
     */
    public static DataSource getDataSource() {
        return dataSource;
    }
}
