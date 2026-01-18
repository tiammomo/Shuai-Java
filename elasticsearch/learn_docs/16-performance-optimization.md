# 性能优化

> 本章讲解 ES + Milvus + RAG 系统的性能优化策略，包括查询优化、缓存策略和资源调优。

## 学习目标

完成本章学习后，你将能够：
- 优化向量检索性能
- 实现多级缓存策略
- 调整 ES 和 Milvus 参数
- 构建性能监控体系

## 1. 向量检索优化

### 1.1 索引参数调优

```java
// Milvus 索引优化配置
public class MilvusOptimizationConfig {

    /**
     * HNSW 索引优化参数
     */
    public static CreateIndexRequest hnswIndex() {
        return CreateIndexReq.builder()
            .collectionName("documents")
            .fieldName("vector")
            .indexType("HNSW")
            .metricType("COSINE")
            .extraParams(Map.of(
                // 构造参数
                "M", 16,                  // 邻居数 (4-64)
                "efConstruction", 200,    // 构造搜索深度 (100-500)

                // 内存优化
                "block_cache_size", "2G"
            ))
            .build();
    }

    /**
     * IVF 索引优化 (大数据量)
     */
    public static CreateIndexRequest ivfIndex(int dataSize) {
        int nlist = (int) (Math.sqrt(dataSize) * 4);

        return CreateIndexReq.builder()
            .collectionName("documents")
            .fieldName("vector")
            .indexType("IVF_SQ8")  // 标量量化，内存节省 4x
            .metricType("COSINE")
            .extraParams(Map.of(
                "nlist", nlist,      // 聚类数量
                "nprobe", 16         // 查询探测数
            ))
            .build();
    }
}
```

### 1.2 查询优化

```java
// 查询优化策略
public class QueryOptimizer {

    /**
     * 异步查询
     */
    public CompletableFuture<SearchResult> asyncSearch(Query query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return performSearch(query);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 批量查询
     */
    public List<SearchResult> batchSearch(List<Query> queries) {
        // 并行执行多个查询
        return queries.parallelStream()
            .map(this::performSearch)
            .collect(Collectors.toList());
    }

    /**
     * 限制返回字段
     */
    public SearchReq optimizeFields(SearchReq request) {
        return SearchReq.builder()
            .collectionName(request.getCollectionName())
            .data(request.getData())
            .topK(request.getTopK())
            .outputFields(Arrays.asList("id", "text"))  // 只返回必要字段
            .build();
    }
}
```

### 1.3 数据分区优化

```java
// Milvus 分区策略
public class PartitionStrategy {

    /**
     * 按时间分区
     */
    public void createTimeBasedPartitions() {
        // 创建月度分区
        for (int month = 1; month <= 12; month++) {
            String partitionName = String.format("partition_2024_%02d", month);
            CreatePartitionReq req = CreatePartitionReq.builder()
                .collectionName("documents")
                .partitionName(partitionName)
                .build();
            client.createPartition(req);
        }
    }

    /**
     * 按类别分区
     */
    public void createCategoryPartitions() {
        String[] categories = {"技术", "产品", "运营", "市场"};
        for (String category : categories) {
            String partitionName = "partition_" + category;
            CreatePartitionReq req = CreatePartitionReq.builder()
                .collectionName("documents")
                .partitionName(partitionName)
                .build();
            client.createPartition(req);
        }
    }

    /**
     * 分区搜索
     */
    public SearchResult searchInPartition(String query, String partitionName) {
        SearchReq searchReq = SearchReq.builder()
            .collectionName("documents")
            .data(Collections.singletonList(query))
            .topK(10)
            .partitionNames(Arrays.asList(partitionName))
            .build();

        return client.search(searchReq);
    }
}
```

## 2. ES 性能优化

### 2.1 索引优化

```bash
# 1. 索引设置优化
PUT /blog
{
  "settings": {
    "number_of_shards": 3,           # 根据节点数和数据量
    "number_of_replicas": 1,
    "refresh_interval": "5s",        # 批量写入时增大
    "merge.scheduler.max_thread_count": 1,
    "store.type": "fs"
  }
}

# 2. 映射优化
PUT /blog/_mapping
{
  "properties": {
    "title": {
      "type": "text",
      "analyzer": "ik_max_word",
      "norms": false,           # 不需要评分时禁用
      "index_options": "positions"  # 只用于搜索
    },
    "vector": {
      "type": "dense_vector",
      "dims": 1024,
      "index": true,
      "similarity": "cosine",
      "index_options": {
        "type": "hnsw",
        "m": 16,
        "ef_construction": 200
      }
    }
  }
}

# 3. Force Merge (减少段数量)
POST /blog/_forcemerge?max_num_segments=1
```

