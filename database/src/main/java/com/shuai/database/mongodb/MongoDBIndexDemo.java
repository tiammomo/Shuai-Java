package com.shuai.database.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * MongoDB 索引操作演示类
 *
 * ============================================================
 * 索引（Index）是什么？
 * ------------------------------------------------------------
 * 索引就像书籍的目录，可以帮助数据库快速定位数据，
 * 而不需要扫描整个集合（表）。
 *
 * 无索引：逐行检查所有数据 = 翻阅整本书找某一页
 * 有索引：直接定位 = 查目录跳转到目标页
 *
 * ============================================================
 * 为什么需要索引？
 * ------------------------------------------------------------
 * - 查询加速：百万级数据从秒级降到毫秒级
 * - 唯一约束：保证数据唯一性（如邮箱、手机号）
 * - 排序优化：避免内存排序
 * - 范围查询：高效处理 >、<、>=、<=
 *
 * ============================================================
 * 索引类型详解
 * ------------------------------------------------------------
 * 1. 单字段索引：单个字段的索引，最常用
 * 2. 复合索引：多个字段组合索引，适合多条件查询
 * 3. 文本索引：全文搜索，支持关键词匹配
 * 4. 地理空间索引：位置查询，支持附近搜索
 * 5. 哈希索引：均匀分布，适合分片键
 * 6. 部分索引：只索引满足条件的文档
 * 7. TTL索引：自动删除过期文档
 *
 * @author Shuai
 */
public class MongoDBIndexDemo {

    // ==================== 连接配置 ====================
    /** MongoDB 连接地址，包含认证信息 */
    private static final String MONGO_URI = "mongodb://ubuntu:ubuntu@localhost:27017/?authSource=admin";

    /** 使用的数据库名称 */
    private static final String DATABASE_NAME = "testdb";

    /** 测试集合前缀，用于演示完成后自动清理 */
    private static final String COLLECTION_PREFIX = "index_demo_";

    // MongoDB 客户端和数据库实例
    private MongoClient mongoClient;
    private MongoDatabase database;

    // ==================== 构造方法 ====================

    /**
     * 默认构造函数
     * 初始化 MongoDB 连接和数据库实例
     *
     * 连接流程：
     * 1. 创建 MongoClients（连接池管理器）
     * 2. 获取指定数据库的引用
     */
    public MongoDBIndexDemo() {
        this.mongoClient = MongoClients.create(MONGO_URI);
        this.database = mongoClient.getDatabase(DATABASE_NAME);
    }

    // ==================== 主入口方法 ====================

    /**
     * 执行所有索引演示
     *
     * 执行流程：
     * 1. prepareTestData()    -> 准备测试数据
     * 2. createSingleFieldIndexes() -> 创建单字段索引
     * 3. createCompoundIndex()     -> 创建复合索引
     * 4. createTextIndex()         -> 创建文本索引
     * 5. createGeoIndex()          -> 创建地理空间索引
     * 6. createPartialIndex()      -> 创建部分索引
     * 7. createTtlIndex()          -> 创建 TTL 过期索引
     * 8. listAndAnalyzeIndexes()   -> 查看和分析索引
     * 9. geoQueryDemo()            -> 地理空间查询演示
     * 10. cleanup()                -> 清理测试数据
     */
    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       MongoDB 索引操作演示");
        System.out.println("=".repeat(50));

        try {
            // 第一步：准备测试数据
            // 为什么要准备数据？因为索引需要数据才能体现效果
            prepareTestData();

            // 第二步：创建各种索引
            createSingleFieldIndexes();
            createCompoundIndex();
            createTextIndex();
            createGeoIndex();
            createPartialIndex();
            createTtlIndex();

            // 第三步：查看和分析索引效果
            listAndAnalyzeIndexes();

            // 第四步：演示地理空间查询
            geoQueryDemo();

            System.out.println("\n[成功] MongoDB 索引演示完成！");

        } catch (Exception e) {
            System.err.println("MongoDB 操作异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 最后：清理测试数据，保持环境整洁
            cleanup();
            mongoClient.close();
        }
    }

    // ==================== 测试数据准备 ====================

