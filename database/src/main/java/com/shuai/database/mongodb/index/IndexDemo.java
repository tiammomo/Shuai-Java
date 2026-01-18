package com.shuai.database.mongodb.index;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.shuai.database.mongodb.MongoDbConfig;
import org.bson.Document;

import java.util.Arrays;
import java.util.List;

/**
 * MongoDB 索引操作演示
 * 演示各种索引的创建和查询
 */
public class IndexDemo {

    private static final String COLLECTION_NAME = "products_index";

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 索引操作演示");
        System.out.println("=".repeat(50));

        MongoDbConfig.init();
        MongoDatabase db = MongoDbConfig.getDatabase();
        MongoCollection<Document> products = db.getCollection(COLLECTION_NAME);

        // 清空并准备测试数据
        products.deleteMany(new Document());
        prepareTestData(products);

        // 1. 查看现有索引
        listIndexes(products);

        // 2. 创建单字段索引
        createSingleFieldIndex(products);

        // 3. 创建复合索引
        createCompoundIndex(products);

        // 4. 创建文本索引
        createTextIndex(products);

        // 5. 创建唯一索引
        createUniqueIndex(products);

        // 6. 验证索引使用
        verifyIndexUsage(products);

        // 7. 删除索引
        dropIndexDemo(products);

