package com.shuai.database.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

/**
 * MongoDB 连接验证演示
 */
public class MongoDbVerifyDemo {

    private static final String CONNECTION_URI = "mongodb://ubuntu:ubuntu@localhost:27017/?authSource=admin";
    private static final String DATABASE_NAME = "testdb";

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 连接验证");
        System.out.println("=".repeat(50));

        try {
            // 1. 创建连接
            connectionDemo();

            // 2. 基本操作
            basicOperations();

            // 3. 清理
            cleanup();

            System.out.println("\n[成功] MongoDB 连接验证完成！");
        } catch (Exception e) {
            System.err.println("\n[错误] MongoDB 操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 连接演示
     */
    private void connectionDemo() {
        System.out.println("\n--- 连接 MongoDB ---");

        try (MongoClient client = MongoClients.create(CONNECTION_URI)) {
            // 获取数据库信息
            MongoDatabase db = client.getDatabase(DATABASE_NAME);
            System.out.println("  连接成功！");
            System.out.println("  数据库: " + db.getName());
            int count = 0;
            for (String name : db.listCollectionNames()) {
                if (count++ < 5) {
                    System.out.println("    - " + name);
                }
            }
            if (count > 5) {
                System.out.println("    ... 共 " + count + " 个集合");
            }
        }
    }

    /**
     * 基本 CRUD 操作
     */
    private void basicOperations() {
        System.out.println("\n--- 基本操作 ---");

        try (MongoClient client = MongoClients.create(CONNECTION_URI)) {
            MongoDatabase db = client.getDatabase(DATABASE_NAME);
            MongoCollection<Document> users = db.getCollection("users");

            // 插入文档
            Document user = new Document()
                .append("name", "TestUser")
                .append("age", 25)
                .append("email", "test@example.com")
                .append("status", "active");

            users.insertOne(user);
            System.out.println("  插入文档成功: " + user.getObjectId("_id"));

            // 查询文档
            Document found = users.find(new Document("name", "TestUser")).first();
            System.out.println("  查询结果: " + found);

            // 更新文档
            users.updateOne(
                new Document("name", "TestUser"),
                new Document("$set", new Document("age", 26))
            );
            System.out.println("  更新文档成功");

            // 验证更新
            Document updated = users.find(new Document("name", "TestUser")).first();
            System.out.println("  更新后: " + updated);
        }
    }

    /**
     * 清理测试数据
     */
    private void cleanup() {
        System.out.println("\n--- 清理测试数据 ---");

        try (MongoClient client = MongoClients.create(CONNECTION_URI)) {
            MongoDatabase db = client.getDatabase(DATABASE_NAME);
            MongoCollection<Document> users = db.getCollection("users");

            long deletedCount = users.deleteMany(new Document("name", "TestUser")).getDeletedCount();
            System.out.println("  删除测试数据: " + deletedCount + " 条");
        }
    }

    public static void main(String[] args) {
        new MongoDbVerifyDemo().runAllDemos();
    }
}
