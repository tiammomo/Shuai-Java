# MongoDB 索引

## 索引基础

### 索引类型

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| 单字段索引 | 单个字段的索引 | 简单查询条件 |
| 复合索引 | 多个字段的索引 | 多条件查询 |
| 多键索引 | 数组字段的索引 | 数组查询 |
| 文本索引 | 全文搜索索引 | 文本搜索 |
| 地理空间索引 | 地理位置索引 | 位置查询 |
| 哈希索引 | 哈希分布索引 | 分片键 |
| 唯一索引 | 唯一约束索引 | 防止重复 |
| 稀疏索引 | 稀疏字段索引 | 可选字段 |

### 查看索引

```javascript
// 查看集合的所有索引
db.users.getIndexes()

// 查看索引大小
db.users.totalIndexSize()

// 查看索引详情
db.users.getIndexDetails()
```

## 索引创建

### 单字段索引

```javascript
// 升序索引（默认）
db.users.createIndex({ name: 1 })

// 降序索引
db.users.createIndex({ age: -1 })

// 唯一索引
db.users.createIndex({ email: 1 }, { unique: true })

// 稀疏索引（只索引非 null 值）
db.users.createIndex({ phone: 1 }, { sparse: true })

// 指定名称
db.users.createIndex({ name: 1 }, { name: "idx_user_name" })

// 过期索引（TTL）
db.sessions.createIndex(
    { createdAt: 1 },
    { expireAfterSeconds: 3600 }  // 1小时后过期
)
```

### 复合索引

```javascript
// 基本复合索引
db.users.createIndex({ status: 1, createdAt: -1 })

// 复合索引字段顺序原则
// 1. 等值查询字段在前
// 2. 排序字段在前
// 3. 范围查询字段在后

// 好的索引设计
// 查询：WHERE status = ? ORDER BY createdAt DESC
db.users.createIndex({ status: 1, createdAt: -1 })

// 查询：WHERE status = ? AND age >= ?
db.users.createIndex({ status: 1, age: 1 })

// 多键索引
db.products.createIndex({ tags: 1 })

// 嵌套文档索引
db.users.createIndex({ "address.city": 1 })

// 数组索引
db.users.createIndex({ "scores.score": 1 })
```

### 文本索引

```javascript
// 基本文本索引
db.articles.createIndex({ title: "text", content: "text" })

// 指定权重
db.articles.createIndex(
    { title: "text", content: "text" },
    { weights: { title: 10, content: 1 } }
)

// 任意字段文本索引
db.articles.createIndex({ "$**": "text" })

// 文本搜索
db.articles.find({
    $text: { $search: "mongodb tutorial" }
})

// 文本搜索（包含所有词）
db.articles.find({
    $text: { $search: "mongodb tutorial", $language: "en" }
})

// 文本搜索（排除词）
db.articles.find({
    $text: { $search: "mongodb -nosql" }
})

// 文本评分
db.articles.find(
    { $text: { $search: "mongodb" } },
    { score: { $meta: "textScore" } }
).sort({ score: { $meta: "textScore" } })
```

### 地理空间索引

```javascript
// 2dsphere 索引（球面）
db.places.createIndex({ location: "2dsphere" })

// 2d 索引（平面）
db.places.createIndex({ location: "2d" })

// 附近查询
db.places.find({
    location: {
        $near: {
            $geometry: {
                type: "Point",
                coordinates: [-73.97, 40.77]
            },
            $maxDistance: 1000  // 1000米
        }
    }
})

// 圆形区域查询
db.places.find({
    location: {
        $geoWithin: {
            $centerSphere: [
                [-73.97, 40.77],
                0.001  // 弧度
            ]
        }
    }
})

// 多边形查询
db.places.find({
    location: {
        $geoWithin: {
            $geometry: {
                type: "Polygon",
                coordinates: [[
                    [-73.97, 40.77],
                    [-73.96, 40.76],
                    [-73.95, 40.77],
                    [-73.97, 40.77]
                ]]
            }
        }
    }
})
```

## 索引管理

### 删除索引

```javascript
// 删除单个索引
db.users.dropIndex("index_name")

// 删除索引（按字段）
db.users.dropIndex({ name: 1 })

// 删除所有非默认索引
db.users.dropIndexes()

// 删除默认 _id 索引（不可恢复）
// db.users.dropIndex("_id")
```

