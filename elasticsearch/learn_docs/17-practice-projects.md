# 实践项目

> 本章通过完整的实践项目巩固所学知识，包括项目架构、代码实现和部署运维。

## 学习目标

完成本章学习后，你将能够：
- 从零构建完整的 RAG 系统
- 掌握项目最佳实践
- 理解 DevOps 和监控体系
- 完成系统的部署和运维

## 1. 项目概述

### 1.1 项目目标

构建一个**智能知识库问答系统**，支持：
- 文档上传和管理
- 语义化搜索
- 基于 LLM 的答案生成
- 多轮对话支持

### 1.2 技术架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                     智能知识库问答系统架构                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                      前端层 (Vue.js)                         │   │
│  │        文档管理 | 问答界面 | 知识图谱 | 统计分析             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│         ┌────────────────────┼────────────────────┐                 │
│         ▼                    ▼                    ▼                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐            │
│  │   文档服务   │    │   API 网关   │    │   认证服务   │            │
│  │  (上传/解析) │    │  (Spring    │    │  (JWT)      │            │
│  │             │    │   Cloud)    │    │             │            │
│  └─────────────┘    └─────────────┘    └─────────────┘            │
│         │                    │                    │                 │
│         └────────────────────┼────────────────────┘                 │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    业务服务层                                │   │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │   │
│  │  │ 知识库服务   │  │  RAG 服务    │  │ 问答服务     │         │   │
│  │  └─────────────┘  └─────────────┘  └─────────────┘         │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    基础设施层                                │   │
│  │                                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │   ES (8.18)  │  Milvus (2.5)  │  Redis (7.x)     │    │   │
│  │  │   文本检索    │   向量检索     │   缓存/会话      │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │                                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  SiliconFlow API  │  MinIO  │  MySQL  │  Kafka    │    │   │
│  │  │  Embedding/Rerank │  存储   │  元数据 │  消息队列  │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │                                                              │   │
└─────────────────────────────────────────────────────────────────────┘
```

### 1.3 技术选型

| 组件 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 后端框架 | Spring Cloud | 2023.x | 微服务框架 |
| 向量检索 | Milvus | 2.5.10 | 向量存储和检索 |
| 文本检索 | Elasticsearch | 8.18.0 | 全文检索 |
| 缓存 | Redis | 7.2.4 | 缓存/会话 |
| Embedding | BGE | large-v1.5 | 文本向量化 |
| Reranker | BGE Reranker | v2-m3 | 重排序 |
| LLM | DeepSeek | - | 答案生成 |
| 对象存储 | MinIO | latest | 文件存储 |
| 消息队列 | Kafka | 3.x | 异步处理 |
| 数据库 | MySQL | 8.0 | 元数据存储 |

## 2. 项目结构

```
elasticsearch/
├── deploy/
│   ├── docker-compose.yml          # Docker Compose 编排
│   ├── config/
│   │   ├── elasticsearch/
│   │   ├── milvus/
│   │   └── redis/
│   └── scripts/
│       ├── init.sh                 # 初始化脚本
│       └── backup.sh               # 备份脚本
│
├── src/main/java/com/shuai/elasticsearch/
│   ├── RagApplication.java         # 启动类
│   │
│   ├── config/
│   │   ├── ElasticsearchConfig.java
│   │   ├── MilvusConfig.java
│   │   ├── RedisConfig.java
│   │   └── SiliconFlowConfig.java
│   │
│   ├── controller/
│   │   ├── DocumentController.java
│   │   ├── RagController.java
│   │   └── AdminController.java
│   │
│   ├── service/
│   │   ├── RagService.java         # RAG 主服务
│   │   ├── RetrievalService.java   # 检索服务
│   │   ├── KnowledgeBaseService.java
│   │   ├── DocumentService.java
│   │   └── LlmService.java
│   │
│   ├── embedding/
│   │   ├── BgeEmbeddingService.java
│   │   └── RerankerService.java
│   │
│   ├── model/
│   │   ├── RagRequest.java
│   │   ├── RagResponse.java
│   │   ├── Document.java
│   │   └── KnowledgeDoc.java
│   │
│   └── util/
│       ├── DocumentProcessor.java
│       ├── TextChunkingUtil.java
│       └── ResponsePrinter.java
│
├── src/main/resources/
│   ├── application.yml             # 应用配置
│   ├── application-siliconflow.properties
│   └── logback.xml
│
└── learn_docs/
    ├── 00-elasticsearch-overview.md
    ├── ...
    └── 17-practice-projects.md
