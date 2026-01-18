package com.shuai.database.mongodb;

import org.bson.Document;

import java.util.Arrays;

/**
 * MongoDB 基础操作演示类
 *
 * 核心内容
 * ----------
 *   - 连接 MongoDB
 *   - CRUD 操作：插入、查询、删除、更新
 *   - 批量操作
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDBOperationsDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 基础操作");
        System.out.println("=".repeat(50));

        connection();
        insertOperations();
        queryOperations();
        deleteOperations();
        updateOperations();
    }

    /**
     * 连接 MongoDB
     */
    private void connection() {
        System.out.println("\n--- 连接 MongoDB ---");

        System.out.println("  标准连接:");
        System.out.println("    MongoClient client = MongoClients.create(\"mongodb://localhost:27017\")");
        System.out.println("    MongoDatabase db = client.getDatabase(\"mydb\")");

        System.out.println("\n  带认证的连接:");
        System.out.println("    MongoClient client = MongoClients.create(");
        System.out.println("        \"mongodb://username:password@localhost:27017/?authSource=admin\"");
        System.out.println("    )");

        System.out.println("\n  获取集合:");
        System.out.println("    MongoCollection<Document> collection = db.getCollection(\"users\")");
    }

    /**
     * 插入操作
     */
    private void insertOperations() {
        System.out.println("\n--- 插入操作 ---");

        System.out.println("  创建文档:");
        System.out.println("    Document doc = new Document()");
        System.out.println("        .append(\"name\", \"张三\")");
        System.out.println("        .append(\"age\", 25)");
        System.out.println("        .append(\"skills\", Arrays.asList(\"Java\", \"Python\"))");

        System.out.println("\n  单条插入:");
        System.out.println("      InsertOneResult result = collection.insertOne(doc)");
        System.out.println("      System.out.println(\"插入ID: \" + result.getInsertedId())");

        System.out.println("\n  批量插入:");
        System.out.println("      List<Document> docs = Arrays.asList(doc1, doc2, doc3)");
        System.out.println("      InsertManyResult result = collection.insertMany(docs)");
        System.out.println("      System.out.println(\"插入数量: \" + result.getInsertedIds().size())");
    }

    /**
     * 查询操作
     */
    private void queryOperations() {
        System.out.println("\n--- 查询操作 ---");

        System.out.println("  查询所有:");
        System.out.println("    collection.find()");
        System.out.println("    // 返回集合中所有文档的游标");

        System.out.println("\n  条件查询:");
        System.out.println("    collection.find(Filters.eq(\"name\", \"张三\"))");
        System.out.println("    collection.find(Filters.gt(\"age\", 18))");
        System.out.println("    collection.find(Filters.and(");
        System.out.println("        Filters.eq(\"status\", \"active\"),");
        System.out.println("        Filters.gte(\"age\", 18)");
        System.out.println("    ))");

        System.out.println("\n  限制结果:");
        System.out.println("    collection.find().limit(10)  // 只返回 10 条");
        System.out.println("    collection.find().skip(20)   // 跳过前 20 条");
        System.out.println("    collection.find().sort(Sorts.ascending(\"name\"))  // 排序");

        System.out.println("\n  统计数量:");
        System.out.println("    long count = collection.countDocuments(Filters.eq(\"status\", \"active\"))");
    }

    /**
     * 删除操作
     */
    private void deleteOperations() {
        System.out.println("\n--- 删除操作 ---");

        System.out.println("  删除一条:");
        System.out.println("    DeleteResult result = collection.deleteOne(Filters.eq(\"name\", \"张三\"))");
        System.out.println("    System.out.println(\"删除数量: \" + result.getDeletedCount())");

        System.out.println("\n  删除多条:");
        System.out.println("    DeleteResult result = collection.deleteMany(Filters.eq(\"status\", \"inactive\"))");

        System.out.println("\n  删除所有:");
        System.out.println("    collection.deleteMany(new Document())");
    }

    /**
     * 更新操作
     */
    private void updateOperations() {
        System.out.println("\n--- 更新操作 ---");

        System.out.println("  更新一条:");
        System.out.println("    UpdateResult result = collection.updateOne(");
        System.out.println("        Filters.eq(\"name\", \"张三\"),");
        System.out.println("        Updates.set(\"age\", 26)");
        System.out.println("    )");

        System.out.println("\n  更新多条:");
        System.out.println("    UpdateResult result = collection.updateMany(");
        System.out.println("        Filters.eq(\"status\", \"inactive\"),");
        System.out.println("        Updates.set(\"status\", \"archived\")");
        System.out.println("    )");

        System.out.println("\n  查找并更新:");
        System.out.println("    Document doc = collection.findOneAndUpdate(");
        System.out.println("        Filters.eq(\"name\", \"张三\"),");
        System.out.println("        Updates.inc(\"age\", 1)");
        System.out.println("    )");

        System.out.println("\n  替换文档:");
        System.out.println("    collection.replaceOne(");
        System.out.println("        Filters.eq(\"_id\", id),");
        System.out.println("        new Document(\"$set\", newDocument)");
        System.out.println("    )");
    }
}
