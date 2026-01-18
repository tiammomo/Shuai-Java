# MongoDB CRUD 操作

## 插入操作

### insertOne

```javascript
// 插入单个文档
db.users.insertOne({
    name: "John",
    age: 30,
    email: "john@example.com",
    status: "active",
    createdAt: new Date()
})
```

### insertMany

```javascript
// 插入多个文档
db.users.insertMany([
    { name: "Alice", age: 25, city: "New York" },
    { name: "Bob", age: 28, city: "Los Angeles" },
    { name: "Charlie", age: 35, city: "Chicago" }
])

// 有序插入（默认）- 一个失败后停止
db.users.insertMany([
    { name: "David" },
    { name: "Eve" }
], { ordered: true })

// 无序插入 - 继续插入其他文档
db.users.insertMany([
    { name: "Frank" },
    { name: "Grace" }
], { ordered: false })
```

### 插入行为

```javascript
// 自动生成 _id
db.users.insertOne({ name: "Test" })
// 返回: { "acknowledged" : true, "insertedId" : ObjectId("xxx") }

// _id 重复会报错
db.users.insertOne({ _id: 1, name: "Test" })
db.users.insertOne({ _id: 1, name: "Test2" })  // Error: duplicate key
```

## 查询操作

### find 方法

```javascript
// 查询所有文档
db.users.find()

// 查询条件
db.users.find({ status: "active" })

// 多条件 AND
db.users.find({
    status: "active",
    age: { $gte: 25 }
})

// 多条件 OR
db.users.find({
    $or: [
        { status: "active" },
        { age: { $gte: 30 } }
    ]
})
```

### 比较操作符

```javascript
// 等于（默认）
db.users.find({ age: 25 })

// 不等于
db.users.find({ age: { $ne: 25 } })

// 大于
db.users.find({ age: { $gt: 25 } })

// 大于等于
db.users.find({ age: { $gte: 25 } })

// 小于
db.users.find({ age: { $lt: 30 } })

// 小于等于
db.users.find({ age: { $lte: 30 } })

// 范围内
db.users.find({
    age: { $gte: 25, $lte: 35 }
})

// 在集合中
db.users.find({
    city: { $in: ["New York", "Los Angeles"] }
})

// 不在集合中
db.users.find({
    city: { $nin: ["Chicago", "Miami"] }
})
```

### 逻辑操作符

```javascript
// AND（默认）
db.users.find({
    status: "active",
    age: { $gte: 25 }
})

// OR
db.users.find({
    $or: [
        { status: "active" },
        { age: { $gte: 30 } }
    ]
})

// NOR（既不...也不...）
db.users.find({
    $nor: [
        { status: "inactive" },
        { age: { $lt: 18 } }
    ]
})

// NOT
db.users.find({
    age: { $not: { $gte: 30 } }
})
```

### 元素操作符

```javascript
// 字段存在
db.users.find({ email: { $exists: true } })
db.users.find({ email: { $exists: false } })

// 字段类型
db.users.find({ age: { $type: "number" } })
db.users.find({ age: { $type: ["number", "string"] } })

// 数组长度
db.users.find({
    tags: { $size: 3 }
})
```

### 数组操作符

```javascript
// 精确匹配数组
db.products.find({ tags: ["electronics", "sale"] })

// 包含元素
db.products.find({ tags: "electronics" })

// 包含所有元素
db.products.find({
    tags: { $all: ["electronics", "sale"] }
})

// 数组元素匹配
db.users.find({
    scores: { $elemMatch: { $gte: 90 } }
})

// 数组元素查询
db.users.find({
    "scores.0": { $gte: 90 }  // 第一个元素 >= 90
})
```

### 正则表达式

```javascript
// 匹配
db.users.find({ name: { $regex: "^John" } })
db.users.find({ name: { $regex: "^john", $options: "i" } })  // 忽略大小写

// 直接使用
db.users.find({ name: /^John/i })
```

### 投影（选择字段）