### 重建索引

```javascript
// 重建集合的所有索引
db.users.reIndex()
```

### 索引属性

```javascript
// 唯一索引
db.users.createIndex(
    { email: 1 },
    { unique: true, dropDups: true }  // 删除重复文档
)

// 部分索引
db.users.createIndex(
    { email: 1 },
    {
        partialFilterExpression: {
            email: { $exists: true }
        }
    }
)

// 稀疏索引
db.users.createIndex(
    { phone: 1 },
    { sparse: true }
)

// TTL 索引
db.sessions.createIndex(
    { lastAccess: 1 },
    { expireAfterSeconds: 3600 }
)
```

## 索引策略

### 选择索引字段

```javascript
// 高区分度字段（适合索引）
// 用户名、邮箱、手机号
db.users.createIndex({ email: 1 })

// 低区分度字段（不适合索引）
// 性别、状态（只有几个值）
db.users.createIndex({ status: 1 })  // 效果有限
```

### 复合索引设计

```javascript
// 查询模式
// Query1: db.users.find({ status: "active" })
// Query2: db.users.find({ status: "active" }).sort({ createdAt: -1 })
// Query3: db.users.find({ status: "active", age: { $gte: 18 } })

// 最佳复合索引
db.users.createIndex({ status: 1, age: 1, createdAt: -1 })

// 验证索引使用
db.users.find({ status: "active" }).explain("executionStats")
```

### 覆盖查询

```javascript
// 索引覆盖：查询只返回索引字段
db.users.createIndex({ name: 1, age: 1, status: 1 })

// 覆盖查询
db.users.find(
    { status: "active", age: { $gte: 18 } },
    { _id: 0, name: 1, age: 1 }
)

// explain 验证
db.users.find(
    { status: "active" },
    { name: 1, age: 1 }
).explain("executionStats")
// "inputStage" -> "inputStageType": "IDHACK" 或 "FETCH"
```

## 索引性能分析

### EXPLAIN

```javascript
// 查看执行计划
db.users.find({ name: "John" }).explain()

// executionStats - 查看详细统计
db.users.find({ status: "active" }).explain("executionStats")

// allPlansExecution - 查看所有计划
db.users.find({ name: "John" }).explain("allPlansExecution")
```

### 关键指标

```javascript
// 好的情况 - 使用索引
{
    "stage": "IDHACK",          // 直接使用 _id 索引
    "indexName": "_id_",
    "nReturned": 1,
    "totalDocsExamined": 1,     // 等于 nReturned
    "totalKeysExamined": 1      // 等于 nReturned
}

// 好的情况 - 索引覆盖
{
    "stage": "PROJECTION_COVERED",
    "indexName": "idx_status_name"
}

// 需要优化 - 全表扫描
{
    "stage": "COLLSCAN",        // 全表扫描
    "totalDocsExamined": 100000,
    "nReturned": 10
}

// 需要优化 - 回表查询
{
    "stage": "FETCH",           // 回表查询
    "indexName": "idx_status",
    "totalDocsExamined": 1000,
    "totalKeysExamined": 1000,
    "nReturned": 100
}
```

### 索引使用情况统计

```javascript
// 查看索引使用统计
db.users.aggregate([
    { $indexStats: {} }
])

// 查看慢查询日志
db.system.profile.find({
    "command.createIndexes": { $exists: true }
}).pretty()
```

## Java 实现

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.Date;

public class MongoIndexDemo {
    private MongoCollection<Document> users;

    // 创建单字段索引
    public void createSingleIndex() {
        // 升序索引
        users.createIndex(Indexes.ascending("name"));

        // 降序索引
        users.createIndex(Indexes.descending("age"));

        // 唯一索引
        IndexOptions uniqueOptions = new IndexOptions().unique(true);
        users.createIndex(Indexes.ascending("email"), uniqueOptions);

        // 稀疏索引
        IndexOptions sparseOptions = new IndexOptions().sparse(true);
        users.createIndex(Indexes.ascending("phone"), sparseOptions);

        // TTL 索引
        IndexOptions ttlOptions = new IndexOptions()
            .expireAfter(3600L, java.util.concurrent.TimeUnit.SECONDS);
        users.createIndex(Indexes.ascending("lastAccess"), ttlOptions);
    }

