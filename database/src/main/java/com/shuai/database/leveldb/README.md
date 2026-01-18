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

## 代码结构

```
leveldb/
├── LevelDbConfig.java          # 数据库配置
├── LevelDbTemplate.java        # 操作模板类
├── basic/                      # 基本操作
│   ├── BasicOpsDemo.java       # 演示代码
│   └── BasicOpsTest.java       # 单元测试
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
    <groupId>org.fusesource.leveldbjni</groupId>
    <artifactId>leveldbjni-all</artifactId>
    <version>1.8</version>
</dependency>
```

### 基本使用

```java
// 创建配置
LevelDbConfig config = new LevelDbConfig("target/mydb");

// 创建模板
try (LevelDbTemplate template = new LevelDbTemplate(config)) {

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
| `put(String key, String value, WriteOptions options)` | 带选项写入 |
| `get(String key)` | 读取值 |
| `get(String key, Snapshot snapshot)` | 使用快照读取 |
| `delete(String key)` | 删除键 |
| `exists(String key)` | 检查键是否存在 |
| `getSnapshot()` | 创建快照 |
| `createWriteBatch()` | 创建 WriteBatch |
| `write(WriteBatch batch)` | 执行批量写入 |
| `iterator()` | 创建迭代器 |
| `iterator(Snapshot snapshot)` | 使用快照创建迭代器 |

## 运行演示

```bash
# 运行所有演示
mvn -pl database exec:java -Dexec.mainClass="com.shuai.database.leveldb.LevelDbDemo"
```

## 运行测试

```bash
# 运行所有 LevelDB 测试
mvn -pl database test -Dtest="LevelDb*Test"

# 运行基本操作测试
mvn -pl database test -Dtest=BasicOpsTest

# 运行快照测试
mvn -pl database test -Dtest=SnapshotTest

# 运行迭代器测试
mvn -pl database test -Dtest=IteratorTest

# 运行批量写测试
mvn -pl database test -Dtest=WriteBatchTest
```

## 与 RocksDB 对比

| 特性 | LevelDB | RocksDB |
|------|---------|---------|
| 列族 | 不支持 | 支持 |
| 压缩 | 简单 | 多线程、可配置 |
| Compaction | 单线程 | 多线程 |
| 性能 | 高 | 更高 |
| 生态 | 较小 | 较大（Facebook） |
| 适用场景 | 简单键值存储 | 高吞吐、复杂场景 |