### 2.2 查询优化

```java
// 查询优化实践
public class EsQueryOptimizer {

    /**
     * 使用 filter 上下文
     */
    public SearchResponse<BlogDocument> filterQuery(ElasticsearchClient client) {
        return client.search(s -> s
            .index("blog")
            .query(q -> q
                .bool(b -> b
                    .must(m -> m
                        .match(mt -> mt.field("title").query("Elasticsearch"))
                    )
                    .filter(f -> f
                        .term(t -> t.field("status").value("published"))
                    )
                )
            )
        , BlogDocument.class);
    }

    /**
     * 分页优化 (避免深度分页)
     */
    public SearchResponse<BlogDocument> paginatedQuery(ElasticsearchClient client,
                                                        int from, int size) {
        // 使用 search_after 替代 from/size
        return client.search(s -> s
            .index("blog")
            .query(q -> q.matchAll(m -> m))
            .sort(so -> so.field(f -> f.field("views").order(SortOrder.Desc)))
            .searchAfter(Collections.emptyList())  // 配合 sort 使用
            .size(size)
        , BlogDocument.class);
    }

    /**
     * 字段过滤
     */
    public SearchResponse<BlogDocument> sourceFilter(ElasticsearchClient client) {
        return client.search(s -> s
            .index("blog")
            .query(q -> q.matchAll(m -> m))
            .source(sf -> sf
                .includes(List.of("id", "title", "author"))
                .excludes(List.of("content"))
            )
        , BlogDocument.class);
    }
}
```

## 3. 缓存策略

### 3.1 多级缓存架构

```
┌─────────────────────────────────────────────────────────────────────┐
│                        多级缓存架构                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    L1: 本地缓存 (Caffeine)                   │   │
│  │                                                              │   │
│  │   • 大小: 10000 条                                           │   │
│  │   • TTL: 5 分钟                                             │   │
│  │   • 命中率: ~60%                                            │   │
│  │   • 延迟: < 1ms                                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   L2: 分布式缓存 (Redis)                     │   │
│  │                                                              │   │
│  │   • 大小: 1GB                                               │   │
│  │   • TTL: 1 小时                                            │   │
│  │   • 命中率: ~80%                                            │   │
│  │   • 延迟: < 10ms                                            │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│                              ▼                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   L3: 向量数据库 (Milvus)                    │   │
│  │                                                              │   │
│  │   • 存储: 全部向量                                           │   │
│  │   • TTL: 无                                                 │   │
│  │   • 命中率: 100%                                            │   │
│  │   • 延迟: 10-50ms                                           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 缓存实现

```java
// QueryCacheService.java
@Service
public class QueryCacheService {

    private final Cache<String, List<RetrievalResult>> localCache;
    private final RedisTemplate<String, List<RetrievalResult>> redisCache;
    private static final String CACHE_PREFIX = "rag:query:";

    public QueryCacheService() {
        // L1: Caffeine 本地缓存
        this.localCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .recordStats()
            .build();

        // L2: Redis 分布式缓存
        this.redisCache = new RedisTemplate<>();
        redisCache.setKeySerializer(new StringRedisSerializer());
        redisCache.setValueSerializer(new JacksonRedisSerializer<>(List.class));
    }

    /**
     * 多级缓存查询
     */
    public List<RetrievalResult> getOrLoad(String query, Supplier<List<RetrievalResult>> loader) {
        String cacheKey = CACHE_PREFIX + hash(query);

        // 1. L1 本地缓存
        List<RetrievalResult> cached = localCache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 2. L2 Redis 缓存
        cached = redisCache.opsForValue().get(cacheKey);
        if (cached != null) {
            localCache.put(cacheKey, cached);
            return cached;
        }

        // 3. 加载数据
        cached = loader.get();

        // 4. 回写缓存
        if (cached != null && !cached.isEmpty()) {
            localCache.put(cacheKey, cached);
            redisCache.opsForValue().set(cacheKey, cached, Duration.ofHours(1));
        }

        return cached;
    }

    /**
     * 缓存命中率统计
     */
    public CacheStats getStats() {
        CacheStats l1Stats = new CacheStats(
            localCache.stats().hitCount(),
            localCache.stats().missCount(),
            localCache.size()
        );
        return l1Stats;
    }

