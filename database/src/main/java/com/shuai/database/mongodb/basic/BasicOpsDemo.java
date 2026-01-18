package com.shuai.database.mongodb.basic;

import com.mongodb.client.model.Filters;
import com.shuai.database.mongodb.MongoDbConfig;
import com.shuai.database.mongodb.MongoDbTemplate;
import org.bson.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MongoDB 基本操作演示
 * 演示 CRUD 操作的完整流程
 */
public class BasicOpsDemo {

    private static final String COLLECTION_NAME = "users";

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 基本操作演示");
        System.out.println("=".repeat(50));

        // 初始化配置
        MongoDbConfig.init();

        // 创建模板
        MongoDbTemplate template = new MongoDbTemplate(COLLECTION_NAME);

        // 清空测试数据
        template.deleteAll();
        System.out.println("已清空测试数据");

        // 1. 插入操作
        insertDemo(template);

        // 2. 查询操作
        queryDemo(template);

        // 3. 更新操作
        updateDemo(template);

        // 4. 删除操作
        deleteDemo(template);

        // 5. 批量操作
        batchDemo(template);

        System.out.println("\n[成功] 基本操作演示完成！");
    }

    /**
     * 插入演示
     */
    private static void insertDemo(MongoDbTemplate template) {
        System.out.println("\n--- 插入操作 ---");

        // 插入单个文档
        Document user1 = new Document()
                .append("name", "张三")
                .append("age", 25)
                .append("email", "zhangsan@example.com")
                .append("status", "active")
                .append("createdAt", new Date());
        template.insert(user1);
        System.out.println("  插入用户1: " + user1.getObjectId("_id"));

        // 插入多个文档
        List<Document> users = Arrays.asList(
                new Document()
                        .append("name", "李四")
                        .append("age", 30)
                        .append("email", "lisi@example.com")
                        .append("status", "active"),
                new Document()
                        .append("name", "王五")
                        .append("age", 28)
                        .append("email", "wangwu@example.com")
                        .append("status", "inactive")
        );
        template.insertMany(users);
        System.out.println("  批量插入用户: " + users.size() + " 条");

        // 保存（插入或更新）
        Document userToSave = new Document()
                .append("name", "赵六")
                .append("age", 35);
        template.save(userToSave);
        System.out.println("  保存用户: " + userToSave.getObjectId("_id"));
    }

    /**
     * 查询演示
     */
    private static void queryDemo(MongoDbTemplate template) {
        System.out.println("\n--- 查询操作 ---");

        // 查询所有
        List<Document> allUsers = template.findAll();
        System.out.println("  查询全部: " + allUsers.size() + " 条记录");

        // 条件查询
        List<Document> activeUsers = template.find(
                new Document("status", "active")
        );
        System.out.println("  活跃用户: " + activeUsers.size() + " 条记录");

        // 查询单个
        template.findOne(new Document("name", "张三"))
                .ifPresent(doc -> System.out.println("  找到用户: " + doc.getString("name")));

        // 统计数量
        long count = template.count();
        System.out.println("  总记录数: " + count);

        // 分页查询
        List<Document> page1 = template.findPage(new Document(), 0, 2);
        System.out.println("  第一页: " + page1.size() + " 条记录");
    }

    /**
     * 更新演示
     */
    private static void updateDemo(MongoDbTemplate template) {
        System.out.println("\n--- 更新操作 ---");

        // 更新单个
        long modified = template.set(
                new Document("name", "张三"),
                "age", 26
        );
        System.out.println("  更新年龄: " + modified + " 条记录");

        // 递增
        template.inc(new Document("name", "李四"), "age", 1);
        template.findOne(new Document("name", "李四"))
                .ifPresent(doc -> System.out.println("  李四年龄+1: " + doc.getInteger("age")));

        // 数组操作
        template.push(new Document("name", "张三"), "tags", "vip");
        template.findOne(new Document("name", "张三"))
                .ifPresent(doc -> System.out.println("  张三标签: " + doc.getList("tags", String.class)));
    }

    /**
     * 删除演示
     */
    private static void deleteDemo(MongoDbTemplate template) {
        System.out.println("\n--- 删除操作 ---");

        // 先插入一条要删除的记录
        Document toDelete = new Document("name", "临时用户").append("age", 20);
        template.insert(toDelete);
        System.out.println("  插入临时用户: " + toDelete.getObjectId("_id"));

        // 删除单个
        long deleted = template.deleteOne(new Document("name", "临时用户"));
        System.out.println("  删除临时用户: " + deleted + " 条记录");

        // 删除不活跃用户
        long inactiveDeleted = template.deleteMany(new Document("status", "inactive"));
        System.out.println("  删除不活跃用户: " + inactiveDeleted + " 条记录");

        // 查看剩余数量
        System.out.println("  剩余记录数: " + template.count());
    }

    /**
     * 批量操作演示
     */
    private static void batchDemo(MongoDbTemplate template) {
        System.out.println("\n--- 批量操作演示 ---");

        // 批量插入
        List<Document> batchUsers = Arrays.asList(
                new Document().append("name", "批量用户1").append("age", 20),
                new Document().append("name", "批量用户2").append("age", 21),
                new Document().append("name", "批量用户3").append("age", 22)
        );
        template.insertMany(batchUsers);
        System.out.println("  批量插入: " + batchUsers.size() + " 条记录");

        // 批量更新
        template.updateMany(
                Filters.regex("name", "批量用户"),
                new Document("$inc", new Document("age", 10))
        );
        System.out.println("  批量更新年龄+10");

        // 批量删除
        long batchDeleted = template.deleteMany(
                Filters.regex("name", "批量用户")
        );
        System.out.println("  批量删除: " + batchDeleted + " 条记录");
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
