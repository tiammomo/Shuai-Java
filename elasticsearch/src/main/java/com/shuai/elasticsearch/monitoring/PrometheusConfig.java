package com.shuai.elasticsearch.monitoring;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Prometheus 监控配置类
 *
 * 模块概述
 * ----------
 * 本模块提供与 Prometheus 集成的监控功能定义。
 * 包含指标名称常量和服务健康状态管理。
 *
 * 核心内容
 * ----------
 *   - 指标名称: MetricNames 常量类
 *   - 健康检查: 服务健康状态管理
 *   - 监控配置: Prometheus 抓取配置
 *
 * 使用示例
 * ----------
 * {@code
 * // 使用指标名称常量
 * String queryRate = MetricNames.ES_QUERY_RATE;
 *
 * // 检查服务健康
 * boolean healthy = checkEsHealth();
 * }
 *
 * 部署配置
 * ----------
 * Prometheus 配置参考: deploy/prometheus/prometheus.yml
 * 告警规则参考: deploy/prometheus/rules/elasticsearch-alerts.yml
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 * @see <a href="https://prometheus.io/docs/concepts/data_model/">Prometheus Data Model</a>
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
     * 默认构造函数
     */
    public PrometheusConfig() {
    }

    /**
     * 记录 ES 查询
     *
     * @param latencyMs 延迟 (毫秒)
     */
    public void recordEsQuery(long latencyMs) {
        esQueryCount.incrementAndGet();
        System.out.println("[Metric] ES query latency: " + latencyMs + "ms");
    }

    /**
     * 记录 Milvus 查询
     *
     * @param latencyMs 延迟 (毫秒)
     */
    public void recordMilvusQuery(long latencyMs) {
        milvusQueryCount.incrementAndGet();
        System.out.println("[Metric] Milvus query latency: " + latencyMs + "ms");
    }

    /**
     * 记录 Milvus 插入
     *
     * @param count 插入数量
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
     * 获取 Milvus 查询计数
     */
    public long getMilvusQueryCount() {
        return milvusQueryCount.get();
    }

    /**
     * 获取 Milvus 插入计数
     */
    public long getMilvusInsertCount() {
        return milvusInsertCount.get();
    }

    /**
     * 获取 RAG 查询计数
     */
    public long getRagQueryCount() {
        return ragQueryCount.get();
    }

    /**
     * 设置 ES 健康状态
     */
    public void setEsHealthy(boolean healthy) {
        this.esHealthy = healthy;
    }

    /**
     * 设置 Milvus 健康状态
     */
    public void setMilvusHealthy(boolean healthy) {
        this.milvusHealthy = healthy;
    }

    /**
     * 获取 ES 健康状态
     */
    public boolean isEsHealthy() {
        return esHealthy;
    }

    /**
     * 获取 Milvus 健康状态
     */
    public boolean isMilvusHealthy() {
        return milvusHealthy;
    }

    /**
     * 更新 ES 文档数量
     */
    public void updateEsDocumentCount(long count) {
        esDocumentCount.set(count);
    }

    /**
     * 获取 ES 文档数量
     */
    public long getEsDocumentCount() {
        return esDocumentCount.get();
    }

    /**
     * 更新 Milvus 向量数量
     */
    public void updateMilvusVectorCount(long count) {
        milvusVectorCount.set(count);
    }

    /**
     * 获取 Milvus 向量数量
     */
    public long getMilvusVectorCount() {
        return milvusVectorCount.get();
    }

    /**
     * 获取监控配置摘要
     */
    public String getSummary() {
        return String.format("PrometheusConfig{EShealthy=%s, MilvusHealthy=%s, ESqueries=%d, Milvusqueries=%d}",
            esHealthy, milvusHealthy, esQueryCount.get(), milvusQueryCount.get());
    }
}
