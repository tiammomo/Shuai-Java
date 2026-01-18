# MongoDB 聚合框架

## 聚合管道基础

### 基本结构

```javascript
// 聚合管道语法
db.collection.aggregate([
    { $stage1: { ... } },
    { $stage2: { ... } },
    { $stage3: { ... } }
])
```

### 简单示例

```javascript
// 统计每个状态的用户数量
db.users.aggregate([
    { $group: {
        _id: "$status",
        count: { $sum: 1 }
    }}
])

// 输出:
// { "_id" : "active", "count" : 100 }
// { "_id" : "inactive", "count" : 50 }
```

## 聚合阶段

### $match - 筛选

```javascript
// 在分组前筛选
db.orders.aggregate([
    { $match: { status: "completed" } },
    { $group: { _id: "$user_id", total: { $sum: "$amount" } } }
])

// 在分组后筛选
db.orders.aggregate([
    { $group: { _id: "$user_id", total: { $sum: "$amount" } } },
    { $match: { total: { $gte: 1000 } } }
])

// 复杂条件
db.orders.aggregate([
    { $match: {
        status: "completed",
        createdAt: { $gte: new Date("2024-01-01") },
        amount: { $gte: 100 }
    }}
])
```

### $project - 投影

```javascript
// 选择字段
db.users.aggregate([
    { $project: {
        name: 1,
        email: 1,
        _id: 0
    }}
])

// 计算新字段
db.orders.aggregate([
    { $project: {
        orderId: "$_id",
        userId: 1,
        finalAmount: {
            $multiply: ["$amount", 0.9]  // 9折
        },
        tax: {
            $multiply: ["$amount", 0.1]
        }
    }}
])

// 重命名字段
db.users.aggregate([
    { $project: {
        userName: "$name",
        userAge: "$age",
        _id: 0
    }}
])

// 嵌套字段展开
db.users.aggregate([
    { $project: {
        name: 1,
        "address.city": 1,
        "address.country": 1
    }}
])
```

### $group - 分组

```javascript
// 按单个字段分组
db.orders.aggregate([
    { $group: {
        _id: "$status",
        count: { $sum: 1 }
    }}
])

// 按多个字段分组
db.orders.aggregate([
    { $group: {
        _id: {
            status: "$status",
            year: { $year: "$createdAt" }
        },
        count: { $sum: 1 },
        totalAmount: { $sum: "$amount" },
        avgAmount: { $avg: "$amount" },
        maxAmount: { $max: "$amount" },
        minAmount: { $min: "$amount" }
    }}
])

// 累计求和
db.orders.aggregate([
    { $sort: { createdAt: 1 } },
    { $group: {
        _id: null,
        orders: { $push: "$amount" },
        total: { $sum: "$amount" }
    }}
])

// 分组后数组
db.users.aggregate([
    { $group: {
        _id: "$status",
        users: { $push: "$name" }
    }}
])

// 收集不同值
db.users.aggregate([
    { $group: {
        _id: "$status",
        cities: { $addToSet: "$city" }
    }}
])

// 第一个/最后一个
db.orders.aggregate([
    { $sort: { createdAt: -1 } },
    { $group: {
        _id: "$user_id",
        lastOrder: { $first: "$$ROOT" },
        firstOrderAmount: { $first: "$amount" }
    }}
])
```

### $sort - 排序

```javascript
// 排序
db.users.aggregate([
    { $sort: { age: 1, name: 1 } }  // 1: 升序, -1: 降序
])

// 管道中排序
db.orders.aggregate([
    { $match: { status: "completed" } },
    { $sort: { amount: -1 } },
    { $limit: 10 }
])
```

### $limit - 限制

```javascript
// 限制数量
db.users.aggregate([
    { $limit: 10 }
])

// 分页
db.users.aggregate([
    { $sort: { _id: 1 } },
    { $skip: 20 },
    { $limit: 10 }
])
```

### $skip - 跳过

```javascript
// 跳过文档
db.users.aggregate([
    { $skip: 100 }
])
```

### $unwind - 数组展开

```javascript
// 展开数组
db.orders.aggregate([
    { $unwind: "$items" },
    { $project: {
        orderId: "$_id",
        productId: "$items.productId",
        quantity: "$items.quantity"
    }}
])

// 保留空数组（preserveNullAndEmptyArrays）
db.orders.aggregate([
    { $unwind: {
        path: "$items",
        preserveNullAndEmptyArrays: true
    }}
])
```

### $lookup - 连接

