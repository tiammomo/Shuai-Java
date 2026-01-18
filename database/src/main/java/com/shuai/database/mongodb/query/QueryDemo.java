package com.shuai.database.mongodb.query;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.shuai.database.mongodb.MongoDbConfig;
import com.shuai.database.mongodb.MongoDbTemplate;
import org.bson.Document;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * MongoDB 查询操作演示
 * 演示各种查询条件的用法
 */
public class QueryDemo {

    private static final String COLLECTION_NAME = "products";

    public static void run() throws Exception {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 查询操作演示");
        System.out.println("=".repeat(50));

        MongoDbConfig.init();
        MongoDbTemplate template = new MongoDbTemplate(COLLECTION_NAME);

        // 清空并准备测试数据
        template.deleteAll();
        prepareTestData(template);

        // 1. 基本查询
        basicQueryDemo(template);

        // 2. 比较操作符
        comparisonDemo(template);

        // 3. 逻辑操作符
        logicalDemo(template);

        // 4. 数组查询
        arrayQueryDemo(template);

        // 5. 嵌套文档查询
        nestedQueryDemo(template);

        // 6. 排序和分页
        sortAndPageDemo(template);

        System.out.println("\n[成功] 查询操作演示完成！");
    }

    /**
     * 准备测试数据
     */
    private static void prepareTestData(MongoDbTemplate template) {
        List<Document> products = Arrays.asList(
                new Document()
                        .append("name", "iPhone 15")
                        .append("price", 6999)
                        .append("category", "手机")
                        .append("stock", 100)
                        .append("tags", Arrays.asList("旗舰", "热销"))
                        .append("specs", new Document("color", "蓝色").append("storage", "256GB")),
                new Document()
                        .append("name", "MacBook Pro")
                        .append("price", 14999)
                        .append("category", "电脑")
                        .append("stock", 50)
                        .append("tags", Arrays.asList("专业", "高端"))
                        .append("specs", new Document("color", "灰色").append("memory", "16GB")),
                new Document()
                        .append("name", "AirPods Pro")
                        .append("price", 1899)
                        .append("category", "耳机")
                        .append("stock", 200)
                        .append("tags", Arrays.asList("无线", "降噪"))
                        .append("specs", new Document("color", "白色")),
                new Document()
                        .append("name", "iPad Air")
                        .append("price", 4799)
                        .append("category", "平板")
                        .append("stock", 80)
                        .append("tags", Arrays.asList("轻薄", "热销"))
                        .append("specs", new Document("color", "紫色")),
                new Document()
                        .append("name", "Apple Watch")
                        .append("price", 2999)
                        .append("category", "手表")
                        .append("stock", 150)
                        .append("tags", Arrays.asList("健康", "运动"))
                        .append("specs", new Document("color", "黑色"))
        );
        template.insertMany(products);
        System.out.println("已准备测试数据: " + products.size() + " 条记录");
    }

    /**
     * 基本查询演示
     */
    private static void basicQueryDemo(MongoDbTemplate template) {
        System.out.println("\n--- 基本查询 ---");

        // 查询所有
        template.findAll().forEach(doc ->
                System.out.println("  - " + doc.getString("name") + ": ¥" + doc.getInteger("price"))
        );

        // 等值查询
        template.findOne(Filters.eq("name", "iPhone 15"))
                .ifPresent(doc -> System.out.println("\n  找到 iPhone: " + doc.getString("name")));

        // IN 查询
        List<Document> appleProducts = template.find(Filters.in("name", "iPhone 15", "MacBook Pro"));
        System.out.println("  IN 查询结果: " + appleProducts.size() + " 条");
    }

