package com.shuai.database.mongodb.aggregation;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.shuai.database.mongodb.MongoDbConfig;
import org.bson.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MongoDB 聚合管道演示
 * 演示聚合操作的各种用法
 */
public class AggregationDemo {

    private static final String COLLECTION_NAME = "orders";

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 聚合管道演示");
        System.out.println("=".repeat(50));

        MongoDbConfig.init();
        MongoDatabase db = MongoDbConfig.getDatabase();
        MongoCollection<Document> orders = db.getCollection(COLLECTION_NAME);

        // 清空并准备测试数据
        orders.deleteMany(new Document());
        prepareTestData(orders);

        // 1. $match - 筛选
        matchDemo(orders);

        // 2. $group - 分组统计
        groupDemo(orders);

        // 3. $project - 投影
        projectDemo(orders);

        // 4. $sort - 排序
        sortDemo(orders);

        // 5. $limit 和 $skip
        limitSkipDemo(orders);

        // 6. $unwind - 数组展开
        unwindDemo(orders);

        // 7. $lookup - 连接
        lookupDemo(db);

        // 8. 多阶段聚合
        multiStageDemo(orders);

        System.out.println("\n[成功] 聚合管道演示完成！");
    }

    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM").parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 准备测试数据
     */
    private static void prepareTestData(MongoCollection<Document> orders) {
        List<Document> orderList = Arrays.asList(
                new Document()
                        .append("orderNo", "ORD001")
                        .append("userId", 1)
                        .append("userName", "张三")
                        .append("amount", 5000)
                        .append("status", "completed")
                        .append("items", Arrays.asList(
                                new Document("productId", 1).append("name", "iPhone 15").append("qty", 1).append("price", 5000),
                                new Document("productId", 3).append("name", "AirPods").append("qty", 1).append("price", 1500)
                        ))
                        .append("createdAt", parseDate("2024-01")),
                new Document()
                        .append("orderNo", "ORD002")
                        .append("userId", 2)
                        .append("userName", "李四")
                        .append("amount", 15000)
                        .append("status", "completed")
                        .append("items", Arrays.asList(
                                new Document("productId", 2).append("name", "MacBook").append("qty", 1).append("price", 15000)
                        ))
                        .append("createdAt", parseDate("2024-01")),
                new Document()
                        .append("orderNo", "ORD003")
                        .append("userId", 1)
                        .append("userName", "张三")
                        .append("amount", 3000)
                        .append("status", "pending")
                        .append("items", Arrays.asList(
                                new Document("productId", 4).append("name", "iPad").append("qty", 1).append("price", 3000)
                        ))
                        .append("createdAt", parseDate("2024-02")),
                new Document()
                        .append("orderNo", "ORD004")
                        .append("userId", 3)
                        .append("userName", "王五")
                        .append("amount", 2000)
                        .append("status", "completed")
                        .append("items", Arrays.asList(
                                new Document("productId", 3).append("name", "AirPods").append("qty", 2).append("price", 1000)
                        ))
                        .append("createdAt", parseDate("2024-02")),
                new Document()
                        .append("orderNo", "ORD005")
                        .append("userId", 2)
                        .append("userName", "李四")
                        .append("amount", 2000)
                        .append("status", "cancelled")
                        .append("items", Arrays.asList(
                                new Document("productId", 5).append("name", "Watch").append("qty", 1).append("price", 2000)
                        ))
                        .append("createdAt", parseDate("2024-03"))
        );
        orders.insertMany(orderList);
        System.out.println("已准备测试数据: " + orderList.size() + " 条订单");
    }

    /**
     * $match 演示
     */
    private static void matchDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $match 筛选 ---");

        // 筛选已完成订单
        orders.aggregate(Arrays.asList(
                Aggregates.match(Filters.eq("status", "completed"))
        )).forEach(doc ->
                System.out.println("  " + doc.getString("orderNo") + ": ¥" + doc.getInteger("amount"))
        );
    }

    /**
     * $group 演示
     */
    private static void groupDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $group 分组统计 ---");

        // 按状态统计
        System.out.println("  按状态统计:");
        orders.aggregate(Arrays.asList(
                Aggregates.group("$status",
                        com.mongodb.client.model.Accumulators.sum("count", 1),
                        com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount")
                )
        )).forEach(doc ->
                System.out.println("    " + doc.get("_id") + ": " + doc.getInteger("count") + " 单, ¥" + doc.getInteger("totalAmount"))
        );

        // 按用户统计
        System.out.println("  用户消费统计:");
        orders.aggregate(Arrays.asList(
                Aggregates.group("$userId",
                        com.mongodb.client.model.Accumulators.sum("orderCount", 1),
                        com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount"),
                        com.mongodb.client.model.Accumulators.avg("avgAmount", "$amount")
                )
        )).forEach(doc ->
                System.out.println("    用户" + doc.get("_id") + ": " + doc.getInteger("orderCount") +
                        " 单, 总计¥" + doc.getInteger("totalAmount") + ", 平均¥" + doc.getDouble("avgAmount"))
        );
    }

    /**
     * $project 演示
     */
    private static void projectDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $project 投影 ---");

        // 选择字段
        orders.aggregate(Arrays.asList(
                Aggregates.project(
                        Document.parse("{orderNo: 1, userName: 1, amount: 1, _id: 0}")
                )
        )).forEach(doc ->
                System.out.println("  " + doc)
        );

        // 添加计算字段
        System.out.println("  添加折扣字段:");
        orders.aggregate(Arrays.asList(
                Aggregates.project(
                        Document.parse("{orderNo: 1, amount: 1, discount: {$multiply: ['$amount', 0.9]}, finalAmount: {$multiply: ['$amount', 0.9]}}")
                )
        )).forEach(doc ->
                System.out.println("    " + doc.getString("orderNo") + ": ¥" + doc.getInteger("amount") +
                        " -> ¥" + doc.getDouble("finalAmount"))
        );
    }

    /**
     * $sort 演示
     */
    private static void sortDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $sort 排序 ---");

        orders.aggregate(Arrays.asList(
                Aggregates.sort(Sorts.descending("amount")),
                Aggregates.limit(3)
        )).forEach(doc ->
                System.out.println("  Top3: " + doc.getString("orderNo") + ": ¥" + doc.getInteger("amount"))
        );
    }

    /**
     * $limit 和 $skip 演示
     */
    private static void limitSkipDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $limit 和 $skip ---");

        System.out.println("  前3条（跳过第1条）:");
        orders.aggregate(Arrays.asList(
                Aggregates.skip(1),
                Aggregates.limit(3)
        )).forEach(doc ->
                System.out.println("    " + doc.getString("orderNo"))
        );
    }

    /**
     * $unwind 演示
     */
    private static void unwindDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- $unwind 展开数组 ---");

        System.out.println("  订单商品明细:");
        orders.aggregate(Arrays.asList(
                Aggregates.unwind("$items"),
                Aggregates.project(
                        Document.parse("{orderNo: 1, itemName: '$items.name', qty: '$items.qty', price: '$items.price'}")
                ),
                Aggregates.limit(10)
        )).forEach(doc ->
                System.out.println("    " + doc.getString("orderNo") + ": " + doc.getString("itemName") +
                        " x" + doc.getInteger("qty") + " = ¥" + doc.getInteger("price"))
        );
    }

    /**
     * $lookup 演示
     */
    private static void lookupDemo(MongoDatabase db) {
        System.out.println("\n--- $lookup 连接 ---");

        // 创建用户集合用于演示
        MongoCollection<Document> users = db.getCollection("users_temp");
        users.deleteMany(new Document());
        users.insertMany(Arrays.asList(
                new Document("_id", 1).append("name", "张三").append("city", "北京"),
                new Document("_id", 2).append("name", "李四").append("city", "上海"),
                new Document("_id", 3).append("name", "王五").append("city", "广州")
        ));

        // 关联查询
        System.out.println("  订单关联用户:");
        db.getCollection("orders").aggregate(Arrays.asList(
                Aggregates.lookup("users_temp", "userId", "_id", "userInfo"),
                Aggregates.limit(3)
        )).forEach(doc -> {
            System.out.println("    " + doc.getString("orderNo") + ": " + doc.getString("userName"));
        });

        // 清理临时集合
        users.drop();
    }

    /**
     * 多阶段聚合演示
     */
    private static void multiStageDemo(MongoCollection<Document> orders) {
        System.out.println("\n--- 多阶段聚合 ---");

        // 复杂报表
        System.out.println("  用户月度消费报表:");
        orders.aggregate(Arrays.asList(
                // 筛选已完成订单
                Aggregates.match(Filters.eq("status", "completed")),
                // 按用户分组
                Aggregates.group("$userId",
                        com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount"),
                        com.mongodb.client.model.Accumulators.sum("orderCount", 1)
                ),
                // 排序
                Aggregates.sort(Sorts.descending("totalAmount")),
                // 限制
                Aggregates.limit(5)
        )).forEach(doc ->
                System.out.println("    用户" + doc.get("_id") + ": " + doc.getInteger("orderCount") +
                        " 单, 总计¥" + doc.getInteger("totalAmount"))
        );
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
