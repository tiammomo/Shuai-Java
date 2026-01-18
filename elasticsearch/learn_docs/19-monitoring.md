# 监控告警

> 本章讲解如何配置 Prometheus 监控和告警规则，实现系统可观测性。

## 学习目标

完成本章学习后，你将能够：
- 理解 Prometheus 指标命名规范
- 配置 ES 和 Milvus 的监控告警
- 使用 Grafana 可视化监控数据

## 1. 指标定义

### 1.1 Elasticsearch 指标

**代码位置**: [MetricNames.java](src/main/java/com/shuai/elasticsearch/monitoring/MetricNames.java)

```java
public final class MetricNames {

    // ============ Elasticsearch 指标 ============

    /**
     * ES 集群健康状态 (0=green, 1=yellow, 2=red)
     */
    public static final String ES_CLUSTER_HEALTH = "elasticsearch_cluster_health";

    /**
     * ES 节点数量
     */
    public static final String ES_NODE_COUNT = "elasticsearch_node_count";

    /**
     * ES 索引数量
     */
    public static final String ES_INDEX_COUNT = "elasticsearch_index_count";

    /**
     * ES 文档数量
     */
    public static final String ES_DOCUMENT_COUNT = "elasticsearch_document_count";

    /**
     * ES 查询延迟 (毫秒)
     */
    public static final String ES_QUERY_LATENCY = "elasticsearch_query_latency_ms";

    /**
     * ES 查询速率 (每秒查询数)
     */
    public static final String ES_QUERY_RATE = "elasticsearch_query_rate";
}
```

### 1.2 Milvus 指标

```java
// ============ Milvus 指标 ============

/**
 * Milvus 集合向量数量
 */
public static final String MILVUS_VECTOR_COUNT = "milvus_vector_count";

/**
 * Milvus 查询延迟 (毫秒)
 */
public static final String MILVUS_QUERY_LATENCY = "milvus_query_latency_ms";

/**
 * Milvus 查询速率
 */
public static final String MILVUS_QUERY_RATE = "milvus_query_rate";

/**
 * Milvus 插入速率
 */
public static final String MILVUS_INSERT_RATE = "milvus_insert_rate";
```

### 1.3 RAG 指标

```java
// ============ RAG 指标 ============

/**
 * Embedding 生成延迟
 */
public static final String RAG_EMBEDDING_LATENCY = "rag_embedding_latency_ms";

/**
 * LLM 生成延迟
 */
public static final String RAG_LLM_LATENCY = "rag_llm_latency_ms";

/**
 * RAG 查询总数
 */
public static final String RAG_QUERY_TOTAL = "rag_query_total";

/**
 * RAG 响应Token数
 */
public static final String RAG_RESPONSE_TOKENS = "rag_response_tokens";
```

### 1.4 JVM 指标

```java
// ============ 应用指标 ============

/**
 * JVM 堆内存使用率
 */
public static final String JVM_HEAP_USAGE = "jvm_heap_usage_percent";

/**
 * JVM GC 次数
 */
public static final String JVM_GC_COUNT = "jvm_gc_count";

/**
 * JVM 线程数
 */
public static final String JVM_THREAD_COUNT = "jvm_thread_count";

/**
 * 应用启动时间
 */
public static final String APP_STARTUP_TIME = "app_startup_time_ms";

/**
 * API 请求总数
 */
public static final String API_REQUEST_TOTAL = "api_request_total";
```

## 2. 监控配置

**代码位置**: [PrometheusConfig.java](src/main/java/com/shuai/elasticsearch/monitoring/PrometheusConfig.java)