    private String hash(String text) {
        return Integer.toHexString(text.hashCode());
    }
}
```

### 3.3 Embedding 缓存

```java
// EmbeddingCacheService.java
@Service
public class EmbeddingCacheService {

    private final Cache<String, List<Float>> embeddingCache;

    public EmbeddingCacheService() {
        this.embeddingCache = Caffeine.newBuilder()
            .maximumSize(50000)
            .expireAfterWrite(Duration.ofHours(24))
            .recordStats()
            .build();
    }

    /**
     * 带缓存的向量化
     */
    public List<Float> embedWithCache(String text, BgeEmbeddingService embeddingService) {
        String cacheKey = "emb:" + hash(text);

        return embeddingCache.get(cacheKey, () -> {
            try {
                return embeddingService.embed(text);
            } catch (Exception e) {
                // API 失败时使用模拟向量
                return generateMockVector();
            }
        });
    }

    /**
     * 批量向量化 (带缓存)
     */
    public List<List<Float>> embedBatchWithCache(List<String> texts,
                                                  BgeEmbeddingService embeddingService) {
        // 1. 先查缓存
        List<List<Float>> results = new ArrayList<>();
        List<String> uncached = new ArrayList<>();

        for (String text : texts) {
            String cacheKey = "emb:" + hash(text);
            List<Float> cached = embeddingCache.getIfPresent(cacheKey);
            if (cached != null) {
                results.add(cached);
            } else {
                uncached.add(text);
                results.add(null);  // 占位
            }
        }

        // 2. 批量 API 调用未缓存的
        if (!uncached.isEmpty()) {
            List<List<Float>> newEmbeddings = embeddingService.embed(uncached);

            // 3. 回写缓存
            int idx = 0;
            for (int i = 0; i < texts.size(); i++) {
                if (results.get(i) == null) {
                    results.set(i, newEmbeddings.get(idx));
                    String cacheKey = "emb:" + hash(texts.get(i));
                    embeddingCache.put(cacheKey, newEmbeddings.get(idx));
                    idx++;
                }
            }
        }

        return results;
    }
}
```

## 4. 资源调优

### 4.1 ES 内存配置

```yaml
# jvm.options
# 堆内存设置为机器内存的 50%，不超过 32GB
-Xms16g
-Xmx16g

# 堆外内存 (用于向量索引)
-XX:MaxDirectMemorySize=8g

# GC 调优
-XX:+UseG1GC
-XX:G1ReservePercent=15
-XX:InitiatingHeapOccupancyPercent=45
-XX:G1HeapRegionSize=16m
```

### 4.2 Milvus 资源调优

```yaml
# milvus.yaml
# 内存配置
memory:
  enableExternal: true        # 启用外部存储
  preloadCollection: []       # 预加载的集合
  cacheSize: 32GB             # 缓存大小

# 索引构建
index:
  buildCacheSize: 16GB        # 索引构建缓存

# 查询调度
scheduler:
  maxReadConcurrentRate: 32   # 最大并发查询数
```

### 4.3 连接池配置

```java
// HttpClient 连接池优化
public class HttpClientConfig {

    @Bean
    public HttpClient embeddingHttpClient() {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .executor(Executors.newFixedThreadPool(10))
            .build();
    }

    @Bean
    public RestClient esRestClient() {
        return RestClient.builder(
            new HttpHost("localhost", 9200, "http")
        )
        .setHttpClientConfigCallback(httpClientBuilder ->
            httpClientBuilder
                .setMaxConnTotal(100)
                .setMaxConnPerRoute(20)
                .setConnectionRequestTimeout(5000)
        )
        .build();
    }
}
```

## 5. 监控指标

### 5.1 关键监控指标

```
┌─────────────────────────────────────────────────────────────────────┐
│                        监控指标体系                                   │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     应用层指标                               │   │
│  │  • QPS (每秒请求数)                                          │   │
│  │  • P99 延迟                                                   │   │
│  │  • 错误率                                                     │   │
│  │  • 缓存命中率                                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                      │
│         ┌────────────────────┼────────────────────┐                 │
│         ▼                    ▼                    ▼                 │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐            │
│  │   ES 指标    │    │ Milvus 指标  │    │ LLM 指标     │            │
│  │─────────────│    │─────────────│    │─────────────│            │
│  │ • 索引大小   │    │ • 查询延迟   │    │ • 生成耗时   │            │
│  │ • 查询耗时   │    │ • 索引构建   │    │ • Token 消耗 │            │
│  │ • 缓存命中   │    │ • 内存使用   │    │ • 错误率     │            │
│  └─────────────┘    └─────────────┘    └─────────────┘            │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 监控实现

