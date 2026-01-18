# 测试指南

> 系统化的测试流程，验证 Elasticsearch + Milvus + RAG 各模块功能。

## 学习目标

完成本章学习后，你将能够：
- 验证所有依赖服务是否正常运行
- 执行完整的功能测试流程
- 解读测试输出结果
- 排查常见问题

## 1. 前置条件检查

### 1.1 服务启动

```bash
# 进入部署目录
cd elasticsearch/deploy

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 预期输出:
#   Name                 Command               State           Ports
#   -----------------------------------------------------------------
#   es01     /bin/tini -- /usr/local/bi ...   Up              9200/tcp, 9300/tcp
#   milvus   [docker-entrypoint.sh]            Up              9091/tcp, 19530/tcp
#   minio    /usr/bin/docker-entrypoint ...   Up              9000/tcp, 9001/tcp
#   etcd     etcd -advertise-client-urls ...  Up              2379/tcp, 2380/tcp
```

### 1.2 健康检查

```bash
# 创建健康检查脚本
mkdir -p deploy/scripts

# 方式1: 使用 Docker
docker exec es01 curl -s http://localhost:9200/_cluster/health

# 方式2: 本地执行
curl -s http://localhost:9200/_cluster/health

# 预期响应:
# {
#   "cluster_name" : "elasticsearch",
#   "status" : "green",
#   "timed_out" : false,
#   "number_of_nodes" : 1,
#   "data_nodes" : 1,
#   "active_primary_shards" : 5,
#   "active_shards" : 5
# }
```

### 1.3 验证 Milvus

```bash
# Milvus 健康检查
curl -s http://localhost:9091/healthz

# 预期响应: OK
```

## 2. 项目编译

### 2.1 Maven 编译

```bash
# 进入项目根目录
cd /home/ubuntu/learn_projects/Shuai-Java

# 编译 Elasticsearch 模块
mvn compile -q -pl elasticsearch

# 预期输出: 无错误信息，生成 target 目录

# 编译全部模块
mvn compile -DskipTests
```

### 2.2 常见编译问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 依赖下载慢 | Maven 仓库连接慢 | 配置阿里云镜像 |
| 内存不足 | Maven 堆内存太小 | 设置 MAVEN_OPTS="-Xmx2g" |
| ES 客户端版本不匹配 | 版本冲突 | 使用 pom.xml 指定的版本 |

## 3. 功能测试

### 3.1 运行完整演示

```bash
# 运行所有演示模块
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo

# 输出保存到文件
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    2>&1 | tee test-output.log
```

### 3.2 分模块测试

```bash
# 测试 ES 概述
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    -Dexec.args="overview"

# 测试文档操作
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    -Dexec.args="document"

# 测试查询功能
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    -Dexec.args="query"

# 测试 RAG
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo \
    -Dexec.args="rag"
```

## 4. 测试输出解读

### 4.1 成功输出示例

```
============================================================
  Elasticsearch 概述
============================================================
  包含: 核心概念、REST API、集群信息

  === 1. 集群健康状态 ===
  集群名称: elasticsearch
  状态: green
  节点数: 1

  === 2. 节点信息 ===
  集群节点数: 1

  === 3. 索引列表 ===
  索引: blog
  索引: product

  测试完成!
```

### 4.2 跳过输出示例

```
============================================================
  Milvus 向量查询演示
============================================================
  包含: 向量插入、相似度搜索、批量操作、过滤搜索

  [跳过] Milvus 向量查询演示需要 Milvus 连接
```

**跳过原因**：
- Milvus 服务未启动
- API Key 未配置
- 网络连接失败

### 4.3 错误输出示例

```
============================================================
  文档操作演示
============================================================
  包含: 插入、更新、删除、批量操作

  执行过程中出现错误: Connection refused
  请确保 Elasticsearch 服务已启动
```

## 5. 验证命令速查

### 5.1 Elasticsearch 验证

```bash
# 集群健康
curl -s http://localhost:9200/_cluster/health?pretty

# 节点信息
curl -s http://localhost:9200/_nodes?pretty

# 查看索引
curl -s http://localhost:9200/_cat/indices?v

# 查看索引映射
curl -s http://localhost:9200/blog/_mapping?pretty

# 测试分词
curl -X POST "localhost:9200/blog/_analyze" \
  -H 'Content-Type: application/json' \
  -d '{"analyzer": "ik_max_word", "text": "Elasticsearch 教程"}'

# 搜索测试
curl -X GET "localhost:9200/blog/_search?pretty" \
  -H 'Content-Type: application/json' \
  -d '{"query": {"match_all": {}}}'
```