```java
/**
 * Prometheus 监控配置类
 */
public class PrometheusConfig {

    private volatile boolean esHealthy = false;
    private volatile boolean milvusHealthy = false;

    // 原子变量用于简单指标存储
    private final AtomicLong esQueryCount = new AtomicLong(0);
    private final AtomicLong milvusQueryCount = new AtomicLong(0);
    private final AtomicLong milvusInsertCount = new AtomicLong(0);
    private final AtomicLong ragQueryCount = new AtomicLong(0);
    private final AtomicLong esDocumentCount = new AtomicLong(0);
    private final AtomicLong milvusVectorCount = new AtomicLong(0);

    /**
     * 记录 ES 查询
     */
    public void recordEsQuery(long latencyMs) {
        esQueryCount.incrementAndGet();
        System.out.println("[Metric] ES query latency: " + latencyMs + "ms");
    }

    /**
     * 记录 Milvus 查询
     */
    public void recordMilvusQuery(long latencyMs) {
        milvusQueryCount.incrementAndGet();
        System.out.println("[Metric] Milvus query latency: " + latencyMs + "ms");
    }

    /**
     * 记录 Milvus 插入
     */
    public void recordMilvusInsert(int count) {
        milvusInsertCount.addAndGet(count);
    }

    /**
     * 记录 RAG 查询
     */
    public void incrementRagQuery() {
        ragQueryCount.incrementAndGet();
    }

    /**
     * 获取 ES 查询计数
     */
    public long getEsQueryCount() {
        return esQueryCount.get();
    }

    /**
     * 获取监控配置摘要
     */
    public String getSummary() {
        return String.format("PrometheusConfig{EShealthy=%s, MilvusHealthy=%s, ESqueries=%d, Milvusqueries=%d}",
            esHealthy, milvusHealthy, esQueryCount.get(), milvusQueryCount.get());
    }
}
```

## 3. Prometheus 配置

### 3.1 Prometheus 主配置

**配置文件**: [prometheus.yml](deploy/prometheus/prometheus.yml)

```yaml
# Prometheus 配置文件
# 用于监控 Elasticsearch + Milvus + RAG 系统

# 全局配置
global:
  scrape_interval: 15s    # 抓取间隔
  evaluation_interval: 15s # 评估间隔
  external_labels:
    cluster: 'elasticsearch-milvus'
    env: 'development'

# 告警配置
alerting:
  alertmanagers:
    - static_configs:
        - targets: []

# 规则配置
rule_files:
  - "rules/*.yml"

# 抓取配置
scrape_configs:
  # Prometheus 自身监控
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  # Elasticsearch 监控
  - job_name: 'elasticsearch'
    static_configs:
      - targets: ['localhost:9200']
    metrics_path: '/_prometheus/metrics'
    relabel_configs:
      - source_labels: [__address__]
        target_label: instance
        regex: '([^:]+):\d+'

  # 应用指标 (如果使用 Micrometer)
  - job_name: 'application'
    static_configs:
      - targets: ['localhost:8080']
    metrics_path: '/actuator/prometheus'
    relabel_configs:
      - source_labels: [__address__]
        target_label: app
        replacement: 'elasticsearch-app'

  # Node Exporter (主机监控)
  - job_name: 'node'
    static_configs:
      - targets: ['localhost:9100']
```

### 3.2 告警规则

**配置文件**: [elasticsearch-alerts.yml](deploy/prometheus/rules/elasticsearch-alerts.yml)