```java
// MetricsService.java
@Service
public class MetricsService {

    private final MeterRegistry meterRegistry;
    private final Timer queryTimer;
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final DistributionSummary cacheHitSummary;

    public MetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.queryTimer = meterRegistry.timer("rag.query.duration");
        this.requestCounter = meterRegistry.counter("rag.requests.total");
        this.errorCounter = meterRegistry.counter("rag.errors.total");
        this.cacheHitSummary = meterRegistry.summary("rag.cache.hits");
    }

    /**
     * 记录查询
     */
    public <T> T recordQuery(String operation, Supplier<T> supplier) {
        requestCounter.increment();

        return queryTimer.record(supplier);
    }

    /**
     * 记录错误
     */
    public void recordError(String operation, Exception e) {
        errorCounter.increment();
        meterRegistry.counter("rag.errors", "operation", operation,
            "error", e.getClass().getSimpleName()).increment();
    }

    /**
     * 记录缓存命中
     */
    public void recordCacheHit(String level, boolean hit) {
        String type = hit ? "hit" : "miss";
        meterRegistry.counter("rag.cache", "level", level, "type", type).increment();
    }
}
```

## 6. 实践部分

### 6.1 性能测试

```java
// PerformanceTest.java
public class PerformanceTest {

    private final RagService ragService;
    private final QueryCacheService cacheService;

    /**
     * 负载测试
     */
    public void loadTest(int concurrentUsers, int iterations) {
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);
        CountDownLatch latch = new CountDownLatch(iterations);

        long start = System.currentTimeMillis();
        IntStream.range(0, iterations).forEach(i -> {
            executor.submit(() -> {
                try {
                    RagRequest request = new RagRequest();
                    request.setQuestion("Elasticsearch 集群配置");
                    ragService.ask(request);
                } finally {
                    latch.countDown();
                }
            });
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long elapsed = System.currentTimeMillis() - start;
        double throughput = iterations / (elapsed / 1000.0);

        System.out.println("负载测试结果:");
        System.out.printf("  并发用户: %d%n", concurrentUsers);
        System.out.printf("  总请求数: %d%n", iterations);
        System.out.printf("  总耗时: %d ms%n", elapsed);
        System.out.printf("  吞吐量: %.2f req/s%n", throughput);
    }

    /**
     * 缓存效果对比
     */
    public void cacheComparison() {
        String query = "Elasticsearch 集群配置";

        // 首次查询 (无缓存)
        long start1 = System.currentTimeMillis();
        ragService.ask(new RagRequest(query));
        long firstTime = System.currentTimeMillis() - start1;

        // 第二次查询 (有缓存)
        long start2 = System.currentTimeMillis();
        ragService.ask(new RagRequest(query));
        long cachedTime = System.currentTimeMillis() - start2;

        System.out.println("缓存效果对比:");
        System.out.printf("  首次查询: %d ms%n", firstTime);
        System.out.printf("  缓存查询: %d ms%n", cachedTime);
        System.out.printf("  加速比: %.2fx%n", (double) firstTime / cachedTime);
    }
}
```

## 7. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 向量检索慢 | HNSW 参数不当 | 增加 ef 参数 |
| ES 内存不足 | 堆配置太小 | 调整 JVM 堆 |
| 缓存命中率低 | Key 设计不当 | 优化 hash 算法 |
| API 调用超时 | 网络延迟 | 使用连接池 + 重试 |

## 8. 扩展阅读

**代码位置**:
- [AsyncClientDemo.java](src/main/java/com/shuai/elasticsearch/async/AsyncClientDemo.java) - 异步客户端演示
- [DataInitUtil.java](src/main/java/com/shuai/elasticsearch/util/DataInitUtil.java) - 数据初始化工具

- [ES Performance Tuning](https://www.elastic.co/guide/en/elasticsearch/reference/current/tune-for-search-speed.html)
- [Milvus Performance](https://milvus.io/docs/performance-tuning.md)
- [Caffeine Cache](https://github.com/ben-manes/caffeine)
- [Micrometer Metrics](https://micrometer.io/)
- [上一章: RAG 实践](15-rag-practice.md) | [下一章: 实践项目](17-practice-projects.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