        System.out.println("\n[成功] 索引操作演示完成！");
    }

    /**
     * 准备测试数据
     */
    private static void prepareTestData(MongoCollection<Document> products) {
        List<Document> productList = Arrays.asList(
                new Document()
                        .append("name", "iPhone 15 Pro")
                        .append("price", 7999)
                        .append("category", "手机")
                        .append("brand", "Apple")
                        .append("stock", 100)
                        .append("description", "Apple最新旗舰手机，搭载A17 Pro芯片"),
                new Document()
                        .append("name", "Samsung Galaxy S24")
                        .append("price", 6999)
                        .append("category", "手机")
                        .append("brand", "Samsung")
                        .append("stock", 80)
                        .append("description", "三星Galaxy S24智能手机"),
                new Document()
                        .append("name", "MacBook Pro 14")
                        .append("price", 14999)
                        .append("category", "电脑")
                        .append("brand", "Apple")
                        .append("stock", 50)
                        .append("description", "Apple MacBook Pro 14英寸 M3芯片"),
                new Document()
                        .append("name", "Dell XPS 15")
                        .append("price", 12999)
                        .append("category", "电脑")
                        .append("brand", "Dell")
                        .append("stock", 40)
                        .append("description", "Dell XPS 15 笔记本电脑"),
                new Document()
                        .append("name", "iPad Air")
                        .append("price", 4799)
                        .append("category", "平板")
                        .append("brand", "Apple")
                        .append("stock", 60)
                        .append("description", "Apple iPad Air 平板电脑"),
                new Document()
                        .append("name", "AirPods Pro")
                        .append("price", 1899)
                        .append("category", "耳机")
                        .append("brand", "Apple")
                        .append("stock", 200)
                        .append("description", "Apple AirPods Pro 第二代无线耳机"),
                new Document()
                        .append("name", "Sony WH-1000XM5")
                        .append("price", 2999)
                        .append("category", "耳机")
                        .append("brand", "Sony")
                        .append("stock", 150)
                        .append("description", "索尼WH-1000XM5无线降噪耳机"),
                new Document()
                        .append("name", "Apple Watch Ultra 2")
                        .append("price", 6499)
                        .append("category", "手表")
                        .append("brand", "Apple")
                        .append("stock", 30)
                        .append("description", "Apple Watch Ultra 2 运动手表")
        );
        products.insertMany(productList);
        System.out.println("已准备测试数据: " + productList.size() + " 条记录");
    }

    /**
     * 列出所有索引
     */
    private static void listIndexes(MongoCollection<Document> products) {
        System.out.println("\n--- 索引列表 ---");

        products.listIndexes().forEach(doc ->
                System.out.println("  索引: " + doc.getString("name") + " -> " + doc.get("key"))
        );
    }

    /**
     * 创建单字段索引
     */
    private static void createSingleFieldIndex(MongoCollection<Document> products) {
        System.out.println("\n--- 单字段索引 ---");

        // 创建升序索引
        products.createIndex(Indexes.ascending("category"));
        System.out.println("  已创建 category 升序索引");

        // 创建降序索引
        products.createIndex(Indexes.descending("price"));
        System.out.println("  已创建 price 降序索引");

        // 创建唯一索引
        products.createIndex(Indexes.ascending("name"), new com.mongodb.client.model.IndexOptions().unique(true));
        System.out.println("  已创建 name 唯一索引");
    }

    /**
     * 创建复合索引
     */
    private static void createCompoundIndex(MongoCollection<Document> products) {
        System.out.println("\n--- 复合索引 ---");

        // 创建复合索引（类别升序 + 品牌升序 + 价格降序）
        products.createIndex(Indexes.compoundIndex(
                Indexes.ascending("category"),
                Indexes.ascending("brand"),
                Indexes.descending("price")
        ));
        System.out.println("  已创建 category + brand + price 复合索引");
    }

    /**
     * 创建文本索引
     */
    private static void createTextIndex(MongoCollection<Document> products) {
        System.out.println("\n--- 文本索引 ---");

        // MongoDB 每个集合只能有一个文本索引
        // 先删除可能存在的文本索引
        try {
            products.dropIndex("name_text");
        } catch (Exception ignored) {}
        try {
            products.dropIndex("description_text");
        } catch (Exception ignored) {}

        // 创建复合文本索引（支持同时搜索多个字段）
        products.createIndex(Indexes.compoundIndex(
                Indexes.text("name"),
                Indexes.text("description")
        ));
        System.out.println("  已创建 name + description 复合文本索引");

        // 文本搜索示例
        System.out.println("  搜索 '手机':");
        products.aggregate(Arrays.asList(
                Aggregates.match(Filters.text("手机")),
                Aggregates.limit(3)
        )).forEach(doc ->
                System.out.println("    - " + doc.getString("name"))
        );
    }

    /**
     * 创建唯一索引
     */
    private static void createUniqueIndex(MongoCollection<Document> products) {
        System.out.println("\n--- 唯一索引 ---");

        // 为 sku 字段创建稀疏唯一索引
        products.createIndex(
                Indexes.ascending("sku"),
                new com.mongodb.client.model.IndexOptions().unique(true).sparse(true)
        );
        System.out.println("  已创建 sku 稀疏唯一索引");
    }

    /**
     * 验证索引使用
     */
    private static void verifyIndexUsage(MongoCollection<Document> products) {
        System.out.println("\n--- 索引使用验证 ---");

        // 使用 explain 分析查询计划
        System.out.println("  按 category 查询:");
        products.find(Filters.eq("category", "手机"))
                .limit(1)
                .explain()
                .get("queryPlanner", Document.class)
                .get("winningPlan", Document.class)
                .forEach((k, v) -> System.out.println("    " + k + ": " + v));

        // 复合查询测试
        System.out.println("\n  按 category + brand 查询:");
        products.find(Filters.and(
                Filters.eq("category", "手机"),
                Filters.eq("brand", "Apple")
        )).forEach(doc ->
                System.out.println("    " + doc.getString("name") + ": ¥" + doc.getInteger("price"))
        );
    }

    /**
     * 删除索引
     */
    private static void dropIndexDemo(MongoCollection<Document> products) {
        System.out.println("\n--- 删除索引 ---");

        // 删除单个索引
        products.dropIndex("category_1");
        System.out.println("  已删除 category_1 索引");

        // 删除所有索引
        products.dropIndexes();
        System.out.println("  已删除所有自定义索引（保留 _id 索引）");
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
