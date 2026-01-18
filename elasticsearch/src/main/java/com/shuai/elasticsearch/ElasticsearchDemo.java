package com.shuai.elasticsearch;

import com.shuai.elasticsearch.aggregation.AggregationDemo;
import com.shuai.elasticsearch.async.AsyncClientDemo;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.document.DocumentOperationDemo;
import com.shuai.elasticsearch.fulltext.FullTextSearchDemo;
import com.shuai.elasticsearch.geo.GeoQueryDemo;
import com.shuai.elasticsearch.index.IndexManagementDemo;
import com.shuai.elasticsearch.query.FunctionScoreDemo;
import com.shuai.elasticsearch.query.NestedQueryDemo;
import com.shuai.elasticsearch.query.QueryDemo;
import com.shuai.elasticsearch.rag.HybridSearchDemo;
import com.shuai.elasticsearch.rag.RAGDemo;
import com.shuai.elasticsearch.util.DataInitUtil;
import com.shuai.elasticsearch.vector.MilvusQueryDemo;

/**
 * Elasticsearch 搜索引擎示例类
 *
 * 模块概述
 * ----------
 * 本模块系统性地演示了 Elasticsearch 全文检索引擎的核心知识和实践，
 * 涵盖文档操作、查询类型、聚合分析、全文搜索、地理查询等。
 *
 * 包结构
 * ----------
 *   com.shuai.elasticsearch
 *   ├── config/           # ES 客户端配置
 *   ├── model/            # 数据模型
 *   ├── util/             # 工具类
 *   ├── overview/         # ES核心概念和架构
 *   ├── document/         # 文档CRUD操作
 *   ├── query/            # 查询类型实现
 *   ├── aggregation/      # 聚合分析功能
 *   ├── fulltext/         # 全文检索高级功能
 *   ├── geo/              # 地理位置查询
 *   ├── async/            # 异步客户端
 *   ├── index/            # 索引管理
 *   ├── vector/           # Milvus 向量检索
 *   └── rag/              # RAG 检索增强
 *
 * 核心内容
 * ----------
 *   - 核心概念: Index、Document、Mapping、Shard、Replica
 *   - 文档操作: 插入、更新、删除、批量操作
 *   - 查询类型: 全文检索、精确匹配、复合查询、范围查询
 *   - 聚合分析: 指标聚合、桶聚合、管道聚合
 *   - 全文搜索: 分词器、高亮显示、相关性排序
 *   - 地理查询: geo_point、geo_shape、距离/范围查询
 *   - 高级查询: nested、function_score
 *   - 异步操作: CompletableFuture 并发查询
 *   - 索引管理: 创建、删除、别名、更新设置
 *   - 向量检索: Milvus 向量数据库、相似度搜索
 *   - RAG: 检索增强生成、混合检索
 *
 * 运行说明
 * ----------
 * 1. 确保本地运行 Elasticsearch (默认 localhost:9200)
 * 2. 可选: 运行 Milvus (默认 localhost:19530)
 * 3. 运行: mvn -pl elasticsearch exec:java -Dexec.mainClass=com.shuai.elasticsearch.ElasticsearchDemo
 *
 * @author Shuai
 * @version 1.0
 */
public class ElasticsearchDemo {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("  Elasticsearch Java Client 演示程序");
        System.out.println("=".repeat(60));
        System.out.println("\n确保 Elasticsearch 服务已启动 (默认 localhost:9200)");
        System.out.println("开始执行演示...\n");

