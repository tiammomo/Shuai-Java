# LevelDB 嵌入式键值存储

## 简介

LevelDB 是 Google 开源的嵌入式键值存储库，由 C++ 实现，提供高性能的持久化存储。

## 核心特点

| 特性 | 说明 |
|------|------|
| 嵌入式 | 无需独立进程，直接嵌入应用 |
| 高性能 | 支持百万级写入/秒 |
| 持久化 | 数据存储在磁盘 |
| 排序存储 | 按键排序存储 |
| 快照支持 | 提供一致性的数据视图 |

## 应用场景

- 缓存服务器
- 日志存储
- 消息队列
- 数据库引擎底层（如 IndexedDB）

## 添加依赖

```xml
<dependency>
    <groupId>org.fusesource.leveldbjni</groupId>
    <artifactId>leveldbjni-all</artifactId>
    <version>1.8</version>
</dependency>
```

## 基本使用

### 创建配置和模板

```java
// 创建配置
LevelDbConfig config = new LevelDbConfig("target/mydb");

// 创建模板
try (LevelDbTemplate template = new LevelDbTemplate(config)) {
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

### 快照操作

```java
// 创建快照
Snapshot snapshot = template.getSnapshot();

// 使用快照读取（读取一致性数据）
String value = template.get("key1", snapshot);

// 释放快照
// 注意: leveldbjni 中快照会在 GC 时自动释放
```

### 迭代器

```java
try (DBIterator iterator = template.iterator()) {
    // 遍历所有数据
    iterator.seekToFirst();
    while (iterator.hasNext()) {
        Map.Entry<byte[], byte[]> entry = iterator.next();
        String key = new String(entry.getKey());
        String value = new String(entry.getValue());
    }
}
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

### LevelDbConfig

| 方法 | 说明 |
|------|------|
| `LevelDbConfig(String dbPath)` | 创建配置，指定数据库路径 |
| `getDbPath()` | 获取数据库路径 |
| `getOptions()` | 获取 Options 对象 |
| `cleanup()` | 清理数据库目录 |

### LevelDbTemplate

| 方法 | 说明 |
|------|------|
| `put(String key, String value)` | 写入键值对 |
| `get(String key)` | 读取值 |
| `delete(String key)` | 删除键 |
| `exists(String key)` | 检查键是否存在 |
| `getSnapshot()` | 创建快照 |
| `createWriteBatch()` | 创建 WriteBatch |
| `write(WriteBatch)` | 执行批量写入 |
| `iterator()` | 创建迭代器 |

## 运行演示

```bash
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.leveldb.LevelDbDemo"
```

## 运行测试

```bash
mvn -pl database test -Dtest="LevelDb*Test"
```

## 完整示例

```java
import com.shuai.database.leveldb.LevelDbConfig;
import com.shuai.database.leveldb.LevelDbTemplate;
import org.iq80.leveldb.WriteOptions;

public class LevelDbExample {
    public static void main(String[] args) throws Exception {
        LevelDbConfig config = new LevelDbConfig("target/leveldb-example");
        try (LevelDbTemplate template = new LevelDbTemplate(config)) {

            // 基本操作
            template.put("user:1", "{\"name\":\"张三\",\"age\":25}");
            template.put("user:2", "{\"name\":\"李四\",\"age\":30}");

            // 读取
            String user1 = template.get("user:1");
            System.out.println("user:1 = " + user1);

            // 快照
            Snapshot snapshot = template.getSnapshot();
            String snapshotValue = template.get("user:1", snapshot);
            System.out.println("快照中的值: " + snapshotValue);

            // 批量写入
            try (org.iq80.leveldb.WriteBatch batch = template.createWriteBatch()) {
                for (int i = 1; i <= 1000; i++) {
                    batch.put(bytes("batch:" + i), bytes("Value_" + i));
                }
                template.write(batch);
            }

            System.out.println("LevelDB 示例完成");
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
```

## 与 RocksDB 对比

| 特性 | LevelDB | RocksDB |
|------|---------|---------|
| 列族 | 不支持 | 支持 |
| 压缩 | 简单 | 多线程、可配置 |
| Compaction | 单线程 | 多线程 |
| 性能 | 高 | 更高 |
| 生态 | 较小 | 较大（Facebook） |

## 注意事项

1. **嵌入式使用** - LevelDB 是嵌入式数据库，不需要独立部署
2. **线程安全** - 不支持多线程并发写入
3. **数据清理** - 每次打开会清理旧数据（演示用途）
4. **资源关闭** - 使用 try-with-resources 确保资源正确关闭