```javascript
// 只返回指定字段
db.users.find(
    { status: "active" },
    { name: 1, email: 1 }  // 1: 显示
)

// 排除字段
db.users.find(
    { status: "active" },
    { password: 0 }  // 0: 排除
)

// 排除 _id
db.users.find(
    { status: "active" },
    { name: 1, _id: 0 }
)

// 嵌套字段
db.users.find(
    { "address.city": "New York" },
    { name: 1, "address.city": 1 }
)
```

### 排序、分页

```javascript
// 排序
db.users.find().sort({ age: 1 })      // 升序
db.users.find().sort({ age: -1 })     // 降序
db.users.find().sort({ name: 1, age: -1 })

// 限制数量
db.users.find().limit(10)

// 跳过数量
db.users.find().skip(20)

// 分页查询
db.users.find()
    .sort({ _id: 1 })
    .skip((page - 1) * size)
    .limit(size)

// 获取总数
db.users.countDocuments({ status: "active" })
db.users.estimatedDocumentCount()  // 快速估算
```

### 聚合管道查询

```javascript
// 使用聚合
db.users.aggregate([
    { $match: { status: "active" } },
    { $sort: { age: -1 } },
    { $skip: 10 },
    { $limit: 10 },
    { $project: { name: 1, age: 1 } }
])
```

## 更新操作

### updateOne

```javascript
// 更新单个文档
db.users.updateOne(
    { name: "John" },
    {
        $set: {
            age: 31,
            status: "vip"
        }
    }
)

// 更新嵌套字段
db.users.updateOne(
    { name: "John" },
    { $set: { "address.city": "Boston" } }
)

// 数组元素更新
db.users.updateOne(
    { name: "John", "scores.0": { $lt: 60 } },
    { $set: { "scores.0": 60 } }
)
```

### updateMany

```javascript
// 更新多个文档
db.users.updateMany(
    { status: "inactive" },
    {
        $set: {
            status: "pending",
            lastLogin: new Date()
        }
    }
)

// 使用 $inc 递增
db.users.updateMany(
    { status: "active" },
    { $inc: { loginCount: 1 } }
)

// 使用 $mul 乘以
db.products.updateMany(
    { category: "sale" },
    { $mul: { price: 0.9 } }  // 打9折
)
```

### 更新操作符

```javascript
// $set - 设置值
db.users.updateOne(
    { _id: 1 },
    { $set: { name: "New Name", age: 30 } }
)

// $unset - 删除字段
db.users.updateOne(
    { _id: 1 },
    { $unset: { tempField: "" } }
)

// $inc - 递增/递减
db.users.updateOne(
    { _id: 1 },
    { $inc: { age: 1, score: -5 } }
)

// $mul - 乘法
db.products.updateOne(
    { _id: 1 },
    { $mul: { price: 1.1 } }
)

// $min - 取最小值
db.users.updateOne(
    { _id: 1 },
    { $min: { price: 100 } }
)

// $max - 取最大值
db.users.updateOne(
    { _id: 1 },
    { $max: { price: 100 } }
)

// $rename - 重命名字段
db.users.updateOne(
    { _id: 1 },
    { $rename: { "old_name": "new_name" } }
)

// $addToSet - 添加到数组（不重复）
db.users.updateOne(
    { _id: 1 },
    { $addToSet: { tags: "vip" } }
)

// $push - 添加到数组
db.users.updateOne(
    { _id: 1 },
    { $push: { scores: 95 } }
)

// $pop - 从数组移除
db.users.updateOne(
    { _id: 1 },
    { $pop: { scores: 1 } }   // 移除最后一个
)

// $pull - 移除匹配元素
db.users.updateOne(
    { _id: 1 },
    { $pull: { tags: "inactive" } }
)

// $pullAll - 移除多个元素
db.users.updateOne(
    { _id: 1 },
    { $pullAll: { tags: ["temp1", "temp2"] } }
)
```

### upsert

```javascript
// 更新或插入
db.users.updateOne(
    { email: "new@example.com" },
    {
        $set: {
            name: "New User",
            status: "active",
            createdAt: new Date()
        }
    },
    { upsert: true }
)
```

### findOneAndUpdate