```

## 3. 核心代码实现

### 3.1 主配置类

```java
// ElasticsearchConfig.java
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = RestClient.builder(
            new HttpHost(host, port, "http")
        ).build();

        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
        );

        return new ElasticsearchClient(transport);
    }
}

// MilvusConfig.java
@Configuration
public class MilvusConfig {

    @Value("${milvus.host:localhost}")
    private String host;

    @Value("${milvus.port:19530}")
    private int port;

    @Bean
    public MilvusClientV2 milvusClient() {
        ConnectConfig config = ConnectConfig.builder()
            .uri("http://" + host + ":" + port)
            .build();
        return new MilvusClientV2(config);
    }
}

// ApplicationConfig.java
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class RagApplication {

    public static void main(String[] args) {
        SpringApplication.run(RagApplication.class, args);
    }

    @Bean
    public AsyncExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("RAG-Async-");
        executor.initialize();
        return executor;
    }
}
```

### 3.2 RAG 控制器

```java
// RagController.java
@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;
    private final MetricsService metricsService;

    @PostMapping("/ask")
    public ResponseEntity<RagResponse> ask(@RequestBody RagRequest request) {
        return metricsService.recordTimer("rag.ask", () -> {
            if (request.getQuestion() == null || request.getQuestion().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            RagResponse response = ragService.ask(request);
            return ResponseEntity.ok(response);
        });
    }

    @PostMapping("/batch-ask")
    public ResponseEntity<List<RagResponse>> batchAsk(@RequestBody List<RagRequest> requests) {
        List<RagResponse> responses = requests.stream()
            .map(ragService::ask)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> metrics() {
        return ResponseEntity.ok(metricsService.getMetrics());
    }
}
```

## 4. 部署配置

### 4.1 Docker Compose

```yaml
# deploy/docker-compose.yml
version: '3.8'

services:
  # Elasticsearch
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.18.0
    container_name: es-node
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - "ES_JAVA_OPTS=-Xms2g -Xmx2g"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es-data:/usr/share/elasticsearch/data
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:9200/_cluster/health || exit 1"]
      interval: 30s
      timeout: 10s
      retries: 5

  # Milvus
  etcd:
    image: quay.io/coreos/etcd:v3.5.16
    container_name: etcd
    command: etcd -advertise-client-urls http://0.0.0.0:2379 -listen-client-urls http://0.0.0.0:2379
    ports:
      - "2379:2379"

  minio:
    image: minio/minio:latest
    container_name: minio
    command: server /data --console-address :9001
    ports:
      - "9000:9000"
      - "9001:9001"
    environment:
      MINIO_ROOT_USER: admin
      MINIO_ROOT_PASSWORD: admin123
    volumes:
      - minio-data:/data

  milvus:
    image: milvusdb/milvus:v2.5.10
    container_name: milvus
    command: ["milvus", "run", "standalone"]
    ports:
      - "19530:19530"
    volumes:
      - milvus-data:/var/lib/milvus
    depends_on:
      - etcd
      - minio

  # Redis
  redis:
    image: redis:7.2.4-alpine
    container_name: redis
    ports:
      - "6379:6379"
    command: redis-server --appendonly yes
    volumes:
      - redis-data:/data

  # 应用服务
  rag-app:
    build: ..
    container_name: rag-app
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      elasticsearch:
        condition: service_healthy
      milvus:
        condition: service_started
      redis:
        condition: service_started
    volumes:
      - app-logs:/var/log/app

volumes:
  es-data:
  milvus-data:
  minio-data:
  redis-data:
  app-logs:

networks:
  default:
    name: rag-network
```

### 4.2 应用配置

```yaml
# src/main/resources/application-docker.yml
spring:
  application:
    name: rag-service

  # Elasticsearch
  elasticsearch:
    host: elasticsearch
    port: 9200

  # Redis
  data:
    redis:
      host: redis
      port: 6379

  # 文件上传
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB

# 自定义配置
elasticsearch:
  host: elasticsearch
  port: 9200

milvus:
  host: milvus
  port: 19530

siliconflow:
  api-key: ${SF_API_KEY:}
  embedding:
    model: BAAI/bge-large-zh-v1.5
    url: https://api.siliconflow.cn/v1/embeddings
  reranker:
    model: BAAI/bge-reranker-v2-m3
    url: https://api.siliconflow.cn/v1/rerank
  llm:
    url: https://api.siliconflow.cn/v1/chat/completions

# 日志
logging:
  level:
    root: INFO
    com.shuai.elasticsearch: DEBUG
```

## 5. 运维脚本

### 5.1 初始化脚本

```bash
#!/bin/bash
# deploy/scripts/init.sh

set -e

echo "========================================"
echo "RAG 系统初始化脚本"
echo "========================================"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "错误: 未安装 Docker"
    exit 1
fi

# 启动服务
echo "1. 启动基础设施服务..."
docker-compose up -d elasticsearch milvus redis minio

# 等待服务就绪
echo "2. 等待服务就绪..."
sleep 30

# 检查 ES
until curl -s http://localhost:9200/_cluster/health | grep -q '"status":"green"'; do
    echo "  等待 ES 启动..."
    sleep 5
done
echo "  ES 已就绪"

# 检查 Milvus
until curl -s http://localhost:19530/healthz | grep -q "OK"; do
    echo "  等待 Milvus 启动..."
    sleep 5
done
echo "  Milvus 已就绪"

# 创建索引
echo "3. 创建 ES 索引..."
curl -X PUT "localhost:9200/knowledge_base" -H 'Content-Type: application/json' -d'
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text", "analyzer": "ik_max_word" },
      "content": { "type": "text", "analyzer": "ik_max_word" },
      "vector": { "type": "dense_vector", "dims": 1024, "index": true, "similarity": "cosine" },
      "source": { "type": "keyword" },
      "metadata": { "type": "object" }
    }
  }
}
'