### 5.2 Milvus 验证

```bash
# 健康检查
curl -s http://localhost:9091/healthz

# 查看集合 (需要使用 Attu UI 或 Milvus CLI)
# 推荐方式: docker run -p 8000:3000 zilliz/attu
```

### 5.3 Redis 验证

```bash
# 连接测试
redis-cli ping
# 预期输出: PONG

# 查看键
redis-cli keys '*'
```

## 6. 测试结果记录

### 6.1 环境信息

| 项目 | 值 |
|------|-----|
| ES 版本 | 8.18.0 |
| Milvus 版本 | 2.5.10 |
| JDK 版本 | 21 |
| Maven 版本 | 3.9.x |
| Docker 版本 | 24.x |
| 测试时间 | 2026-01-17 |

### 6.2 测试结果

| 模块 | 测试项 | 状态 | 响应时间 | 备注 |
|------|--------|------|----------|------|
| 概述 | 集群状态 | ✅ | <100ms | green |
| 文档 | CRUD 操作 | ✅ | <200ms | 批量插入正常 |
| 查询 | match/term/bool | ✅ | <100ms | 返回结果正确 |
| 嵌套 | nested 查询 | ✅ | <150ms | 需 product 索引 |
| 评分 | function_score | ✅ | <100ms | 脚本评分正常 |
| 聚合 | avg/sum/terms | ✅ | <200ms | 聚合正确 |
| 全文 | IK 分词 | ✅ | <100ms | 中文分词正常 |
| 地理 | geo_distance | ✅ | <100ms | 位置查询正常 |
| 异步 | 异步客户端 | ✅ | <100ms | 并发正常 |
| 索引 | 别名/模板 | ✅ | <200ms | 管理操作正常 |
| 向量 | Milvus 连接 | ✅ | <500ms | Mock 模式正常 |
| RAG | 文档分块 | ⚠️ | - | 需 2G+ JVM 内存 |

**状态说明**:
- ✅ 成功
- ❌ 失败
- ⏭️ 跳过
- ⚠️ 警告 (需要更多资源)

### 6.3 测试日志示例

```bash
# 运行测试
bash elasticsearch/deploy/scripts/run-tests.sh

# 或直接执行
export MAVEN_OPTS="-Xmx2g"
mvn -pl elasticsearch exec:java -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo

# 查看 ES 索引
curl -s "localhost:9200/_cat/indices?v"
```

### 6.4 已知问题

| 问题 ID | 问题描述 | 严重性 | 状态 | 解决方案 |
|---------|----------|--------|------|----------|
| T-001 | RAG 模块 OOM | 中 | 已修复 | 增加 JVM 内存: MAVEN_OPTS="-Xmx2g" |
| T-002 | NestedQuery API 错误 | 低 | 已修复 | 使用 number() 替代 field() |

## 7. 自动化测试脚本

### 7.1 一键测试脚本

```bash
#!/bin/bash
# elasticsearch/deploy/scripts/run-tests.sh

set -e

# 增加 JVM 内存 (RAG 模块需要)
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"

echo "========================================"
echo "Elasticsearch + Milvus 一键测试"
echo "========================================"

# 1. 检查服务
echo "\n[1/5] 检查服务状态..."
bash elasticsearch/deploy/scripts/health-check.sh

# 2. 编译项目
echo "\n[2/5] 编译项目..."
mvn compile -q -pl elasticsearch

# 3. 运行演示
echo "\n[3/5] 运行演示..."
mvn -pl elasticsearch exec:java \
    -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo

# 4. 分析结果
echo "\n[4/5] 测试结果分析..."

# 5. ES 索引验证
echo "\n[5/5] ES 索引验证..."
curl -s "localhost:9200/_cat/indices?v"

echo "\n========================================"
echo "测试完成!"
echo "========================================"
```

### 7.2 健康检查脚本

```bash
#!/bin/bash
# deploy/scripts/health-check.sh

echo "========================================"
echo "服务健康检查"
echo "========================================"

failed=0

check_service() {
    local name=$1
    local cmd=$2

    echo -n "[$name] "
    if eval "$cmd" > /dev/null 2>&1; then
        echo "✅ OK"
    else
        echo "❌ FAILED"
        failed=$((failed + 1))
    fi
}

# ES 健康检查
check_service "Elasticsearch" \
    "curl -s http://localhost:9200/_cluster/health | grep -q '\"status\":\"green\"'"

# Milvus 健康检查
check_service "Milvus" \
    "curl -s http://localhost:9091/healthz | grep -q OK"

# Redis 健康检查
check_service "Redis" \
    "redis-cli ping | grep -q PONG"

echo ""
if [ $failed -eq 0 ]; then
    echo "✅ 所有服务正常运行"
    exit 0
else
    echo "❌ $failed 个服务异常"
    exit 1
fi
```

