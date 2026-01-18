package com.shuai.elasticsearch.monitoring;

/**
 * 指标名称常量类
 *
 * 定义所有可用的监控指标名称。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
public final class MetricNames {

    private MetricNames() {}

    // ============ Elasticsearch 指标 ============

    /**
     * ES 集群健康状态
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

    /**
     * ES 索引操作计数
     */
    public static final String ES_INDEX_OPERATIONS = "elasticsearch_index_operations";

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
}
