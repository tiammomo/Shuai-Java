# MongoDB 基础

## 简介

MongoDB 是一个基于分布式文件存储的开源 NoSQL 数据库，使用 C++ 编写。它支持灵活的数据模型（BSON 文档），适合处理大量非结构化或半结构化数据。

### 核心特性

| 特性 | 说明 |
|------|------|
| 文档模型 | 使用 BSON 格式，灵活的模式设计 |
| 高可用 | 副本集（Replica Set）支持自动故障转移 |
| 可扩展 | 分片（Sharding）支持水平扩展 |
| 索引支持 | 支持多种索引类型 |
| 聚合框架 | 强大的数据处理管道 |
| 事务支持 | 多文档事务（4.0+） |

### 与关系型数据库对比

| 关系型 | MongoDB |
|--------|---------|
| Database | Database |
| Table | Collection |
| Row | Document |
| Column | Field |
| Index | Index |
| JOIN | $lookup / 嵌入文档 |

## Docker 部署

### 单节点部署

```bash
# 拉取镜像
docker pull mongo:7

# 启动容器
docker run -d \
    --name mongodb7 \
    -e MONGO_INITDB_ROOT_USERNAME=ubuntu \
    -e MONGO_INITDB_ROOT_PASSWORD=ubuntu \
    -e MONGO_INITDB_DATABASE=admin \
    -p 27017:27017 \
    -v mongodb_data:/data/db \
    mongo:7

# 连接测试
mongosh "mongodb://ubuntu:ubuntu@localhost:27017/admin"
```

### 副本集部署

```bash
# 启动副本集
docker run -d \
    --name mongodb-replica \
    -e MONGO_INITDB_ROOT_USERNAME=ubuntu \
    -e MONGO_INITDB_ROOT_PASSWORD=ubuntu \
    -p 27017:27017 \
    -v mongodb_data:/data/db \
    mongo:7 \
    --replSet rs0

# 初始化副本集
mongosh "mongodb://ubuntu:ubuntu@localhost:27017/admin"
> rs.initiate()
```

## 基本操作

### 数据库操作

```javascript
// 查看所有数据库
show dbs
// 或
db.adminCommand('listDatabases')

// 切换/创建数据库
use mydb

// 查看当前数据库
db

// 删除当前数据库
db.dropDatabase()
```

### 集合操作

```javascript
// 查看所有集合
show collections
// 或
db.getCollectionNames()

// 创建集合
db.createCollection("users")
db.createCollection("orders", { capped: true, size: 100000 })

// 删除集合
db.users.drop()

// 查看集合信息
db.users.stats()

// 集合重命名
db.users.renameCollection("new_users")
```

### 文档操作

```javascript
// 插入单个文档
db.users.insertOne({
    name: "John",
    age: 30,
    email: "john@example.com",
    createdAt: new Date()
})

// 插入多个文档
db.users.insertMany([
    { name: "Alice", age: 25 },
    { name: "Bob", age: 28 }
])

// 插入单个文档（推荐写法）
db.users.insertOne({ name: "John", age: 30 })

// 查看文档
db.users.find()
db.users.findOne({ name: "John" })

// 更新文档
db.users.updateOne(
    { name: "John" },
    { $set: { age: 31 } }
)

db.users.updateMany(
    { status: "inactive" },
    { $set: { status: "pending" } }
)

// 删除文档
db.users.deleteOne({ name: "John" })
db.users.deleteMany({ status: "deleted" })
```

## 数据类型

```javascript
// String
{ name: "John" }

// Number
{ age: 30, price: 29.99 }

// Boolean
{ active: true, deleted: false }

// Array
{ tags: ["admin", "user"], scores: [85, 90, 78] }

// Object（嵌套文档）
{
    address: {
        street: "123 Main St",
        city: "New York",
        zip: "10001"
    }
}

// Date
{ createdAt: new Date() }
{ timestamp: Timestamp(1234567890, 0) }

// ObjectId
{ _id: ObjectId("507f1f77bcf86cd799439011") }

// Null
{ deletedAt: null }

// Regular Expression
{ email: /@example\.com$/ }

// Binary Data
{ data: BinData(0, "dGVzdA==") }
```

## Java 连接

### Maven 依赖