    /**
     * 准备测试数据
     *
     * 数据设计原则：
     * 1. 字段丰富：包含各种类型的字段（字符串、数字、日期、数组、嵌套文档）
     * 2. 数据量适中：100条数据足以演示索引效果
     * 3. 业务场景真实：模拟用户数据和地理位置数据
     *
     * 创建的集合：
     * - index_demo_users : 用户数据，用于演示普通索引
     * - index_demo_places : 地点数据，用于演示地理空间索引
     */
    private void prepareTestData() {
        String collection = COLLECTION_PREFIX + "users";
        System.out.println("\n--- 准备测试数据 ---");
        System.out.println("  集合: " + collection);

        MongoCollection<Document> coll = database.getCollection(collection);

        // 批量插入用户数据
        List<Document> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            users.add(new Document()
                // 用户ID
                .append("userId", i)
                // 用户名，用于文本搜索
                .append("username", "user_" + i)
                // 邮箱，用于唯一索引演示
                .append("email", "user" + i + "@example.com")
                // 年龄，用于范围查询
                .append("age", 18 + (i % 50))
                // 状态，用于部分索引演示
                .append("status", i % 2 == 0 ? "active" : "inactive")
                // 创建时间，用于排序
                .append("createdAt", new Date(System.currentTimeMillis() - (i * 1000000)))
                // 标签数组，用于多键索引演示
                .append("tags", List.of("tag" + (i % 5), "category" + (i % 3)))
                // 个人简介，用于文本索引演示
                .append("bio", "This is user " + i + " with some description for text search")
            );
        }
        coll.insertMany(users);
        System.out.println("  插入用户数据: " + users.size() + " 条");

        // 准备地理空间测试数据
        // GeoJSON 格式：必须有 type 和 coordinates 字段
        String geoCollection = COLLECTION_PREFIX + "places";
        MongoCollection<Document> places = database.getCollection(geoCollection);