```javascript
// 基本连接
db.orders.aggregate([
    { $lookup: {
        from: "users",           // 连接的集合
        localField: "user_id",   // 本地字段
        foreignField: "_id",     // 外部字段
        as: "user_info"          // 输出字段名
    }}
])

// 多字段连接
db.orders.aggregate([
    { $lookup: {
        from: "users",
        let: { userId: "$user_id" },
        pipeline: [
            { $match: { $expr: { $eq: ["$_id", "$$userId"] } } }
        ],
        as: "user_info"
    }}
])

// 自连接
db.employees.aggregate([
    { $lookup: {
        from: "employees",
        localField: "manager_id",
        foreignField: "_id",
        as: "manager"
    }}
])

// 反向连接
db.users.aggregate([
    { $lookup: {
        from: "orders",
        localField: "_id",
        foreignField: "user_id",
        as: "orders"
    }},
    { $project: {
        name: 1,
        orderCount: { $size: "$orders" }
    }}
])
```

### $addFields - 添加字段

```javascript
// 添加计算字段
db.orders.aggregate([
    { $addFields: {
        totalWithTax: { $multiply: ["$amount", 1.1] },
        year: { $year: "$createdAt" }
    }}
])

// 替换_id
db.orders.aggregate([
    { $addFields: {
        orderId: "$_id"
    }},
    { $project: {
        _id: 0,
        orderId: 1
    }}
])
```

### $replaceRoot - 替换根

```javascript
// 替换为嵌套文档
db.users.aggregate([
    { $replaceRoot: {
        newRoot: "$address"
    }}
])

// 条件替换
db.orders.aggregate([
    { $replaceRoot: {
        newRoot: {
            $mergeObjects: [
                "$user",
                { orderId: "$_id", total: "$amount" }
            ]
        }
    }}
])
```

## 聚合表达式

### 算术表达式

```javascript
// 加减乘除
db.orders.aggregate([
    { $project: {
        subtotal: "$amount",
        tax: { $multiply: ["$amount", 0.1] },
        total: { $multiply: ["$amount", 1.1] }
    }}
])

// 取整
db.orders.aggregate([
    { $project: {
        roundedAmount: { $round: ["$amount", 2] }
    }}
])

// 向上/向下取整
db.orders.aggregate([
    { $project: {
        ceilAmount: { $ceil: "$amount" },
        floorAmount: { $floor: "$amount" }
    }}
])

// 取模
db.orders.aggregate([
    { $project: {
        remainder: { $mod: ["$amount", 10] }
    }}
])
```

### 字符串表达式

```javascript
// 连接字符串
db.users.aggregate([
    { $project: {
        fullName: { $concat: ["$firstName", " ", "$lastName"] }
    }}
])

// 截取
db.users.aggregate([
    { $project: {
        firstChar: { $substr: ["$name", 0, 1] },
        shortDesc: { $substr: ["$description", 0, 100] }
    }}
])

// 转大写/小写
db.users.aggregate([
    { $project: {
        upperName: { $toUpper: "$name" },
        lowerName: { $toLower: "$name" }
    }}
])

// 长度
db.users.aggregate([
    { $project: {
        nameLength: { $strLenCP: "$name" }
    }}
])

// 替换
db.users.aggregate([
    { $project: {
        cleanName: {
            $replaceAll: {
                input: "$name",
                find: " ",
                replacement: "-"
            }
        }
    }}
])
```

### 日期表达式

```javascript
// 提取日期部分
db.orders.aggregate([
    { $project: {
        year: { $year: "$createdAt" },
        month: { $month: "$createdAt" },
        day: { $dayOfMonth: "$createdAt" },
        hour: { $hour: "$createdAt" },
        minute: { $minute: "$createdAt" },
        second: { $second: "$createdAt" },
        dayOfWeek: { $dayOfWeek: "$createdAt" },
        dayOfYear: { $dayOfYear: "$createdAt" },
        week: { $week: "$createdAt" }
    }}
])

// 日期转换
db.orders.aggregate([
    { $project: {
        dateString: { $dateToString: {
            format: "%Y-%m-%d",
            date: "$createdAt"
        }}
    }}
])

// 日期运算
db.orders.aggregate([
    { $project: {
        daysSince: {
            $divide: [
                { $subtract: [new Date(), "$createdAt"] },
                1000 * 60 * 60 * 24  // 毫秒转天
            ]
        }
    }}
])
```

### 条件表达式

```javascript
// $ifNull
db.users.aggregate([
    { $project: {
        displayName: { $ifNull: ["$nickname", "$name"] }
    }}
])

// $cond
db.orders.aggregate([
    { $project: {
        discount: {
            $cond: {
                if: { $gte: ["$amount", 1000] },
                then: 0.1,
                else: 0
            }
        }
    }}
])

// $switch
db.orders.aggregate([
    { $project: {
        statusText: {
            $switch: {
                branches: [
                    { case: { $eq: ["$status", "pending"] }, then: "待支付" },
                    { case: { $eq: ["$status", "paid"] }, then: "已支付" },
                    { case: { $eq: ["$status", "shipped"] }, then: "已发货" }
                ],
                default: "未知"
            }
        }
    }}
])
```

