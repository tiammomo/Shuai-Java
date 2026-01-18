package com.shuai.database.shardingsphere.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * ShardingSphere 配置类
 * 提供读写分离和分片配置
 * 支持 ShardingSphere 5.4.1 API
 */
public class ShardingConfig {

    // 数据源配置
    private static final String MASTER_URL = "jdbc:mysql://localhost:3307/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String MASTER_USERNAME = "sharding";
    private static final String MASTER_PASSWORD = "sharding";

    private static final String SLAVE0_URL = "jdbc:mysql://localhost:3308/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String SLAVE1_URL = "jdbc:mysql://localhost:3309/sharding_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    /**
     * 创建读写分离数据源
     * 配置一主两从的读写分离架构
     * ShardingSphere 5.4.1 API: ReadwriteSplittingDataSourceRuleConfiguration(name, writeDataSourceName, readDataSourceNames, loadBalancerName)
     */
    public static DataSource createReadWriteDataSource() throws SQLException {
        // 创建主库数据源
        DataSource masterDataSource = createDataSource(MASTER_URL, MASTER_USERNAME, MASTER_PASSWORD);

        // 创建从库数据源
        DataSource slave0DataSource = createDataSource(SLAVE0_URL, MASTER_USERNAME, MASTER_PASSWORD);
        DataSource slave1DataSource = createDataSource(SLAVE1_URL, MASTER_USERNAME, MASTER_PASSWORD);

        // 配置读写分离数据源规则 (5.4.1 API 简化版)
        ReadwriteSplittingDataSourceRuleConfiguration dataSourceRule = new ReadwriteSplittingDataSourceRuleConfiguration(
            "ds_group",           // 规则名称
            "ds_master",          // 主库数据源名称
            Arrays.asList("ds_slave0", "ds_slave1"),  // 从库数据源名称列表
            "round_robin"         // 负载均衡算法
        );

        // 配置读写分离规则
        ReadwriteSplittingRuleConfiguration rwConfig = new ReadwriteSplittingRuleConfiguration(
            Collections.singletonList(dataSourceRule),
            new HashMap<>()  // 负载均衡算法配置
        );

        // 构建 ShardingSphere 数据源
        Collection<RuleConfiguration> rules = Collections.singletonList(rwConfig);
        return ShardingSphereDataSourceFactory.createDataSource(
            createDataSourceMap(masterDataSource, slave0DataSource, slave1DataSource),
            rules,
            new Properties()
        );
    }

    /**
     * 创建分片数据源
     * 配置水平分表（t_order 表按 order_id 取模分片）
     */
    public static DataSource createShardingDataSource() throws SQLException {
        // 创建数据源
        DataSource masterDataSource = createDataSource(MASTER_URL, MASTER_USERNAME, MASTER_PASSWORD);

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();

        // 配置 t_order 表的分片规则
        ShardingTableRuleConfiguration orderTableRule = new ShardingTableRuleConfiguration(
            "t_order",
            "ds_master.t_order_${0..3}"
        );
        // 配置分片策略 - 使用 StandardShardingStrategyConfiguration
        orderTableRule.setDatabaseShardingStrategy(
            new StandardShardingStrategyConfiguration("user_id", "order_id_mod")
        );
        orderTableRule.setTableShardingStrategy(
            new StandardShardingStrategyConfiguration("order_id", "order_id_mod")
        );
        // 配置主键生成策略
        orderTableRule.setKeyGenerateStrategy(
            new KeyGenerateStrategyConfiguration("order_id", "snowflake")
        );

        shardingRuleConfig.getTables().add(orderTableRule);

        // 配置分片算法（5.4.1 使用 INLINE 算法）
        Map<String, AlgorithmConfiguration> shardingAlgorithms = new HashMap<>();
        shardingAlgorithms.put("order_id_mod", new AlgorithmConfiguration("INLINE", createModAlgorithmProperties(4)));
        shardingRuleConfig.setShardingAlgorithms(shardingAlgorithms);

        // 配置主键生成算法
        Map<String, AlgorithmConfiguration> keyGenerators = new HashMap<>();
        keyGenerators.put("snowflake", new AlgorithmConfiguration("SNOWFLAKE", new Properties()));
        shardingRuleConfig.setKeyGenerators(keyGenerators);

        // 构建数据源
        Map<String, DataSource> dataSourceMap = createDataSourceMap(masterDataSource);
        Collection<RuleConfiguration> rules = Collections.singletonList(shardingRuleConfig);

        return ShardingSphereDataSourceFactory.createDataSource(
            dataSourceMap,
            rules,
            new Properties()
        );
    }

