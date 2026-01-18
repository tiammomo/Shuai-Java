package com.shuai.elasticsearch.overview;

/**
 * Elasticsearch 概述演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 全文检索引擎的核心概念和架构。
 *
 * 核心内容
 * ----------
 *   - 核心概念: Index、Document、Mapping、Shard、Replica
 *   - 集群架构: Node、Cluster、Shard、Replica
 *   - 数据结构: 倒排索引、Document Values
 *   - REST API: 常用操作接口
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html">Elasticsearch Reference</a>
 */
public class ElasticsearchOverviewDemo {

    public void runAllDemos() {
        /*
         * ==================================================
         *              Elasticsearch 概述
         * ==================================================
         */

        esCoreConcepts();
        clusterArchitecture();
        dataStructure();
        restApiDemo();
    }

    /**
     * 核心概念
     */
    private void esCoreConcepts() {
        /*
         * --- Elasticsearch 核心概念 ---
         *   - Index (索引) - 相当于数据库
         *   - Document (文档) - 相当于数据行
         *   - Field (字段) - 相当于数据列
         *   - Mapping (映射) - 相当于表结构
         *   - Type (类型) - 7.x 已移除
         *
         * 【数据结构对照】
         *   Elasticsearch    |    传统数据库
         *   -----------------|----------------
         *   Index            |    Database
         *   Document         |    Row
         *   Field            |    Column
         *   Mapping          |    Schema
         *   _id              |    Primary Key
         */
    }

    /**
     * 集群架构
     */
    private void clusterArchitecture() {
        /*
         * --- 集群架构 ---
         *   - Node (节点) - ES 实例进程
         *   - Cluster (集群) - 多个节点组成
         *   - Shard (分片) - 数据分片，分布在不同节点
         *   - Replica (副本) - 分片副本，保证高可用
         *
         * 【分片与副本】
         *   Cluster: [Node1, Node2, Node3]
         *   Index: blog (3 Primary Shards, 1 Replica)
         *   ├── P0 → R0 (replica)
         *   ├── P1 → R1 (replica)
         *   └── P2 → R2 (replica)
         */
    }

    /**
     * 数据结构
     */
    private void dataStructure() {
        /*
         * --- 数据结构 ---
         *   - Inverted Index (倒排索引) - 全文检索基础
         *   - Document Values - 列式存储，聚合/排序
         *   - Fielddata - 字段数据，字段聚合
         *   - Doc Values - 文档值，排序和聚合
         *
         * 【倒排索引原理】
         *   文档: ["Java 教程", "Python 教程"]
         *   倒排索引:
         *   ──────────────┬────────────────────────
         *   Term          │  Doc IDs
         *   ──────────────┼────────────────────────
         *   Java          │  [1]
         *   教程          │  [1, 2]
         *   Python        │  [2]
         */
    }

    /**
     * REST API 演示
     */
    private void restApiDemo() {
        /*
         * --- 常用 REST API ---
         *
         * 索引操作:
         *   PUT /index_name              # 创建索引
         *   GET /index_name              # 查询索引
         *   DELETE /index_name           # 删除索引
         *
         * 文档操作:
         *   POST /index/_doc/{id}        # 插入文档
         *   GET /index/_doc/{id}         # 查询文档
         *   PUT /index/_doc/{id}         # 更新文档
         *   DELETE /index/_doc/{id}      # 删除文档
         *
         * 搜索操作:
         *   GET /index/_search           # 搜索文档
         *   GET /index/_doc/_search      # 同上
         *
         * 聚合操作:
         *   GET /index/_search           # 带聚合查询
         */
    }
}