```javascript
// 更新并返回文档
db.users.findOneAndUpdate(
    { name: "John" },
    { $set: { status: "vip" } },
    {
        returnDocument: "after",  // 返回更新后的文档
        sort: { _id: 1 }          // 多文档时排序
    }
)
```

## 删除操作

### deleteOne

```javascript
// 删除单个文档
db.users.deleteOne({ _id: 1 })
db.users.deleteOne({ name: "John" })
```

### deleteMany

```javascript
// 删除多个文档
db.users.deleteMany({ status: "deleted" })

// 删除所有文档（保留索引）
db.users.deleteMany({})
```

### findOneAndDelete

```javascript
// 删除并返回
db.users.findOneAndDelete(
    { status: "inactive" },
    { sort: { createdAt: 1 } }
)
```

### 删除集合

```javascript
// 删除集合和所有文档
db.users.drop()

// 删除数据库
db.dropDatabase()
```

## Java 实现

```java
import com.mongodb.client.MongoCollection;
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

public class MongoCrudDemo {
    private MongoCollection<Document> users;

    // 插入
    public void insert() {
        Document doc = new Document()
            .append("name", "John")
            .append("age", 30)
            .append("email", "john@example.com")
            .append("status", "active")
            .append("createdAt", new Date());

        InsertOneResult result = users.insertOne(doc);
        System.out.println("插入成功: " + result.getInsertedId());
    }

    public void insertMany() {
        List<Document> docs = Arrays.asList(
            new Document("name", "Alice").append("age", 25),
            new Document("name", "Bob").append("age", 28),
            new Document("name", "Charlie").append("age", 35)
        );
        users.insertMany(docs);
    }

    // 查询
    public void find() {
        // 查询所有
        System.out.println("=== 所有用户 ===");
        users.find().forEach(doc -> System.out.println(doc.toJson()));

        // 条件查询
        System.out.println("=== 活跃用户 ===");
        users.find(Filters.eq("status", "active"))
            .forEach(doc -> System.out.println(doc.toJson()));

        // 复合条件
        Bson filter = Filters.and(
            Filters.eq("status", "active"),
            Filters.gte("age", 25)
        );
        System.out.println("=== 25岁以上活跃用户 ===");
        users.find(filter).forEach(doc -> System.out.println(doc.toJson()));

        // 投影
        System.out.println("=== 只返回姓名和邮箱 ===");
        users.find(Filters.eq("status", "active"))
            .projection(new Document("name", 1).append("email", 1).append("_id", 0))
            .forEach(doc -> System.out.println(doc.toJson()));

        // 排序分页
        System.out.println("=== 按年龄降序，前10个 ===");
        users.find()
            .sort(new Document("age", -1))
            .limit(10)
            .forEach(doc -> System.out.println(doc.toJson()));
    }

    // 更新
    public void update() {
        // 更新单个
        Bson filter = Filters.eq("name", "John");
        Bson update = Updates.combine(
            Updates.set("age", 31),
            Updates.set("status", "vip")
        );
        UpdateResult result = users.updateOne(filter, update);
        System.out.println("更新数量: " + result.getModifiedCount());

        // 更新多个
        Bson filterMany = Filters.eq("status", "inactive");
        Bson updateMany = Updates.set("status", "pending");
        users.updateMany(filterMany, updateMany);

        // 递增
        users.updateOne(
            Filters.eq("name", "John"),
            Updates.inc("loginCount", 1)
        );

        // 数组操作
        users.updateOne(
            Filters.eq("name", "John"),
            Updates.push("tags", "vip")
        );
    }

    // 删除
    public void delete() {
        // 删除单个
        users.deleteOne(Filters.eq("name", "John"));

        // 删除多个
        DeleteResult result = users.deleteMany(Filters.eq("status", "deleted"));
        System.out.println("删除数量: " + result.getDeletedCount());
    }

    // upsert
    public void upsert() {
        Bson filter = Filters.eq("email", "new@example.com");
        Bson update = Updates.combine(
            Updates.setOnInsert("name", "New User"),
            Updates.set("status", "active"),
            Updates.set("createdAt", new Date())
        );
        users.updateOne(filter, update);
    }
}
```