    // 创建复合索引
    public void createCompoundIndex() {
        // 基本复合索引
        users.createIndex(Indexes.compoundIndex(
            Indexes.ascending("status"),
            Indexes.descending("createdAt")
        ));

        // 多键索引
        users.createIndex(Indexes.ascending("tags"));
    }

    // 创建文本索引
    public void createTextIndex() {
        // 基本文本索引
        users.createIndex(Indexes.text("bio"));

        // 多字段文本索引
        users.createIndex(Indexes.compoundText(
            Indexes.text("title"),
            Indexes.text("content")
        ));

        // 指定权重
        users.createIndex(
            Indexes.compoundText(
                Indexes.text("title"),
                Indexes.text("content")
            ),
            new IndexOptions().weights(new Document("title", 10))
        );
    }

    // 创建地理空间索引
    public void createGeoIndex() {
        // 2dsphere 索引
        users.createIndex(Indexes.geo2dsphere("location"));

        // 2d 索引
        users.createIndex(Indexes.geo2d("coordinates"));
    }

    // 创建部分索引
    public void createPartialIndex() {
        IndexOptions partialOptions = new IndexOptions()
            .partialFilterExpression(
                Filters.exists("email", true)
            );
        users.createIndex(
            Indexes.ascending("email"),
            partialOptions
        );

        // 更复杂的部分索引
        users.createIndex(
            Indexes.ascending("status"),
            new IndexOptions().partialFilterExpression(
                Filters.and(
                    Filters.eq("status", "active"),
                    Filters.gte("createdAt", new Date())
                )
            )
        );
    }

    // 删除索引
    public void dropIndex() {
        // 按名称删除
        users.dropIndex("idx_user_email");

        // 按字段删除
        users.dropIndex(Indexes.ascending("name"));

        // 删除所有
        users.dropIndexes();
    }

    // 查看索引信息
    public void listIndexes() {
        users.listIndexes().forEach(index -> {
            System.out.println("索引名称: " + index.getString("name"));
            System.out.println("索引键: " + index.get("key"));
            System.out.println("索引选项: " + index.get("options"));
            System.out.println("---");
        });
    }

    // 分析查询计划
    public void explainQuery() {
        Document plan = users.find(Filters.eq("status", "active"))
            .sort(new Document("createdAt", -1))
            .limit(10)
            .explain();

        System.out.println("执行计划: " + plan.toJson());

        // 分析关键指标
        Document executionStats = plan.get("executionStats", Document.class);
        if (executionStats != null) {
            int totalKeysExamined = executionStats.getInteger("totalKeysExamined");
            int totalDocsExamined = executionStats.getInteger("totalDocsExamined");
            int nReturned = executionStats.getInteger("nReturned");

            System.out.println("返回文档数: " + nReturned);
            System.out.println("检查键数: " + totalKeysExamined);
            System.out.println("检查文档数: " + totalDocsExamined);

            if (totalKeysExamined == nReturned && totalDocsExamined == nReturned) {
                System.out.println("查询效率高：使用索引覆盖");
            } else if (totalKeysExamined == nReturned) {
                System.out.println("查询效率好：使用索引");
            } else {
                System.out.println("查询需要优化：存在回表或全表扫描");
            }
        }
    }

    // 地理空间查询
    public void geoQuery(MongoDatabase db) {
        // 附近查询
        Point point = new Point(new Position(-73.97, 40.77));
        db.getCollection("places").find(
            Filters.near("location", point, 1000.0, null)
        ).limit(10).forEach(doc -> System.out.println(doc.toJson()));
    }
}
```

## 索引最佳实践

| 场景 | 建议 |
|------|------|
| 查询字段 | 高区分度字段建立单字段索引 |
| 排序字段 | 复合索引中排序字段放在等值字段后 |
| 多条件查询 | 创建复合索引支持多个查询条件 |
| 大文本搜索 | 使用文本索引而非正则表达式 |
| 地理位置 | 使用 2dsphere 索引 |
| 唯一约束 | 使用唯一索引 |
| 可选字段 | 使用稀疏索引 |
| 自动过期 | 使用 TTL 索引 |
| 分片键 | 使用哈希索引 |
| 读多写少 | 适当增加索引 |
| 写多读少 | 减少索引数量 |