```xml
<dependency>
    <groupId>org.mongodb</groupId>
    <artifactId>mongodb-driver-sync</artifactId>
    <version>4.11.1</version>
</dependency>
```

### 基本连接

```java
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class MongoConnectDemo {
    private static final String URI = "mongodb://ubuntu:ubuntu@localhost:27017";

    public static void main(String[] args) {
        try (MongoClient client = MongoClients.create(URI)) {
            // 获取数据库
            MongoDatabase db = client.getDatabase("testdb");

            // 获取集合
            MongoCollection<Document> users = db.getCollection("users");

            System.out.println("MongoDB 连接成功！");
            System.out.println("数据库: " + db.getName());
            System.out.println("集合数量: " + db.listCollectionNames().into(new ArrayList<>()).size());
        }
    }
}
```

### 连接池配置

```java
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import java.util.concurrent.TimeUnit;

public class MongoPoolDemo {
    public static void main(String[] args) {
        ConnectionString connectionString = new ConnectionString(
            "mongodb://ubuntu:ubuntu@localhost:27017/?maxPoolSize=50&minPoolSize=10&maxIdleTimeMS=60000"
        );

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToConnectionPoolSettings(builder ->
                builder.maxSize(50)
                    .minSize(10)
                    .maxWaitTime(30, TimeUnit.SECONDS)
                    .maxConnectionIdleTime(60, TimeUnit.SECONDS)
            )
            .applyToSocketSettings(builder ->
                builder.connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
            )
            .build();

        try (MongoClient client = MongoClients.create(settings)) {
            System.out.println("连接池配置成功");
        }
    }
}
```

### 常用操作

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MongoBasicOps {
    private MongoCollection<Document> collection;

    public void insert() {
        // 插入单个文档
        Document doc = new Document()
            .append("name", "John")
            .append("age", 30)
            .append("email", "john@example.com")
            .append("createdAt", new Date());

        InsertOneResult result = collection.insertOne(doc);
        System.out.println("插入ID: " + result.getInsertedId());
    }

    public void insertMany() {
        List<Document> docs = Arrays.asList(
            new Document("name", "Alice").append("age", 25),
            new Document("name", "Bob").append("age", 28),
            new Document("name", "Charlie").append("age", 35)
        );
        collection.insertMany(docs);
    }

    public void find() {
        // 查询所有
        collection.find().forEach(doc -> System.out.println(doc.toJson()));

        // 条件查询
        collection.find(Filters.eq("name", "John"))
            .forEach(doc -> System.out.println(doc.toJson()));

        // 多条件查询
        Bson filter = Filters.and(
            Filters.eq("status", "active"),
            Filters.gt("age", 25)
        );
        collection.find(filter).forEach(doc -> System.out.println(doc.toJson()));

        // 查询单个
        Document first = collection.find(Filters.eq("name", "John")).first();
    }

    public void update() {
        // 更新单个
        Bson filter = Filters.eq("name", "John");
        Bson update = Updates.set("age", 31);
        UpdateResult result = collection.updateOne(filter, update);
        System.out.println("更新数量: " + result.getModifiedCount());

        // 更新多个
        Bson filterMany = Filters.eq("status", "inactive");
        Bson updateMany = Updates.set("status", "pending");
        collection.updateMany(filterMany, updateMany);

        // 更新或插入
        Bson upsertFilter = Filters.eq("email", "new@example.com");
        Bson upsertUpdate = Updates.combine(
            Updates.setOnInsert("name", "New User"),
            Updates.set("status", "active"),
            Updates.set("createdAt", new Date())
        );
        collection.updateOne(upsertFilter, upsertUpdate);
    }

    public void delete() {
        // 删除单个
        collection.deleteOne(Filters.eq("name", "John"));

        // 删除多个
        DeleteResult result = collection.deleteMany(Filters.eq("status", "deleted"));
        System.out.println("删除数量: " + result.getDeletedCount());
    }

    public void aggregate() {
        collection.aggregate(Arrays.asList(
            new Document("$match", new Document("status", "active")),
            new Document("$group", new Document("_id", "$category")
                .append("count", new Document("$sum", 1))
                .append("avgPrice", new Document("$avg", "$price")))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }
}
```