```yaml
# Elasticsearch 监控告警规则

groups:
  # Elasticsearch 告警规则
  - name: elasticsearch
    rules:
      # ES 集群健康告警
      - alert: ElasticsearchClusterRed
        expr: elasticsearch_cluster_health == 2
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Elasticsearch 集群状态为 Red"
          description: "Elasticsearch 集群健康状态为 Red，需要立即处理"

      - alert: ElasticsearchClusterYellow
        expr: elasticsearch_cluster_health == 1
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Elasticsearch 集群状态为 Yellow"
          description: "Elasticsearch 集群健康状态为 Yellow，需要关注"

      # ES 查询延迟告警
      - alert: ElasticsearchHighQueryLatency
        expr: histogram_quantile(0.95, rate(elasticsearch_query_latency_ms_bucket[5m])) > 1000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Elasticsearch 查询延迟过高"
          description: "P95 查询延迟超过 1 秒，当前值: {{ $value }}ms"

  # Milvus 告警规则
  - name: milvus
    rules:
      # Milvus 查询延迟告警
      - alert: MilvusHighQueryLatency
        expr: histogram_quantile(0.95, rate(milvus_query_latency_ms_bucket[5m])) > 500
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Milvus 查询延迟过高"
          description: "P95 查询延迟超过 500ms，当前值: {{ $value }}ms"

      # Milvus 向量数量告警
      - alert: MilvusVectorCountHigh
        expr: milvus_vector_count > 10000000
        for: 5m
        labels:
          severity: info
        annotations:
          summary: "Milvus 向量数量较高"
          description: "Milvus 集合中向量数量超过 1000 万，当前值: {{ $value }}"

  # RAG 告警规则
  - name: rag
    rules:
      # RAG Embedding 延迟告警
      - alert: RAGHighEmbeddingLatency
        expr: histogram_quantile(0.95, rate(rag_embedding_latency_ms_bucket[5m])) > 2000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "RAG Embedding 延迟过高"
          description: "P95 Embedding 生成延迟超过 2 秒"

      # RAG LLM 延迟告警
      - alert: RAGHighLLMLatency
        expr: histogram_quantile(0.95, rate(rag_llm_latency_ms_bucket[5m])) > 10000
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "RAG LLM 生成延迟过高"
          description: "P95 LLM 生成延迟超过 10 秒"

  # JVM 告警规则
  - name: jvm
    rules:
      # JVM 堆内存告警
      - alert: JVMHeapUsageHigh
        expr: jvm_heap_usage_percent > 85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "JVM 堆内存使用率过高"
          description: "JVM 堆内存使用率超过 85%，当前值: {{ $value }}%"

      - alert: JVMHeapUsageCritical
        expr: jvm_heap_usage_percent > 95
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "JVM 堆内存使用率危急"
          description: "JVM 堆内存使用率超过 95%，当前值: {{ $value }}%"

      # JVM GC 频繁告警
      - alert: JVMGCFrequent
        expr: rate(jvm_gc_count[5m]) > 10
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "JVM GC 过于频繁"
          description: "JVM GC 频率超过每分钟 10 次"
```

## 4. Kibana 仪表盘

**配置文件**: [dashboard.json](deploy/kibana/dashboard.json)

```json
{
  "version": "8.18.0",
  "objects": [
    {
      "id": "elasticsearch-overview",
      "type": "dashboard",
      "updated_at": "2026-01-18T00:00:00.000Z",
      "references": [],
      "attributes": {
        "title": "Elasticsearch Overview",
        "hits": 0,
        "description": "Elasticsearch 集群概览",
        "panelsJSON": "[{\"version\":\"8.18.0\",\"type\":\"visualization\",\"gridData\":{\"x\":0,\"y\":0,\"w\":24,\"h\":16,\"i\":\"1\"},\"panelRefName\":\"panel_1\"},{\"version\":\"8.18.0\",\"type\":\"visualization\",\"gridData\":{\"x\":24,\"y\":0,\"w\":24,\"h\":16,\"i\":\"2\"},\"panelRefName\":\"panel_2\"},{\"version\":\"8.18.0\",\"type\":\"visualization\",\"gridData\":{\"x\":0,\"y\":16,\"w\":48,\"h\":16,\"i\":\"3\"},\"panelRefName\":\"panel_3\"}]",
        "optionsJSON": "{\"useMargins\":true,\"syncColors\":false,\"hidePanelTitles\":false}",
        "timeRestore": true,
        "timeTo": "now",
        "timeFrom": "now-24h",
        "refreshInterval": {
          "pause": false,
          "value": 30000
        }
      }
    },
    {
      "id": "panel_1",
      "type": "visualization",
      "attributes": {
        "title": "Cluster Health",
        "visState": "{\"title\":\"Cluster Health\",\"type\":\"metric\",\"aggs\":[],\"params\":{\"addTooltip\":true,\"addLegend\":false,\"type\":\"metric\",\"metric\":{}}}"
      }
    },
    {
      "id": "panel_2",
      "type": "visualization",
      "attributes": {
        "title": "Query Rate",
        "visState": "{\"title\":\"Query Rate\",\"type\":\"line\",\"aggs\":[{\"id\":\"1\",\"enabled\":true,\"type\":\"count\",\"schema\":\"metric\",\"params\":{}}],\"params\":{\"type\":\"line\",\"grid\":{\"categoryLines\":false}}}"
      }
    },
    {
      "id": "search_1",
      "type": "search",
      "attributes": {
        "title": "Query Log Search",
        "columns": ["_source"],
        "sort": [["@timestamp", "desc"]],
        "kibanaSavedObjectMeta": {
          "searchSourceJSON": "{\"query\":{\"query\":\"\",\"language\":\"kuery\"},\"filter\":[],\"indexRefName\":\"search_1\"}"
        }
      }
    }
  ]
}
```