        try {
            // 初始化测试数据
            initTestData();

            // 执行各模块演示
            overviewDemo();
            documentOperation();
            queryDemo();
            nestedQueryDemo();
            functionScoreDemo();
            aggregationDemo();
            fullTextSearch();
            geoQuery();
            asyncClientDemo();
            indexManagementDemo();
            milvusQueryDemo();
            ragDemo();
            hybridSearchDemo();

            System.out.println("\n" + "=".repeat(60));
            System.out.println("  演示执行完成！");
            System.out.println("=".repeat(60));

            // 关闭客户端
            ElasticsearchConfig.close();
        } catch (Exception e) {
            System.err.println("\n执行过程中出现错误: " + e.getMessage());
            System.err.println("请确保 Elasticsearch 服务已启动");
            e.printStackTrace();
        }
    }

    /**
     * 初始化测试数据
     */
    private static void initTestData() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  初始化测试数据");
        System.out.println("=".repeat(50));

        try {
            DataInitUtil.initAllData(ElasticsearchConfig.getClient());
        } catch (Exception e) {
            System.out.println("  [跳过] 数据初始化需要 Elasticsearch 连接");
        }
    }

    /**
     * 概述演示
     */
    private static void overviewDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Elasticsearch 概述");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 核心概念、REST API、集群信息");

        try {
            var demo = new com.shuai.elasticsearch.overview.ElasticsearchOverviewDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 概述演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 文档操作演示
     */
    private static void documentOperation() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  文档操作演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 插入、更新、删除、批量操作");

        try {
            DocumentOperationDemo demo = new DocumentOperationDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 文档操作演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 查询演示
     */
    private static void queryDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  查询演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: match、term、range、bool 查询");

        try {
            QueryDemo demo = new QueryDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 查询演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 嵌套查询演示
     */
    private static void nestedQueryDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  嵌套查询演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: nested 类型、nested 查询、嵌套聚合");

        try {
            NestedQueryDemo demo = new NestedQueryDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 嵌套查询演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 评分函数演示
     */
    private static void functionScoreDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  评分函数演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: script_score、random_score、field_value_factor");

        try {
            FunctionScoreDemo demo = new FunctionScoreDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 评分函数演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 聚合分析演示
     */
    private static void aggregationDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  聚合分析演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: avg、sum、terms、histogram 聚合");

        try {
            AggregationDemo demo = new AggregationDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 聚合演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 全文检索演示
     */
    private static void fullTextSearch() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  全文检索演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 高亮、相关性评分、多字段匹配");

        try {
            FullTextSearchDemo demo = new FullTextSearchDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 全文检索演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 地理查询演示
     */
    private static void geoQuery() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  地理查询演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: geo_distance、geo_bounding_box、geo_grid");

        try {
            GeoQueryDemo demo = new GeoQueryDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 地理查询演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 异步客户端演示
     */
    private static void asyncClientDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  异步客户端演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: CompletableFuture、并行操作、超时控制");

        try {
            AsyncClientDemo demo = new AsyncClientDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 异步客户端演示需要 Elasticsearch 连接");
        }
    }

    /**
     * 索引管理演示
     */
    private static void indexManagementDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  索引管理演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 创建索引、获取信息、更新设置、别名操作");

        try {
            IndexManagementDemo demo = new IndexManagementDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 索引管理演示需要 Elasticsearch 连接");
        }
    }

    /**
     * Milvus 向量查询演示
     */
    private static void milvusQueryDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  Milvus 向量查询演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 向量插入、相似度搜索、批量操作、过滤搜索");

        try {
            MilvusQueryDemo demo = new MilvusQueryDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] Milvus 向量查询演示需要 Milvus 连接");
        }
    }

    /**
     * RAG 检索增强演示
     */
    private static void ragDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  RAG 检索增强演示");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 文档处理、向量化、上下文构建、提示词工程");

        try {
            RAGDemo demo = new RAGDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] RAG 演示需要连接");
        }
    }

    /**
     * 混合检索演示
     */
    private static void hybridSearchDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("  混合检索演示 (ES + Milvus)");
        System.out.println("=".repeat(50));
        System.out.println("  包含: 并行检索、结果融合、查询路由");

        try {
            HybridSearchDemo demo = new HybridSearchDemo();
            demo.runAllDemos();
        } catch (Exception e) {
            System.out.println("  [跳过] 混合检索演示需要连接");
        }
    }
}
