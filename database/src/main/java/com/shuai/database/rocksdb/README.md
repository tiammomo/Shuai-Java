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

## 代码结构

```
rocksdb/
├── RocksDbConfig.java          # 数据库配置
├── RocksDbTemplate.java        # 操作模板类
├── basic/                      # 基本操作
│   ├── BasicOpsDemo.java       # 演示代码
│   └── BasicOpsTest.java       # 单元测试
├── columnfamily/               # 列族操作
│   ├── ColumnFamilyDemo.java   # 演示代码
│   └── ColumnFamilyTest.java   # 单元测试
├── snapshot/                   # 快照
│   ├── SnapshotDemo.java       # 演示代码
│   └── SnapshotTest.java       # 单元测试
├── iterator/                   # 迭代器
│   ├── IteratorDemo.java       # 演示代码
│   └── IteratorTest.java       # 单元测试
└── writebatch/                 # 批量写
    ├── WriteBatchDemo.java     # 演示代码
    └── WriteBatchTest.java     # 单元测试
```

## 快速开始

### 添加依赖

```xml
<dependency>
    <groupId>org.rocksdb</groupId>
    <artifactId>rocksdbjni</artifactId>
    <version>9.7.3</version>
</dependency>
```

### 基本使用

```java
// 创建配置
RocksDbConfig config = new RocksDbConfig("target/mydb");

// 创建模板
try (RocksDbTemplate template = new RocksDbTemplate(config)) {

    // 写入数据
    template.put("key1", "value1");

    // 读取数据
    String value = template.get("key1");
    System.out.println(value); // 输出: value1

    // 删除数据
    template.delete("key1");

    // 检查存在
    boolean exists = template.exists("key1");
}
```

### 使用列族

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
| `put(String key, String value, WriteOptions options)` | 带选项写入 |
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
| `iterator(Snapshot)` | 使用快照创建迭代器 |

## 运行演示

```bash
# 运行所有演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.rocksdb.RocksDbDemo"
```

## 运行测试

```bash
# 运行所有 RocksDB 测试
mvn -pl database test -Dtest="RocksDb*Test"

# 运行基本操作测试
mvn -pl database test -Dtest=BasicOpsTest

# 运行列族测试
mvn -pl database test -Dtest=ColumnFamilyTest

# 运行快照测试
mvn -pl database test -Dtest=SnapshotTest

# 运行迭代器测试
mvn -pl database test -Dtest=IteratorTest

# 运行批量写测试
mvn -pl database test -Dtest=WriteBatchTest
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
