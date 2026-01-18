package com.shuai.database.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.concurrent.TimeUnit;

/**
 * MongoDB 配置类
 * 管理连接和常用配置
 */
public class MongoDbConfig {

    // 连接配置
    private static final String DEFAULT_URI = "mongodb://ubuntu:ubuntu@localhost:27017/?authSource=admin";
    private static final String DEFAULT_DATABASE = "testdb";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    /**
     * 初始化连接
     */
    public static synchronized void init() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(DEFAULT_URI);
            database = mongoClient.getDatabase(DEFAULT_DATABASE);
            System.out.println("[MongoDbConfig] MongoDB 连接初始化完成");
            System.out.println("  数据库: " + database.getName());
        }
    }

    /**
     * 获取 MongoClient
     */
    public static MongoClient getClient() {
        if (mongoClient == null) {
            init();
        }
        return mongoClient;
    }

    /**
     * 获取数据库
     */
    public static MongoDatabase getDatabase() {
        if (database == null) {
            init();
        }
        return database;
    }

    /**
     * 获取集合
     */
    public static MongoCollection<Document> getCollection(String name) {
        return getDatabase().getCollection(name);
    }

    /**
     * 获取集合（带文档类型）
     */
    public static <T> MongoCollection<T> getCollection(String name, Class<T> clazz) {
        return getDatabase().getCollection(name, clazz);
    }

    /**
     * 创建临时集合（用于测试）
     */
    public static MongoCollection<Document> createTestCollection(String name) {
        MongoCollection<Document> collection = getDatabase().getCollection(name);
        // 清空集合
        collection.deleteMany(new Document());
        return collection;
    }

    /**
     * 关闭连接
     */
    public static synchronized void shutdown() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            database = null;
            System.out.println("[MongoDbConfig] MongoDB 连接已关闭");
        }
    }

    /**
     * 获取数据库名称
     */
    public static String getDatabaseName() {
        return DEFAULT_DATABASE;
    }
}