    /**
     * 比较操作符演示
     */
    private static void comparisonDemo(MongoDbTemplate template) {
        System.out.println("\n--- 比较操作符 ---");

        // 大于
        List<Document> expensive = template.find(Filters.gt("price", 5000));
        System.out.println("  价格 > 5000: " + expensive.size() + " 个");

        // 大于等于
        List<Document> expensiveOrEqual = template.find(Filters.gte("price", 5000));
        System.out.println("  价格 >= 5000: " + expensiveOrEqual.size() + " 个");

        // 小于
        List<Document> cheap = template.find(Filters.lt("price", 2000));
        System.out.println("  价格 < 2000: " + cheap.size() + " 个");

        // 范围查询
        List<Document> range = template.find(
                Filters.and(
                        Filters.gte("price", 2000),
                        Filters.lte("price", 5000)
                )
        );
        System.out.println("  2000 <= 价格 <= 5000: " + range.size() + " 个");

        // 不等于
        List<Document> notApple = template.find(Filters.ne("category", "手表"));
        System.out.println("  非手表类: " + notApple.size() + " 个");
    }

    /**
     * 逻辑操作符演示
     */
    private static void logicalDemo(MongoDbTemplate template) {
        System.out.println("\n--- 逻辑操作符 ---");

        // AND
        List<Document> andQuery = template.find(
                Filters.and(
                        Filters.gt("price", 2000),
                        Filters.lt("price", 10000)
                )
        );
        System.out.println("  AND: 2000 < 价格 < 10000: " + andQuery.size() + " 个");

        // OR
        List<Document> orQuery = template.find(
                Filters.or(
                        Filters.eq("category", "手机"),
                        Filters.eq("category", "电脑")
                )
        );
        System.out.println("  OR: 手机或电脑: " + orQuery.size() + " 个");

        // NOT
        List<Document> notExpensive = template.find(Filters.not(Filters.gt("price", 10000)));
        System.out.println("  NOT: 价格 <= 10000: " + notExpensive.size() + " 个");
    }

    /**
     * 数组查询演示
     */
    private static void arrayQueryDemo(MongoDbTemplate template) {
        System.out.println("\n--- 数组查询 ---");

        // 数组包含
        List<Document> hotProducts = template.find(Filters.all("tags", "热销"));
        System.out.println("  包含 '热销' 标签: " + hotProducts.size() + " 个");

        // 数组包含多个值
        List<Document> multiTags = template.find(
                Filters.all("tags", Arrays.asList("旗舰", "热销"))
        );
        System.out.println("  包含 '旗舰' 和 '热销': " + multiTags.size() + " 个");

        // 数组长度
        List<Document> twoTags = template.find(Filters.size("tags", 2));
        System.out.println("  标签数量为2: " + twoTags.size() + " 个");

        // 数组元素匹配
        List<Document> hasWireless = template.find(Filters.in("tags", "无线"));
        System.out.println("  包含 '无线' 标签: " + hasWireless.size() + " 个");
    }

    /**
     * 嵌套文档查询演示
     */
    private static void nestedQueryDemo(MongoDbTemplate template) {
        System.out.println("\n--- 嵌套文档查询 ---");

        // 嵌套字段查询
        List<Document> blueProducts = template.find(Filters.eq("specs.color", "蓝色"));
        System.out.println("  蓝色产品: " + blueProducts.size() + " 个");

        // 嵌套文档精确匹配
        List<Document> grayMac = template.find(
                new Document("specs.color", "灰色").append("specs.memory", "16GB")
        );
        System.out.println("  灰色+16GB内存: " + grayMac.size() + " 个");
    }

    /**
     * 排序和分页演示
     */
    private static void sortAndPageDemo(MongoDbTemplate template) {
        System.out.println("\n--- 排序和分页 ---");

        // 按价格升序
        List<Document> asc = template.findAll();
        asc.sort((a, b) -> Integer.compare(
                a.getInteger("price", 0),
                b.getInteger("price", 0)
        ));
        System.out.println("  按价格升序:");
        asc.forEach(doc -> System.out.println("    " + doc.getString("name") + ": ¥" + doc.getInteger("price")));

        // 按价格降序（前3个）
        List<Document> top3 = template.findPage(null, 0, 3);
        top3.sort((a, b) -> Integer.compare(
                b.getInteger("price", 0),
                a.getInteger("price", 0)
        ));
        System.out.println("  价格前3:");
        top3.forEach(doc -> System.out.println("    " + doc.getString("name") + ": ¥" + doc.getInteger("price")));
    }

    public static void main(String[] args) throws Exception {
        run();
    }
}
