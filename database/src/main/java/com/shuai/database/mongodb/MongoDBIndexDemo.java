package com.shuai.database.mongodb;

/**
 * MongoDB 索引操作演示类
 *
 * 核心内容
 * ----------
 *   - 索引类型
 *   - 索引创建与删除
 *   - 索引选项
 *
 * @author Shuai
 * @version 1.0
 */
public class MongoDBIndexDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 索引操作");
        System.out.println("=".repeat(50));

        indexTypes();
        indexOperations();
        indexOptions();
    }

    /**
     * 索引类型
     */
    private void indexTypes() {
        System.out.println("\n--- 索引类型 ---");

        System.out.println("  单字段索引:");
        System.out.println("    Indexes.ascending(\"field\")");
        System.out.println("    Indexes.descending(\"field\")");

        System.out.println("\n  复合索引:");
        System.out.println("    Indexes.ascending(\"field1\", \"field2\")");
        System.out.println("    Indexes.descending(\"status\", \"createdAt\")");

        System.out.println("\n  文本索引:");
        System.out.println("    Indexes.text(\"field\")");
        System.out.println("    Indexes.text(\"title\", \"content\")  // 多字段文本索引");

        System.out.println("\n  地理空间索引:");
        System.out.println("    Indexes.geo2dsphere(\"location\")");
        System.out.println("    Indexes.geoHaystack(\"location\", \"category\")");

        System.out.println("\n  哈希索引:");
        System.out.println("    Indexes.hashed(\"field\")");

        System.out.println("\n  多键索引（自动为数组字段创建）:");
        System.out.println("    // skills 字段是数组，自动创建多键索引");
        System.out.println("    collection.createIndex(Indexes.ascending(\"skills\"))");
    }

    /**
     * 索引操作
     */
    private void indexOperations() {
        System.out.println("\n--- 索引操作 ---");

        System.out.println("  创建索引:");
        System.out.println("    collection.createIndex(Indexes.ascending(\"name\"))");
        System.out.println("    collection.createIndex(Indexes.ascending(\"email\"), new IndexOptions().unique(true))");

        System.out.println("\n  创建复合索引:");
        System.out.println("    collection.createIndex(");
        System.out.println("        Indexes.ascending(\"status\", \"createdAt\")");
        System.out.println("    )");

        System.out.println("\n  查看索引:");
        System.out.println("    collection.listIndexes()");

        System.out.println("\n  删除索引:");
        System.out.println("    collection.dropIndex(\"indexName\")");
        System.out.println("    collection.dropIndexes()  // 删除所有索引");

        System.out.println("\n  查看索引大小:");
        System.out.println("    db.collection.totalIndexSize()");
    }

    /**
     * 索引选项
     */
    private void indexOptions() {
        System.out.println("\n--- 索引选项 ---");

        System.out.println("  唯一索引:");
        System.out.println("    new IndexOptions().unique(true)");
        System.out.println("    // 不允许重复值");

        System.out.println("\n  部分索引:");
        System.out.println("    new IndexOptions().partialFilterExpression(");
        System.out.println("        Filters.gt(\"age\", 18)");
        System.out.println("    )");
        System.out.println("    // 只为满足条件的文档创建索引");

        System.out.println("\n  稀疏索引:");
        System.out.println("    new IndexOptions().sparse(true)");
        System.out.println("    // 只索引存在该字段的文档");

        System.out.println("\n  过期索引:");
        System.out.println("    new IndexOptions().expireAfter(3600L, TimeUnit.SECONDS)");
        System.out.println("    // 自动删除过期文档");

        System.out.println("\n  命名索引:");
        System.out.println("    new IndexOptions().name(\"idx_user_email\")");

        System.out.println("\n  权重设置（文本索引）:");
        System.out.println("    new IndexOptions()");
        System.out.println("        .weights(new Document().append(\"title\", 10).append(\"content\", 1))");
    }
}