    /**
     * 创建同时支持读写分离和分片的数据源
     */
    public static DataSource createShardingWithReadWriteDataSource() throws SQLException {
        // 创建数据源
        DataSource masterDataSource = createDataSource(MASTER_URL, MASTER_USERNAME, MASTER_PASSWORD);
        DataSource slave0DataSource = createDataSource(SLAVE0_URL, MASTER_USERNAME, MASTER_PASSWORD);
        DataSource slave1DataSource = createDataSource(SLAVE1_URL, MASTER_USERNAME, MASTER_PASSWORD);

        // 配置读写分离数据源规则 (5.4.1 API 简化版)
        ReadwriteSplittingDataSourceRuleConfiguration dataSourceRule = new ReadwriteSplittingDataSourceRuleConfiguration(
            "ds_group",           // 规则名称
            "ds_master",          // 主库数据源名称
            Arrays.asList("ds_slave0", "ds_slave1"),  // 从库数据源名称列表
            "round_robin"         // 负载均衡算法
        );

        // 配置读写分离规则
        ReadwriteSplittingRuleConfiguration rwConfig = new ReadwriteSplittingRuleConfiguration(
            Collections.singletonList(dataSourceRule),
            new HashMap<>()
        );

        // 配置分片规则
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        ShardingTableRuleConfiguration orderTableRule = new ShardingTableRuleConfiguration(
            "t_order",
            "ds_group.t_order_${0..3}"
        );
        orderTableRule.setDatabaseShardingStrategy(
            new StandardShardingStrategyConfiguration("user_id", "order_id_mod")
        );
        orderTableRule.setTableShardingStrategy(
            new StandardShardingStrategyConfiguration("order_id", "order_id_mod")
        );
        orderTableRule.setKeyGenerateStrategy(
            new KeyGenerateStrategyConfiguration("order_id", "snowflake")
        );
        shardingRuleConfig.getTables().add(orderTableRule);

        // 配置分片算法（5.4.1 使用 INLINE 算法）
        Map<String, AlgorithmConfiguration> shardingAlgorithms = new HashMap<>();
        shardingAlgorithms.put("order_id_mod", new AlgorithmConfiguration("INLINE", createModAlgorithmProperties(4)));
        shardingRuleConfig.setShardingAlgorithms(shardingAlgorithms);

        Map<String, AlgorithmConfiguration> keyGenerators = new HashMap<>();
        keyGenerators.put("snowflake", new AlgorithmConfiguration("SNOWFLAKE", new Properties()));
        shardingRuleConfig.setKeyGenerators(keyGenerators);

        // 构建数据源
        Map<String, DataSource> dataSourceMap = createDataSourceMap(masterDataSource, slave0DataSource, slave1DataSource);
        Collection<RuleConfiguration> rules = Arrays.asList(rwConfig, shardingRuleConfig);

        return ShardingSphereDataSourceFactory.createDataSource(
            dataSourceMap,
            rules,
            new Properties()
        );
    }

    /**
     * 创建数据源
     */
    public static DataSource createDataSource(String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        return dataSource;
    }

    /**
     * 创建数据源 Map
     */
    private static Map<String, DataSource> createDataSourceMap(DataSource... dataSources) {
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds_master", dataSources[0]);
        if (dataSources.length > 1) {
            result.put("ds_slave0", dataSources[1]);
        }
        if (dataSources.length > 2) {
            result.put("ds_slave1", dataSources[2]);
        }
        return result;
    }

    /**
     * 创建取模算法配置 (5.4.1 使用 INLINE 算法 + 表达式)
     */
    private static Properties createModAlgorithmProperties(int shardingCount) {
        Properties props = new Properties();
        // 5.4.1 使用 INLINE 算法，配置表达式
        props.setProperty("algorithm-expression", "t_order_${order_id % " + shardingCount + "}");
        return props;
    }
}
