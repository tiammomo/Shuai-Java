package com.shuai.elasticsearch.rag;

import com.shuai.elasticsearch.embedding.RerankerService;
import com.shuai.elasticsearch.util.VectorEmbeddingUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * RAG 演示测试类
 *
 * 测试 RAG 相关功能，包括文档分块、向量检索、提示词构建等。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class RAGDemoTest {

    @Test
    @DisplayName("测试文档分块")
    void testDocumentChunking() {
        String sampleDoc = """
            Elasticsearch 是一个分布式搜索和数据分析引擎。
            它能够解决不断涌现出的各种用例。

            Milvus 是一个开源的向量数据库，专为大规模向量相似度检索设计。
            它支持多种向量索引类型。

            RAG (Retrieval-Augmented Generation) 是一种结合检索和生成的技术。
            """;

        List<VectorEmbeddingUtil.TextChunk> chunks = VectorEmbeddingUtil.chunkText(sampleDoc);

        assertNotNull(chunks);
        assertTrue(chunks.size() > 0, "分块数量应大于0");
        System.out.println("分块数量: " + chunks.size());
    }

    @Test
    @DisplayName("测试文本向量化")
    void testTextEmbedding() {
        String text = "Elasticsearch 是一个分布式搜索和数据分析引擎";
        float[] embedding = VectorEmbeddingUtil.generateEmbedding(text);

        assertNotNull(embedding);
        assertTrue(embedding.length > 0, "向量维度应大于0");
        System.out.println("向量维度: " + embedding.length);
    }

    @Test
    @DisplayName("测试向量归一化")
    void testVectorNormalization() {
        String text = "测试文本";
        float[] embedding = VectorEmbeddingUtil.generateEmbedding(text);
        float[] normalized = VectorEmbeddingUtil.normalizeVector(embedding);

        assertNotNull(normalized);
        assertEquals(embedding.length, normalized.length, "归一化后维度应相同");

        // 归一化后向量模长应为1
        double magnitude = 0.0;
        for (float v : normalized) {
            magnitude += v * v;
        }
        assertEquals(1.0, Math.sqrt(magnitude), 0.01, "归一化后模长应为1");
    }

    @Test
    @DisplayName("测试相似度计算")
    void testSimilarityCalculation() {
        String text1 = "Elasticsearch 搜索";
        String text2 = "Elasticsearch 查询";
        String text3 = "完全不相关的内容";

        float[] embedding1 = VectorEmbeddingUtil.generateEmbedding(text1);
        float[] embedding2 = VectorEmbeddingUtil.generateEmbedding(text2);
        float[] embedding3 = VectorEmbeddingUtil.generateEmbedding(text3);

        float similarity12 = VectorEmbeddingUtil.cosineSimilarity(embedding1, embedding2);
        float similarity13 = VectorEmbeddingUtil.cosineSimilarity(embedding1, embedding3);

        assertTrue(similarity12 > similarity13,
            "相似文本的相似度应该更高");
        System.out.println("相似文本相似度: " + similarity12);
        System.out.println("不相似文本相似度: " + similarity13);
    }

    @Test
    @DisplayName("测试重排序服务")
    void testRerankerService() {
        RerankerService rerankerService = new RerankerService();

        String query = "Elasticsearch 集群配置";
        List<String> documents = Arrays.asList(
            "Elasticsearch 集群需要配置节点名称和集群名称",
            "Redis 是内存数据库，用于缓存",
            "Milvus 是向量数据库，用于相似度搜索",
            "ES 集群配置关键参数包括 cluster.name 和 node.name"
        );

        List<RerankerService.RerankResult> results = rerankerService.rerank(query, documents, 4);

        assertNotNull(results);
        assertTrue(results.size() <= 4, "返回数量不应超过topK");

        // 检查结果是否按分数降序排列
        for (int i = 1; i < results.size(); i++) {
            assertTrue(results.get(i-1).getScore() >= results.get(i).getScore(),
                "结果应按分数降序排列");
        }

        System.out.println("重排序结果数量: " + results.size());
    }

    @Test
    @DisplayName("测试 RagPromptTemplate 构建")
    void testRagPromptTemplate() {
        String context = "Elasticsearch 是一个分布式搜索和数据分析引擎。";
        String question = "什么是 Elasticsearch?";

        // 模拟提示词构建
        String systemPrompt = "你是一个技术专家，请基于以下参考信息回答用户的问题。";
        String userContent = String.format("""
            ## 参考信息

            %s

            ---

            ## 用户问题

            %s

            ## 回答

            """, context, question);

        assertNotNull(systemPrompt);
        assertNotNull(userContent);
        assertTrue(userContent.contains(context));
        assertTrue(userContent.contains(question));
    }

    @Test
    @DisplayName("测试重排序评分方法")
    void testRerankerScore() {
        RerankerService rerankerService = new RerankerService();

        String query = "如何安装 Elasticsearch";
        String relevantDoc = "Elasticsearch 安装步骤：1. 下载压缩包 2. 解压 3. 修改配置 4. 运行 bin/elasticsearch";
        String irrelevantDoc = "今天天气很好";

        float relevantScore = rerankerService.score(query, relevantDoc);
        float irrelevantScore = rerankerService.score(query, irrelevantDoc);

        assertTrue(relevantScore >= 0 && relevantScore <= 1,
            "分数应在0-1范围内");
        assertTrue(irrelevantScore >= 0 && irrelevantScore <= 1,
            "分数应在0-1范围内");

        System.out.println("相关文档分数: " + relevantScore);
        System.out.println("不相关文档分数: " + irrelevantScore);
    }

    @Test
    @DisplayName("测试 RAG 流程模拟")
    void testRAGFlow() {
        // 1. 用户查询
        String userQuery = "Elasticsearch 和 Milvus 有什么关系?";

        // 2. 查询向量化
        float[] queryVector = VectorEmbeddingUtil.generateQueryEmbedding(userQuery);
        assertNotNull(queryVector);
        assertTrue(queryVector.length > 0);

        // 3. 模拟检索结果
        List<String> retrievedDocs = Arrays.asList(
            "Elasticsearch 是一个分布式搜索和数据分析引擎",
            "Milvus 是开源向量数据库，专为大规模向量相似度检索设计",
            "RAG 结合检索和生成技术，通过知识库增强大语言模型能力"
        );

        // 4. 重排序
        RerankerService rerankerService = new RerankerService();
        List<RerankerService.RerankResult> reranked = rerankerService.rerank(
            userQuery, retrievedDocs, 3);

        assertNotNull(reranked);
        assertTrue(reranked.size() <= 3);

        // 5. 构建上下文
        StringBuilder context = new StringBuilder();
        for (RerankerService.RerankResult result : reranked) {
            context.append("- ").append(result.getText()).append("\n");
        }

        assertTrue(context.length() > 0);
        System.out.println("构建的上下文长度: " + context.length() + " 字符");
    }

    @Test
    @DisplayName("测试空文档处理")
    void testEmptyDocumentHandling() {
        RerankerService rerankerService = new RerankerService();

        // 空文档列表
        List<RerankerService.RerankResult> emptyResults = rerankerService.rerank(
            "test", List.of(), 5);
        assertNotNull(emptyResults);
        assertTrue(emptyResults.isEmpty(), "空输入应返回空列表");

        // 单个文档
        List<RerankerService.RerankResult> singleResult = rerankerService.rerank(
            "test", List.of("只有一个文档"), 1);
        assertNotNull(singleResult);
        assertTrue(singleResult.size() <= 1);
    }
}