        // 创建5个地点，包含位置信息（经纬度）
        // 坐标格式：[经度, 纬度]
        // 纽约市附近模拟数据
        List<Document> placeList = List.of(
            new Document()
                .append("name", "咖啡馆")
                .append("location", new Document("type", "Point")
                    .append("coordinates", List.of(-73.97, 40.77))),
            new Document()
                .append("name", "餐厅")
                .append("location", new Document("type", "Point")
                    .append("coordinates", List.of(-73.96, 40.78))),
            new Document()
                .append("name", "公园")
                .append("location", new Document("type", "Point")
                    .append("coordinates", List.of(-73.95, 40.76))),
            new Document()
                .append("name", "书店")
                .append("location", new Document("type", "Point")
                    .append("coordinates", List.of(-73.98, 40.75))),
            new Document()
                .append("name", "超市")
                .append("location", new Document("type", "Point")
                    .append("coordinates", List.of(-73.94, 40.79)))
        );
        places.insertMany(placeList);
        System.out.println("  插入地理数据: " + placeList.size() + " 条");
    }

    // ==================== 单字段索引 ====================

    /**
     * 创建单字段索引
     *
     * 单字段索引是最基础的索引类型，只针对一个字段建立索引。
     *
     * 索引方向说明：
     * - 1  : 升序索引（从小到大）
     * - -1 : 降序索引（从大到小）
     *
     * 注意：MongoDB 查询可以双向利用单字段索引，
     *       所以升序和降序索引在实际使用中差别不大。
     *
     * 索引选项 IndexOptions：
     * - unique(true)     : 唯一索引，不允许重复值
     * - sparse(true)     : 稀疏索引，只索引非 null 值
     * - name("xxx")      : 自定义索引名称
     * - expireAfter(30)  : TTL 索引，30秒后过期
     */
    private void createSingleFieldIndexes() {
        System.out.println("\n--- 单字段索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "users");

        try {
            // 升序索引（默认）
            // 使用场景：按用户名查询、按ID查询
            coll.createIndex(Indexes.ascending("username"));
            System.out.println("  [创建] username 升序索引");

            // 降序索引
            // 使用场景：按年龄降序排列、按时间倒序
            coll.createIndex(Indexes.descending("age"));
            System.out.println("  [创建] age 降序索引");

            // 唯一索引
            // 使用场景：邮箱、手机号、身份证号等需要唯一的字段
            // 效果：插入重复邮箱会报错
            IndexOptions uniqueOptions = new IndexOptions()
                .unique(true)
                .name("idx_user_email");
            coll.createIndex(Indexes.ascending("email"), uniqueOptions);
            System.out.println("  [创建] email 唯一索引");

            // 稀疏索引
            // 使用场景：可选字段（如手机号可能为空）
            // 特点：只索引非 null 值，节省空间
            IndexOptions sparseOptions = new IndexOptions()
                .sparse(true)
                .name("idx_user_phone");
            coll.createIndex(Indexes.ascending("phone"), sparseOptions);
            System.out.println("  [创建] phone 稀疏索引");

            System.out.println("  单字段索引创建成功！");

        } catch (Exception e) {
            // 索引已存在时忽略错误
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 复合索引 ====================

    /**
     * 创建复合索引
     *
     * 复合索引是多个字段组合的索引，适合多条件查询。
     *
     * 字段顺序原则（重要！）：
     * 1. 等值查询字段放前面（如：status = 'active'）
     * 2. 排序字段放中间
     * 3. 范围查询字段放最后（如：age >= 18）
     *
     * 示例：查询活跃用户并按时间排序
     * 索引：{ status: 1, createdAt: -1 }
     * 查询：db.users.find({status: 'active'}).sort({createdAt: -1})
     *       ^^^^^^^          ^^^^^^^^^^^^^^^^^^^^
     *       完全匹配          使用索引排序
     *
     * 多键索引：
     * 当索引字段是数组时，MongoDB 会自动为数组的每个元素创建索引项
     * 这就是多键索引，不需要额外配置
     */
    private void createCompoundIndex() {
        System.out.println("\n--- 复合索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "users");

        try {
            // 复合索引：状态 + 创建时间
            // 适用查询：WHERE status = ? ORDER BY createdAt DESC
            coll.createIndex(Indexes.compoundIndex(
                Indexes.ascending("status"),
                Indexes.descending("createdAt")
            ));
            System.out.println("  [创建] status + createdAt 复合索引");

            // 多键索引
            // tags 是数组字段，自动创建多键索引
            // 查询 db.users.find({tags: 'tag1'}) 会使用此索引
            coll.createIndex(Indexes.ascending("tags"));
            System.out.println("  [创建] tags 多键索引");

            // 嵌套文档索引
            // 使用点号访问嵌套字段
            // 索引 address.city 后，查询 db.users.find({'address.city': '北京'}) 会使用索引
            coll.createIndex(Indexes.ascending("address.city"));
            System.out.println("  [创建] address.city 嵌套索引");

            System.out.println("  复合索引创建成功！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 文本索引 ====================

    /**
     * 创建文本索引
     *
     * 文本索引用于全文搜索，支持关键词匹配。
     *
     * 与正则表达式的区别：
     * - 正则：db.collection.find({name: /^keyword/}) 只能前缀匹配
     * - 文本索引：db.collection.find({$text: {$search: 'keyword'}}) 任意位置匹配
     *
     * 文本索引特点：
     * 1. 支持词干提取（英文）：run 匹配 running
     * 2. 支持停用词过滤（the, a, is 等）
     * 3. 支持权重设置：匹配 title 字段权重更高
     * 4. 限制：每个集合只能有一个文本索引（可包含多个字段）
     *
     * 权重作用：
     * 搜索 "mongodb tutorial" 时，title 命中的文档得分更高
     */
    private void createTextIndex() {
        System.out.println("\n--- 文本索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "users");

        try {
            // 基本文本索引
            // 在 bio 字段上创建文本索引
            coll.createIndex(Indexes.text("bio"));
            System.out.println("  [创建] bio 文本索引");

            // 带权重的复合文本索引
            // 同时索引 bio 和 username，bio 权重更高
            IndexOptions textOptions = new IndexOptions()
                .weights(new Document("bio", 10).append("username", 5))
                .name("idx_text_bio_username");
            coll.createIndex(Indexes.compoundIndex(
                Indexes.text("bio"),
                Indexes.text("username")
            ), textOptions);
            System.out.println("  [创建] bio + username 带权重文本索引");

            // 执行文本搜索
            System.out.println("\n  执行文本搜索 'user description':");
            // $text 操作符进行全文搜索
            // 默认使用 AND 逻辑（包含所有词）
            coll.find(Filters.text("user description"))
                .limit(5)
                .forEach(doc -> System.out.println("    用户: " + doc.getString("username")));

            System.out.println("  文本索引创建成功！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 地理空间索引 ====================

    /**
     * 创建地理空间索引
     *
     * 地理空间索引用于位置相关的查询，如附近搜索、范围查询等。
     *
     * 索引类型：
     * - 2dsphere  : 球面索引，适用于地球表面（推荐）
     *              支持 $near、$geoNear 等操作符
     *              坐标格式：[经度, 纬度]
     *
     * - 2d        : 平面索引，适用于小范围平面
     *              不考虑地球曲率，适合游戏地图等
     *
     * - geoHaystack : 干草堆索引，用于特定区域优化查询
     *
     * GeoJSON 格式：
     * 必须包含 type 和 coordinates 字段
     * {
     *   "type": "Point",
     *   "coordinates": [经度, 纬度]
     * }
     *
     * 常见查询：
     * - $near       : 附近搜索，返回距离排序
     * - $geoWithin  : 区域内搜索
     * - $geoIntersects : 交叉查询
     */
    private void createGeoIndex() {
        System.out.println("\n--- 地理空间索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "places");

        try {
            // 2dsphere 索引
            // 用于地球表面的位置查询
            coll.createIndex(Indexes.geo2dsphere("location"));
            System.out.println("  [创建] location 2dsphere 索引");

            // 2d 索引
            // 用于平面坐标查询（如游戏地图）
            MongoCollection<Document> simpleGeo = database.getCollection(COLLECTION_PREFIX + "simple_geo");
            simpleGeo.createIndex(Indexes.geo2d("coordinates"));
            System.out.println("  [创建] coordinates 2d 索引");

            System.out.println("  地理空间索引创建成功！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 部分索引 ====================

    /**
     * 创建部分索引
     *
     * 部分索引只对满足条件的文档创建索引，可以：
     * - 节省存储空间
     * - 提高查询效率
     * - 减少索引维护开销
     *
     * 适用场景：
     * 1. 只查询活跃数据：WHERE status = 'active'
     * 2. 只查询成年用户：WHERE age >= 18
     * 3. 大表中只有部分数据经常被查询
     *
     * partialFilterExpression：
     * 使用 Filters 构建复杂的条件表达式
     * 支持：eq、gt、gte、lt、lte、and、or 等
     */
    private void createPartialIndex() {
        System.out.println("\n--- 部分索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "users");

        try {
            // 部分索引：只为活跃用户创建索引
            // 效果：查询 inactive 用户不会使用此索引
            IndexOptions partialOptions = new IndexOptions()
                .partialFilterExpression(Filters.eq("status", "active"))
                .name("idx_active_users");
            coll.createIndex(Indexes.ascending("username"), partialOptions);
            System.out.println("  [创建] 仅限 active 用户的部分索引");

            // 更复杂的部分索引
            // 只索引活跃的成年用户
            IndexOptions partialOptions2 = new IndexOptions()
                .partialFilterExpression(
                    Filters.and(
                        Filters.eq("status", "active"),
                        Filters.gte("age", 18)
                    )
                )
                .name("idx_active_adult");
            coll.createIndex(Indexes.ascending("email"), partialOptions2);
            System.out.println("  [创建] 活跃且成年用户的部分索引");

            System.out.println("  部分索引创建成功！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== TTL 索引 ====================

    /**
     * 创建 TTL 索引（Time-To-Live 过期索引）
     *
     * TTL 索引会自动删除过期的文档，适用于：
     * - 会话数据：session 过期自动清理
     * - 日志数据：只保留最近N天的日志
     * - 缓存数据：临时缓存自动过期
     * - 验证码：短信验证码过期
     *
     * 工作原理：
     * MongoDB 后台有一个 TTL 监控线程，每60秒检查一次
     * 发现超过过期时间的文档就会删除
     *
     * 注意事项：
     * 1. 必须是 Date 类型字段
     * 2. 精确度不是秒级，有延迟
     * 3. 不能用于分片集合
     */
    private void createTtlIndex() {
        System.out.println("\n--- TTL 过期索引 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "sessions");

        try {
            // 创建 TTL 索引
            // expireAfter: 30秒后过期
            // lastAccess 字段必须是 Date 类型
            IndexOptions ttlOptions = new IndexOptions()
                .expireAfter(30L, java.util.concurrent.TimeUnit.SECONDS)
                .name("idx_session_expire");
            coll.createIndex(Indexes.ascending("lastAccess"), ttlOptions);
            System.out.println("  [创建] lastAccess TTL 索引（30秒过期）");

            // 插入测试会话数据
            // 30秒后这条数据会被自动删除
            coll.insertOne(new Document()
                .append("sessionId", "session_" + System.currentTimeMillis())
                .append("userId", 1001)
                .append("lastAccess", new Date()));
            System.out.println("  [插入] 测试会话数据（30秒后自动删除）");

            System.out.println("  TTL 索引创建成功！");

        } catch (Exception e) {
            System.out.println("  索引可能已存在: " + e.getMessage());
        }
    }

    // ==================== 索引管理和分析 ====================

    /**
     * 查看和分析索引
     *
     * 通过 listIndexes() 查看集合的所有索引
     * 通过 explain() 分析查询执行计划
     *
     * explain() 返回信息：
     * - stage        : 执行阶段（IDHACK/FETCH/COLLSCAN 等）
     * - indexName    : 使用的索引名
     * - totalDocsExamined : 检查的文档数
     * - nReturned    : 返回的文档数
     *
     * 执行阶段说明：
     * - IDHACK      : 使用 _id 索引，最快
     * - FETCH       : 使用索引后回表获取数据
     * - COLLSCAN    : 全表扫描，需要优化！
     * - IXSCAN      : 索引扫描
     * - PROJECTION  : 字段过滤
     */
    private void listAndAnalyzeIndexes() {
        System.out.println("\n--- 索引管理与分析 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "users");

        // 查看所有索引
        System.out.println("  索引列表:");
        coll.listIndexes().forEach(index -> {
            System.out.println("    - " + index.getString("name") + ": " + index.get("key"));
        });

        // 查询计划分析
        System.out.println("\n  查询计划分析:");

        // 使用索引的查询
        // 按 username 精确查询，会使用 username_1 索引
        Document planWithIndex = coll.find(Filters.eq("username", "user_50"))
            .limit(1)
            .explain();
        printExplainStats(planWithIndex, "按 username 查询（使用索引）");

        // 正则表达式前缀查询
        // /^user_5/ 可以使用索引
        Document planWithoutIndex = coll.find(Filters.regex("username", "^user_5"))
            .limit(1)
            .explain();
        printExplainStats(planWithoutIndex, "正则前缀查询");
    }

    /**
     * 打印 EXPLAIN 分析结果
     *
     * @param plan     EXPLAIN 返回的执行计划文档
     * @param queryType 查询类型描述
     */
    private void printExplainStats(Document plan, String queryType) {
        System.out.println("    " + queryType + ":");

        // 获取执行阶段
        String stage = plan.getString("stage");
        System.out.println("      - 执行阶段: " + stage);

        // 获取执行统计
        Document executionStats = plan.get("executionStats", Document.class);
        if (executionStats != null) {
            int totalDocsExamined = executionStats.getInteger("totalDocsExamined", 0);
            int nReturned = executionStats.getInteger("nReturned", 0);
            System.out.println("      - 检查文档数: " + totalDocsExamined);
            System.out.println("      - 返回文档数: " + nReturned);

            // 分析查询效率
            if ("IDHACK".equals(stage) || "FETCH".equals(stage)) {
                System.out.println("      - 状态: 使用索引，高效");
            } else if ("COLLSCAN".equals(stage)) {
                System.out.println("      - 状态: 全表扫描，需优化！");
            } else if ("IXSCAN".equals(stage)) {
                System.out.println("      - 状态: 纯索引扫描，高效");
            }
        }
    }

    // ==================== 地理空间查询演示 ====================

    /**
     * 地理空间查询演示
     *
     * 使用 $near 操作符进行附近搜索
     *
     * $near 语法：
     * {
     *   $near: {
     *     $geometry: { type: "Point", coordinates: [经度, 纬度] },
     *     $maxDistance: 最大距离（米）,
     *     $minDistance: 最小距离（米）
     *   }
     * }
     *
     * 返回结果按距离从小到大排序
     */
    private void geoQueryDemo() {
        System.out.println("\n--- 地理空间查询 ---");
        MongoCollection<Document> coll = database.getCollection(COLLECTION_PREFIX + "places");

        // 附近查询
        // 查找坐标 (-73.97, 40.77) 附近 1000 米内的地点
        System.out.println("  附近查询（坐标: -73.97, 40.77，1000米内）:");

        // 创建 GeoJSON Point
        Point point = new Point(new Position(-73.97, 40.77));

        // 使用 $near 查询
        // Filters.near() 是 MongoDB Java Driver 提供的便捷方法
        coll.find(Filters.near("location", point, 1000.0, null))
            .forEach(doc -> {
                System.out.println("    - " + doc.getString("name"));
            });

        System.out.println("  地理空间查询成功！");
    }

    // ==================== 清理方法 ====================

    /**
     * 清理测试数据
     *
     * 测试完成后删除演示创建的集合，保持环境整洁
     */
    private void cleanup() {
        System.out.println("\n--- 清理测试数据 ---");

        String[] collections = {
            COLLECTION_PREFIX + "users",
            COLLECTION_PREFIX + "places",
            COLLECTION_PREFIX + "simple_geo",
            COLLECTION_PREFIX + "sessions"
        };

        for (String coll : collections) {
            try {
                database.getCollection(coll).drop();
                System.out.println("  删除集合: " + coll);
            } catch (Exception e) {
                // 集合可能不存在，忽略错误
            }
        }
    }

    // ==================== 主方法 ====================

    /**
     * 程序入口
     *
     * 运行命令：
     * mvn exec:java -Dexec.mainClass="com.shuai.database.mongodb.MongoDBIndexDemo"
     */
    public static void main(String[] args) {
        new MongoDBIndexDemo().runAllDemos();
    }
}
