package com.shuai.database;

/**
 * 数据库模块入口类
 *
 * 整合 MySQL、PostgreSQL、MongoDB、Redis 数据库演示：
 * - MySQL: JDBC 操作、连接池、事务、CRUD、MyBatis
 * - PostgreSQL: JDBC 操作、CTEs、窗口函数、JSONB、全文搜索
 * - MongoDB: NoSQL 文档数据库、CRUD、聚合、索引
 * - Redis: 缓存、分布式锁、数据结构
 * - LevelDB: 嵌入式键值存储
 * - RocksDB: 高性能嵌入式键值存储
 * - ShardingSphere: 分库分表、读写分离
 * - Canal: MySQL binlog 订阅与数据同步
 *
 * @author Shuai
 * @version 1.0
 */
public class DatabaseDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(50));
        System.out.println("       数据库模块");
        System.out.println("=".repeat(50));

        // MySQL 演示
        new com.shuai.database.mysql.MySqlDemo().runAllDemos();

        // PostgreSQL 演示
        new com.shuai.database.postgresql.PostgreSqlDemo().runAllDemos();

        // MongoDB 演示
        new com.shuai.database.mongodb.MongoDbDemo().runAllDemos();

        // Redis 演示
        new com.shuai.database.redis.RedisDemo().runAllDemos();

        // LevelDB 演示
        try {
            com.shuai.database.leveldb.LevelDbDemo.main(args);
        } catch (Exception e) {
            System.err.println("LevelDB 演示失败: " + e.getMessage());
        }

        // RocksDB 演示
        try {
            com.shuai.database.rocksdb.RocksDbDemo.main(args);
        } catch (Exception e) {
            System.err.println("RocksDB 演示失败: " + e.getMessage());
        }

        // ShardingSphere 演示
        try {
            com.shuai.database.shardingsphere.ShardingSphereDemo.main(args);
        } catch (Exception e) {
            System.err.println("ShardingSphere 演示失败: " + e.getMessage());
        }

        // Canal 演示
        try {
            com.shuai.database.canal.CanalDemo.main(args);
        } catch (Exception e) {
            System.err.println("Canal 演示失败: " + e.getMessage());
        }
    }
}
