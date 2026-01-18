# RocksDB 嵌入式键值存储

## 简介

RocksDB 是 Facebook 开源的嵌入式键值存储库，基于 LevelDB，由 C++ 实现，优化了性能和扩展性。

## 核心特点

| 特性 | 说明 |
|------|------|
| 嵌入式 | 无需独立进程，直接嵌入应用 |
| 高性能 | 多线程 compaction，支持多核 CPU |
| 可配置性 | 高度可配置的存储引擎 |
| 列族支持 | 支持多列族，逻辑隔离数据 |
| 插件化 | 支持自定义压缩、比较器等 |

## 应用场景

- 数据库引擎（如 MyRocks）
- 消息队列（如 Kafka）
- 缓存层
- 时序数据库（如 InfluxDB）
- 区块链存储

## 添加依赖

```xml
<dependency>
    <groupId>org.rocksdb</groupId>
    <artifactId>rocksdbjni</artifactId>
    <version>9.7.3</version>
</dependency>
```

## 基本使用

### 创建配置和模板

```java
// 创建配置
RocksDbConfig config = new RocksDbConfig("target/mydb");

// 创建模板
try (RocksDbTemplate template = new RocksDbTemplate(config)) {
    // 使用模板操作数据库
}
```

### 基本操作

```java
// 写入数据
template.put("key1", "value1");

// 读取数据
String value = template.get("key1");

// 删除数据
template.delete("key1");

// 检查存在
boolean exists = template.exists("key1");
```

### 列族操作

```java
// 创建列族
ColumnFamilyHandle usersHandle = template.createColumnFamily("users");
ColumnFamilyHandle ordersHandle = template.createColumnFamily("orders");

// 使用列族写入
template.put(usersHandle, "user:1", "{\"name\":\"张三\"}");
template.put(ordersHandle, "order:1", "{\"amount\":100}");

// 使用列族读取
String user = template.get(usersHandle, "user:1");
String order = template.get(ordersHandle, "order:1");
```

### 快照操作

```java
// 创建快照
Snapshot snapshot = template.getSnapshot();

// 使用快照读取
// 注意：需要配合 ReadOptions 使用

// 释放快照
template.releaseSnapshot(snapshot);
```

### 迭代器

```java
RocksIterator iterator = template.iterator();
for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
    String key = new String(iterator.key());
    String value = new String(iterator.value());
}
iterator.close();
```

### 批量写入

```java
try (WriteBatch batch = template.createWriteBatch()) {
    batch.put(bytes("key1"), bytes("value1"));
    batch.put(bytes("key2"), bytes("value2"));
    batch.delete(bytes("key3"));
    template.write(batch);
}
```

## API 参考

### RocksDbConfig

| 方法 | 说明 |
|------|------|
| `RocksDbConfig(String dbPath)` | 创建配置，指定数据库路径 |
| `getDbPath()` | 获取数据库路径 |
| `getDbOptions()` | 获取 DBOptions 对象 |
| `addColumnFamily(String name)` | 添加列族 |
| `cleanup()` | 清理数据库目录 |

### RocksDbTemplate

| 方法 | 说明 |
|------|------|
| `put(String key, String value)` | 写入键值对 |
| `get(String key)` | 读取值 |
| `delete(String key)` | 删除键 |
| `exists(String key)` | 检查键是否存在 |
| `createColumnFamily(String name)` | 创建列族 |
| `put(ColumnFamilyHandle, String key, String value)` | 使用列族写入 |
| `get(ColumnFamilyHandle, String key)` | 使用列族读取 |
| `getSnapshot()` | 创建快照 |
| `releaseSnapshot(Snapshot)` | 释放快照 |
| `createWriteBatch()` | 创建 WriteBatch |
| `write(WriteBatch)` | 执行批量写入 |
| `iterator()` | 创建迭代器 |

## 运行演示

```bash
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.rocksdb.RocksDbDemo"
```

## 运行测试

```bash
mvn -pl database test -Dtest="RocksDb*Test"
```