### 数组表达式

```javascript
// $size - 数组长度
db.orders.aggregate([
    { $project: {
        itemCount: { $size: "$items" }
    }}
])

// $arrayElemAt - 数组元素
db.orders.aggregate([
    { $project: {
        firstItem: { $arrayElemAt: ["$items", 0] },
        lastItem: { $arrayElemAt: ["$items", -1] }
    }}
])

// $slice - 数组切片
db.orders.aggregate([
    { $project: {
        firstThree: { $slice: ["$items", 3] }
    }}
])

// $filter - 过滤数组
db.orders.aggregate([
    { $project: {
        expensiveItems: {
            $filter: {
                input: "$items",
                as: "item",
                cond: { $gte: ["$$item.price", 100] }
            }
        }
    }}
])

// $map - 映射数组
db.orders.aggregate([
    { $project: {
        itemNames: {
            $map: {
                input: "$items",
                as: "item",
                in: "$$item.name"
            }
        }
    }}
])

// $reduce - 累计
db.orders.aggregate([
    { $project: {
        totalDiscount: {
            $reduce: {
                input: "$discounts",
                initialValue: 0,
                in: { $add: ["$$value", "$$this"] }
            }
        }
    }}
])

// $in - 包含
db.products.aggregate([
    { $match: {
        tags: { $in: ["sale", "clearance"] }
    }}
])
```

## Java 实现

```java
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MongoAggregationDemo {
    private MongoCollection<Document> orders;

    // 基本聚合
    public void basicAggregate() {
        // 按状态统计
        orders.aggregate(Arrays.asList(
            Aggregates.group("$status",
                com.mongodb.client.model.Accumulators.count("count", 1),
                com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount")
            )
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    // 多阶段聚合
    public void multiStageAggregate() {
        orders.aggregate(Arrays.asList(
            // 筛选
            Aggregates.match(Filters.eq("status", "completed")),
            // 分组
            Aggregates.group("$userId",
                com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount"),
                com.mongodb.client.model.Accumulators.avg("avgAmount", "$amount"),
                com.mongodb.client.model.Accumulators.count("orderCount", 1)
            ),
            // 筛选分组结果
            Aggregates.match(Filters.gte("totalAmount", 1000)),
            // 排序
            Aggregates.sort(Sorts.descending("totalAmount")),
            // 限制
            Aggregates.limit(10)
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    // $unwind 示例
    public void unwindExample() {
        orders.aggregate(Arrays.asList(
            Aggregates.unwind("$items"),
            Aggregates.project(Document.parse(
                "{orderId: '$_id', productId: '$items.productId', quantity: '$items.quantity'}"
            ))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    // $lookup 示例
    public void lookupExample(MongoDatabase db) {
        MongoCollection<Document> users = db.getCollection("users");

        orders.aggregate(Arrays.asList(
            Aggregates.lookup("users", "userId", "_id", "userInfo"),
            Aggregates.project(Document.parse(
                "{orderId: '$_id', amount: 1, user: { $arrayElemAt: ['$userInfo', 0] }}"
            ))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    // $facet - 多管道
    public void facetExample() {
        orders.aggregate(Arrays.asList(
            Aggregates.facet(
                // 子管道1：按状态统计
                Aggregates.group("$status",
                    com.mongodb.client.model.Accumulators.count("count", 1)
                ),
                // 子管道2：按月统计
                Aggregates.group(new Document("year", new Document("$year", "$createdAt"))
                        .append("month", new Document("$month", "$createdAt")),
                    com.mongodb.client.model.Accumulators.sum("totalAmount", "$amount")
                ),
                // 子管道3：前10大单
                Aggregates.sort(Sorts.descending("amount")),
                Aggregates.limit(10)
            )
        )).forEach(doc -> System.out.println(doc.toJson()));
    }

    // 复杂表达式
    public void complexAggregate() {
        orders.aggregate(Arrays.asList(
            Aggregates.project(Document.parse(
                "{orderId: '$_id', amount: 1, " +
                "discount: { $cond: { if: { $gte: ['$amount', 1000] }, then: 0.1, else: 0 } }, " +
                "finalAmount: { $multiply: ['$amount', { $cond: { if: { $gte: ['$amount', 1000] }, then: 0.9, else: 1 } }] }," +
                "year: { $year: '$createdAt' }," +
                "month: { $month: '$createdAt' }" +
                "}"
            ))
        )).forEach(doc -> System.out.println(doc.toJson()));
    }
}
```