echo "========================================"
echo "初始化完成!"
echo "========================================"
echo "访问地址:"
echo "  - ES: http://localhost:9200"
echo "  - Milvus Attu: http://localhost:8000"
echo "  - MinIO: http://localhost:9001"
```

### 5.2 备份脚本

```bash
#!/bin/bash
# deploy/scripts/backup.sh

set -e

BACKUP_DIR="/data/backup"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

echo "开始备份... $DATE"

# 备份 ES 索引
echo "1. 备份 ES 索引..."
curl -X PUT "localhost:9200/_snapshot/backup" -H 'Content-Type: application/json' -d'
{
  "type": "fs",
  "settings": {
    "location": "/usr/share/elasticsearch/snapshots"
  }
}
'
curl -X PUT "localhost:9200/_snapshot/backup/snapshot_$DATE?wait_for_completion=true"

# 备份 Milvus (元数据)
echo "2. 备份 Milvus 元数据..."
docker exec etcd etcdctl snapshot save $BACKUP_DIR/milvus_$DATE.db

# 备份 Redis
echo "3. 备份 Redis..."
docker exec redis redis-cli BGSAVE
docker cp redis:/data/dump.rdb $BACKUP_DIR/redis_$DATE.rdb

echo "备份完成: $BACKUP_DIR"
ls -lh $BACKUP_DIR
```

## 6. 测试验证

### 6.1 单元测试

```java
// RagServiceTest.java
@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private RetrievalService retrievalService;

    @Mock
    private LlmService llmService;

    @Mock
    private BgeEmbeddingService embeddingService;

    @Mock
    private RerankerService rerankerService;

    @InjectMocks
    private RagService ragService;

    @Test
    void testAsk_Success() {
        // Given
        RagRequest request = new RagRequest();
        request.setQuestion("Elasticsearch 如何配置?");

        List<RetrievalService.RetrievalResult> candidates = Arrays.asList(
            createMockResult("id1", "ES 配置说明...", 0.9f)
        );

        when(retrievalService.retrieve(anyString(), anyInt())).thenReturn(candidates);
        when(rerankerService.rerank(anyString(), anyList(), anyInt()))
            .thenReturn(Arrays.asList(new RerankResult(0, "ES 配置说明...", 0.95f)));
        when(llmService.generate(anyString(), anyString()))
            .thenReturn("Elasticsearch 配置步骤...");

        // When
        RagResponse response = ragService.ask(request);

        // Then
        assertNotNull(response);
        assertEquals("Elasticsearch 如何配置?", response.getQuestion());
        assertNotNull(response.getAnswer());
        assertTrue(response.getElapsedMs() >= 0);
    }

    private RetrievalService.RetrievalResult createMockResult(String id, String content, float score) {
        RetrievalService.RetrievalResult result = new RetrievalService.RetrievalResult();
        result.setId(id);
        result.setContent(content);
        result.setScore(score);
        return result;
    }
}
```

### 6.2 集成测试

```java
// RagIntegrationTest.java
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RagIntegrationTest {

    @Autowired
    private RagService ragService;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @BeforeAll
    void setup() throws Exception {
        // 准备测试数据
        KnowledgeDoc doc = new KnowledgeDoc();
        doc.setTitle("测试文档");
        doc.setContent("这是一篇关于 Elasticsearch 的测试文档，包含配置和使用的相关信息。");
        doc.setSource("test");

        knowledgeBaseService.addDocument(doc);
    }

    @Test
    void testEndToEnd() {
        RagRequest request = new RagRequest();
        request.setQuestion("Elasticsearch 如何配置?");
        request.setTopK(5);

        RagResponse response = ragService.ask(request);

        assertNotNull(response);
        assertNotNull(response.getAnswer());
        assertTrue(response.getElapsedMs() > 0);
    }
}
```

## 7. 项目总结

### 7.1 学习路径

```
学习进度回顾:
├── 阶段 1: 基础 (1-3 周)
│   ├── ES 核心概念和 CRUD 操作
│   ├── 查询类型和聚合分析
│   └── 索引管理和优化
│
├── 阶段 2: 进阶 (4-6 周)
│   ├── 嵌套查询和评分函数
│   ├── 全文检索和高亮
│   └── 地理查询
│
├── 阶段 3: 向量检索 (7-9 周)
│   ├── Milvus 入门和向量索引
│   ├── 向量检索算法
│   └── Embedding 模型
│
├── 阶段 4: RAG (10-12 周)
│   ├── 两阶段检索架构
│   ├── RAG 系统实现
│   └── 性能优化
│
└── 阶段 5: 实践 (13-14 周)
    ├── 项目构建
    └── 部署运维
```

### 7.2 扩展方向

| 方向 | 说明 |
|------|------|
| 多模态 | 图像、音频检索 |
| 多语言 | 跨语言检索 |
| 个性化 | 用户画像优化 |
| 实时更新 | 流式知识库更新 |

## 8. 扩展阅读

**代码位置**:
- [ElasticsearchDemo.java](src/main/java/com/shuai/elasticsearch/ElasticsearchDemo.java) - 主入口类
- [ElasticsearchConfig.java](src/main/java/com/shuai/elasticsearch/config/ElasticsearchConfig.java) - 配置类
- [BlogDataGenerator.java](src/main/java/com/shuai/elasticsearch/data/BlogDataGenerator.java) - 测试数据生成器

- [Spring Cloud](https://spring.io/projects/spring-cloud)
- [Milvus Best Practices](https://milvus.io/docs/overview.md)
- [Docker Docs](https://docs.docker.com/)
- [项目源码](https://github.com/your-repo/elasticsearch-rag)
- [上一章: 性能优化](16-performance-optimization.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