## 完整示例

```java
import com.shuai.database.rocksdb.RocksDbConfig;
import com.shuai.database.rocksdb.RocksDbTemplate;
import org.rocksdb.ColumnFamilyHandle;

public class RocksDbExample {
    public static void main(String[] args) throws Exception {
        RocksDbConfig config = new RocksDbConfig("target/rocksdb-example");
        try (RocksDbTemplate template = new RocksDbTemplate(config)) {

            // 基本操作
            template.put("key1", "value1");
            template.put("key2", "value2");

            // 列族操作
            ColumnFamilyHandle usersHandle = template.createColumnFamily("users");
            template.put(usersHandle, "user:1", "{\"name\":\"张三\",\"age\":25}");
            template.put(usersHandle, "user:2", "{\"name\":\"李四\",\"age\":30}");

            ColumnFamilyHandle ordersHandle = template.createColumnFamily("orders");
            template.put(ordersHandle, "order:1", "{\"amount\":100,\"product\":\"A\"}");

            // 读取数据
            System.out.println("key1 = " + template.get("key1"));
            System.out.println("user:1 = " + template.get(usersHandle, "user:1"));
            System.out.println("order:1 = " + template.get(ordersHandle, "order:1"));

            // 批量写入
            try (org.rocksdb.WriteBatch batch = template.createWriteBatch()) {
                for (int i = 1; i <= 1000; i++) {
                    batch.put(bytes("batch:" + i), bytes("Value_" + i));
                }
                template.write(batch);
            }

            System.out.println("RocksDB 示例完成");
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
```

## 列族最佳实践

### 为什么使用列族？

1. **数据隔离** - 不同类型的数据存储在不同列族
2. **独立配置** - 每个列族可以有不同的压缩策略
3. **性能优化** - 可以只遍历特定列族的数据
4. **备份恢复** - 可以单独备份/恢复列族

### 列族命名建议

```java
// 推荐：按业务域划分列族
ColumnFamilyHandle usersHandle = template.createColumnFamily("users");
ColumnFamilyHandle ordersHandle = template.createColumnFamily("orders");
ColumnFamilyHandle productsHandle = template.createColumnFamily("products");

// 推荐：按数据类型划分列族
ColumnFamilyHandle hotDataHandle = template.createColumnFamily("hot_data");
ColumnFamilyHandle coldDataHandle = template.createColumnFamily("cold_data");
```

## 与 LevelDB 对比

| 特性 | LevelDB | RocksDB |
|------|---------|---------|
| 列族 | 不支持 | 支持 |
| 压缩 | 简单 | 多线程、可配置 |
| Compaction | 单线程 | 多线程 |
| 性能 | 高 | 更高 |
| 生态 | 较小 | 较大（Facebook） |
| 适用场景 | 简单键值存储 | 高吞吐、复杂场景 |

## 性能优化建议

1. **选择合适的压缩算法**
   ```java
   ColumnFamilyOptions options = new ColumnFamilyOptions();
   options.setCompressionType(CompressionType.LZ4_COMPRESSION);
   ```

2. **调整 WriteBufferSize**
   ```java
   DBOptions dbOptions = new DBOptions();
   dbOptions.setWriteBufferSize(64 * 1024 * 1024); // 64MB
   ```

3. **使用列族隔离热数据**
   ```java
   // 热数据使用小 WriteBuffer
   // 冷数据使用大 WriteBuffer + 压缩
   ```

4. **批量写入**
   ```java
   // 使用 WriteBatch 减少 I/O 操作
   ```

## 注意事项

1. **必须加载本地库**
   ```java
   RocksDB.loadLibrary();
   ```

2. **资源管理**
   - 列族 Handle 使用后需要关闭
   - 迭代器使用后需要关闭
   - 使用 try-with-resources 确保关闭

3. **异常处理**
   - 大部分操作会抛出 `RocksDBException`
   - 需要正确处理异常

4. **线程安全**
   - RocksDB 支持多线程并发读取
   - 写入需要同步控制