## 5. 监控最佳实践

### 5.1 关键指标监控

```
┌─────────────────────────────────────────────────────────────────────┐
│                      关键监控指标                                     │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  可用性指标                                                          │
│  ├── ES 集群健康状态 (green/yellow/red)                             │
│  ├── 服务可用性 (up/down)                                           │
│  └── 请求成功率 (4xx/5xx 比例)                                      │
│                                                                      │
│  性能指标                                                            │
│  ├── 查询延迟 (P50/P95/P99)                                         │
│  ├── 吞吐量 (QPS)                                                   │
│  └── 响应时间分布                                                    │
│                                                                      │
│  资源指标                                                            │
│  ├── CPU 使用率                                                      │
│  ├── 内存使用率                                                      │
│  ├── 磁盘使用率                                                      │
│  └── 网络带宽                                                        │
│                                                                      │
│  业务指标                                                            │
│  ├── 文档数量                                                        │
│  ├── 向量数量                                                        │
│  └── RAG 查询次数                                                    │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

### 5.2 告警策略

```yaml
# 告警级别定义
labels:
  severity: critical    # 危急 - 需要立即处理
  severity: warning     # 警告 - 需要关注
  severity: info        # 信息 - 供参考

# 告警触发条件
for: 1m   # 持续 1 分钟才触发 (避免瞬时抖动)
for: 5m   # 持续 5 分钟才触发 (更严格)

# 告警抑制
- alert: HighLatency
  expr: latency > 1000
  labels:
    severity: warning
  annotations:
    summary: "高延迟告警"

- alert: VeryHighLatency
  expr: latency > 5000
  labels:
    severity: critical
  annotations:
    summary: "严重高延迟告警"
  # 自动抑制 HighLatency
```

### 5.3 可视化面板设计

```
┌─────────────────────────────────────────────────────────────────────┐
│                      Grafana 面板布局建议                             │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────┬─────────────────────┐                     │
│  │    集群健康状态     │     查询 QPS        │                     │
│  │   (Single Stat)     │     (Graph)         │                     │
│  └─────────────────────┴─────────────────────┘                     │
│                                                                      │
│  ┌─────────────────────────────────────────────────────┐            │
│  │               查询延迟分布 (Graph)                    │            │
│  │         P50 / P95 / P99                             │            │
│  └─────────────────────────────────────────────────────┘            │
│                                                                      │
│  ┌─────────────────────────────────────────────────────┐            │
│  │              ES vs Milvus 延迟对比 (Graph)           │            │
│  └─────────────────────────────────────────────────────┘            │
│                                                                      │
│  ┌─────────────────────┬─────────────────────┐                     │
│  │    JVM 堆内存       │     文档/向量数量    │                     │
│  │    (Gauge)          │     (Stat)           │                     │
│  └─────────────────────┴─────────────────────┘                     │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 告警频繁触发 | 阈值设置过低 | 调整 for 时间和阈值 |
| 指标收集延迟 | scrape_interval 过长 | 缩短抓取间隔 |
| 存储空间不足 | 保留时间过长 | 调整保留策略 |
| 告警风暴 | 多指标同时告警 | 设置告警抑制规则 |

## 7. 扩展阅读

**代码位置**:
- [MetricNames.java](src/main/java/com/shuai/elasticsearch/monitoring/MetricNames.java) - 指标定义
- [PrometheusConfig.java](src/main/java/com/shuai/elasticsearch/monitoring/PrometheusConfig.java) - Prometheus 配置

- [Prometheus 官方文档](https://prometheus.io/docs/introduction/overview/)
- [PromQL 查询语言](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana 面板](https://grafana.com/docs/grafana/latest/panels/)
- [Alertmanager 配置](https://prometheus.io/docs/alerting/latest/configuration/)
- [Elasticsearch 监控](https://www.elastic.co/guide/en/elasticsearch/reference/current/monitor-elasticsearch.html)
- [上一章: LLM 集成](18-llm-integration.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜⬜ 0/19 文档完成

**最后更新**: 2026-01-18