## 8. 常见问题排查

### 8.1 Elasticsearch 问题

| 问题 | 解决方案 |
|------|----------|
| 连接被拒绝 | 确保 ES 容器正在运行: `docker ps \| grep elasticsearch` |
| 集群状态 yellow | 副本未分配，增加节点或减少副本数 |
| 索引不存在 | 运行 `IndexManagementDemo` 创建索引 |
| IK 分词无效 | 检查插件是否安装: `docker exec es01 ls plugins/` |

### 8.2 Milvus 问题

| 问题 | 解决方案 |
|------|----------|
| 连接超时 | 确保 Milvus 容器正在运行 |
| 集合不存在 | 先创建集合再进行操作 |
| 向量维度不匹配 | 检查 Embedding 模型维度配置 |

### 8.3 编译问题

| 问题 | 解决方案 |
|------|----------|
| 找不到依赖 | 运行 `mvn clean install -DskipTests` |
| 内存不足 | 设置 `MAVEN_OPTS="-Xmx2g"` |
| Java 版本不兼容 | 使用 JDK 21 |

## 9. 性能测试

### 9.1 基准测试

```bash
# 批量插入测试
time curl -X POST "localhost:9200/blog/_bulk" \
  -H 'Content-Type: application/json' \
  --data-binary @test-data.json

# 搜索性能测试
for i in {1..10}; do
    time curl -X GET "localhost:9200/blog/_search" \
        -H 'Content-Type: application/json' \
        -d '{"query": {"match_all": {}}}'
done | grep real
```

### 9.2 性能指标

| 操作 | 预期响应时间 | 实际响应时间 |
|------|-------------|-------------|
| 单条插入 | < 100ms | ___ms |
| 批量插入 (100条) | < 1s | ___ms |
| 简单查询 | < 50ms | ___ms |
| 复杂查询 | < 200ms | ___ms |
| 聚合查询 | < 300ms | ___ms |

## 10. 扩展阅读

**测试类清单**:

| 测试类 | 测试内容 | 文件位置 |
|--------|----------|----------|
| `ElasticsearchConfigTest` | 客户端初始化、单例模式 | [ElasticsearchConfigTest.java](src/test/java/com/shuai/elasticsearch/ElasticsearchConfigTest.java) |
| `DocumentModelTest` | 文档模型创建、属性设置 | [DocumentModelTest.java](src/test/java/com/shuai/elasticsearch/DocumentModelTest.java) |
| `DocumentOperationDemoTest` | 文档 CRUD、批量操作 | [DocumentOperationDemoTest.java](src/test/java/com/shuai/elasticsearch/document/DocumentOperationDemoTest.java) |
| `QueryDemoTest` | 查询类型测试 | [QueryDemoTest.java](src/test/java/com/shuai/elasticsearch/query/QueryDemoTest.java) |
| `AggregationDemoTest` | 聚合分析测试 | [AggregationDemoTest.java](src/test/java/com/shuai/elasticsearch/aggregation/AggregationDemoTest.java) |
| `FullTextSearchTest` | 全文检索测试 | [FullTextSearchTest.java](src/test/java/com/shuai/elasticsearch/fulltext/FullTextSearchTest.java) |
| `GeoQueryTest` | 地理查询测试 | [GeoQueryTest.java](src/test/java/com/shuai/elasticsearch/geo/GeoQueryTest.java) |
| `VectorEmbeddingUtilTest` | 向量嵌入测试 | [VectorEmbeddingUtilTest.java](src/test/java/com/shuai/elasticsearch/util/VectorEmbeddingUtilTest.java) |
| `RAGDemoTest` | RAG 系统测试 | [RAGDemoTest.java](src/test/java/com/shuai/elasticsearch/rag/RAGDemoTest.java) |
| `OpenAIClientTest` | OpenAI 客户端测试 | [OpenAIClientTest.java](src/test/java/com/shuai/elasticsearch/llm/OpenAIClientTest.java) |

- [Elasticsearch 官方测试指南](https://www.elastic.co/guide/en/elasticsearch/reference/current/tests.html)
- [Milvus 测试文档](https://milvus.io/docs/test.md)
- [JUnit 5 教程](https://junit.org/junit5/docs/current/user-guide/)
- [上一章: 实战项目](17-practice-projects.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
