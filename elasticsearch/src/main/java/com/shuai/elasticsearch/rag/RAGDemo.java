package com.shuai.elasticsearch.rag;

import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.data.BlogDataGenerator;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.util.VectorEmbeddingUtil;
import com.shuai.elasticsearch.util.ResponsePrinter;
import co.elastic.clients.elasticsearch.ElasticsearchClient;

import java.io.IOException;
import java.util.List;

/**
 * RAG 检索增强演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 RAG (Retrieval-Augmented Generation) 检索增强技术。
 *
 * RAG 流程
 * ----------
 *   1. 文档处理: 文档分块、向量化
 *   2. 向量存储: 存入 Milvus 向量数据库
 *   3. 用户查询: 查询向量化
 *   4. 向量搜索: 在 Milvus 中检索相似文档
 *   5. 上下文构建: 拼接检索结果
 *   6. LLM 生成: 将上下文提供给大模型生成回答
 *
 * 核心内容
 * ----------
 *   - 文档分块 (Chunking)
 *   - 向量化 (Embedding)
 *   - 向量存储与检索
 *   - 上下文融合
 *   - 提示词工程
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://arxiv.org/abs/2005.11401">RAG 论文</a>
 */
public class RAGDemo {

    private final ElasticsearchClient esClient;
    private static final int TOP_K = 5;
    private static final int MAX_CONTEXT_LENGTH = 4000;

    public RAGDemo() {
        this.esClient = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有 RAG 演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("RAG 检索增强演示", "Retrieval-Augmented Generation 技术");

        ragOverview();
        documentProcessing();
        vectorRetrieval();
        contextConstruction();
        ragPromptEngineering();
    }

    /**
     * RAG 概述
     *
     * 什么是 RAG?
     * - RAG (Retrieval-Augmented Generation) 结合检索和生成
     * - 检索: 从知识库中找到相关文档
     * - 生成: 基于检索结果生成回答
     *
     * RAG 优势:
     * - 解决知识截止日期问题
     * - 减少模型幻觉 (Hallucination)
     * - 支持私有知识库
     * - 可解释性强 (可追溯来源)
     *
     * RAG 工作流程:
     * 用户查询 -> 向量化 -> Milvus 检索 -> 上下文构建 -> LLM 生成
     *
     * 技术栈组成:
     * - 文档处理: PDF/Word/Markdown 解析
     * - 向量存储: Milvus 向量数据库
     * - 关键词存储: Elasticsearch
     * - LLM 接口: OpenAI/Claude/通义千问 API
     */
    private void ragOverview() {
        ResponsePrinter.printMethodInfo("ragOverview", "RAG 技术概述");
        // 详见上方 Javadoc
    }

    /**
     * 文档处理与分块
     *
     * 文档分块策略:
     * - 固定长度分块: 实现简单，可能切断句子
     * - 语义分块: 按段落/句子边界分块，保持语义完整性
     * - 滑动窗口分块: 块之间有重叠，避免边界信息丢失
     */
    private void documentProcessing() {
        ResponsePrinter.printMethodInfo("documentProcessing", "文档处理与分块");

        // 分块策略详见上方 Javadoc

        String sampleDoc = """
            Elasticsearch 是一个分布式、RESTful 风格的搜索和数据分析引擎。
            它能够解决不断涌现出的各种用例。作为 Elastic Stack 的核心，
            Elasticsearch 集中存储您的数据，帮助您发现意料之中以及意料之外的情况。

            Milvus 是一个开源的向量数据库，专为大规模向量相似度检索设计。
            它支持多种向量索引类型，广泛应用于推荐系统、问答系统、RAG 等场景。

            RAG (Retrieval-Augmented Generation) 是一种结合检索和生成的技术。
            它通过从知识库中检索相关文档，来增强大语言模型的生成能力。
            这种方法可以有效解决大模型的幻觉问题和知识更新问题。
            """;

        List<VectorEmbeddingUtil.TextChunk> chunks = VectorEmbeddingUtil.chunkText(sampleDoc);

        System.out.println("    原始文档长度: " + sampleDoc.length() + " 字符");
        System.out.println("    分块数量: " + chunks.size());

        for (int i = 0; i < Math.min(3, chunks.size()); i++) {
            VectorEmbeddingUtil.TextChunk chunk = chunks.get(i);
            System.out.println("    [" + (i + 1) + "] " + chunk.getContent().substring(0, 30) + "...");
        }

        System.out.println("\n  2. 分块策略:");

        System.out.println("    固定长度分块:");
        System.out.println("      - 优点: 实现简单");
        System.out.println("      - 缺点: 可能切断句子");

        System.out.println("\n    语义分块:");
        System.out.println("      - 按段落/句子边界分块");
        System.out.println("      - 保持语义完整性");

        System.out.println("\n    滑动窗口分块:");
        System.out.println("      - 块之间有重叠");
        System.out.println("      - 避免边界信息丢失");

        System.out.println("\n  3. 向量化:");

        if (!chunks.isEmpty()) {
            float[] embedding = VectorEmbeddingUtil.generateEmbedding(chunks.get(0).getContent());
            System.out.println("    向量维度: " + embedding.length);
            System.out.println("    归一化: " + VectorEmbeddingUtil.normalizeVector(embedding).length);
        }
    }

    /**
     * 向量检索 - 使用真实数据
     */
    private void vectorRetrieval() {
        ResponsePrinter.printMethodInfo("vectorRetrieval", "向量检索");

        System.out.println("\n  1. 查询向量化:");

        String userQuery = "Elasticsearch 和 Milvus 有什么关系?";
        float[] queryVector = VectorEmbeddingUtil.generateQueryEmbedding(userQuery);

        System.out.println("    用户查询: " + userQuery);
        System.out.println("    向量维度: " + queryVector.length);

        System.out.println("\n  2. 向量相似度搜索 - 使用真实数据:");

        // 使用 BlogDataGenerator 获取真实文档
        List<BlogDocument> documents = BlogDataGenerator.getAIDocuments();
        System.out.println("    数据来源: BlogDataGenerator");
        System.out.println("    文档数量: " + documents.size());

        // 模拟检索结果 (基于真实文档)
        System.out.println("    Top-" + TOP_K + " 检索结果:");
        for (int i = 0; i < Math.min(TOP_K, documents.size()); i++) {
            BlogDocument doc = documents.get(i);
            float score = 0.95f - (i * 0.05f); // 模拟相似度分数
            System.out.println("    [" + (i + 1) + "] " + doc.getTitle() + " (相似度: " +
                String.format("%.4f", score) + ")");
        }

        System.out.println("\n  3. 检索优化策略:");

        System.out.println("    重排序 (Reranking):");
        System.out.println("      - 粗排: 向量检索快速筛选");
        System.out.println("      - 精排: 交叉编码器重排序");

        System.out.println("\n    查询改写:");
        System.out.println("      - 同义词扩展");
        System.out.println("      - HyDE (假设文档嵌入)");

        System.out.println("\n    混合检索:");
        System.out.println("      - 向量检索 + 关键词检索");
        System.out.println("      - 结果融合");
    }

    /**
     * 上下文构建 - 使用真实数据
     */
    private void contextConstruction() {
        ResponsePrinter.printMethodInfo("contextConstruction", "上下文构建");

        System.out.println("\n  1. 检索结果合并 - 使用真实数据:");

        // 使用真实文档数据
        List<BlogDocument> documents = BlogDataGenerator.getAIDocuments();

        List<String> retrievedContents = new java.util.ArrayList<>();
        for (BlogDocument doc : documents) {
            retrievedContents.add(doc.getContent());
        }

        String context = String.join("\n\n", retrievedContents);
        System.out.println("    数据来源: BlogDataGenerator");
        System.out.println("    文档数量: " + documents.size());
        System.out.println("    合并后长度: " + context.length() + " 字符");

        System.out.println("\n  2. 上下文截断:");

        if (context.length() > MAX_CONTEXT_LENGTH) {
            context = context.substring(0, MAX_CONTEXT_LENGTH);
            System.out.println("    已截断到: " + context.length() + " 字符");
        }

        System.out.println("\n  3. 元信息附加 - 真实文档来源:");

        System.out.println("    文档来源列表:");
        for (int i = 0; i < Math.min(3, documents.size()); i++) {
            BlogDocument doc = documents.get(i);
            System.out.println("    - 标题: " + doc.getTitle());
            System.out.println("      作者: " + doc.getAuthor());
            System.out.println("      标签: " + String.join(", ", doc.getTags()));
        }

        System.out.println("\n  4. 提示词模板:");

        System.out.println("    基于以下上下文回答问题。如果上下文中没有相关信息，请说不知道。");
        System.out.println("    上下文: [来自真实文档的内容]");
        System.out.println("    问题: [用户问题]");
    }

    /**
     * RAG 提示词工程
     */
    private void ragPromptEngineering() {
        ResponsePrinter.printMethodInfo("ragPromptEngineering", "RAG 提示词工程");

        System.out.println("\n  1. 基础提示词模板:");

        String basePrompt = """
            你是一个知识问答助手。请根据以下上下文回答用户的问题。

            上下文:
            {{context}}

            问题:
            {{question}}

            要求:
            1. 仅基于上下文回答，不要添加外部知识
            2. 如果上下文没有相关信息，请说"根据提供的上下文，我无法回答这个问题"
            3. 回答要简洁明了
            """;

        System.out.println("    " + basePrompt.substring(0, 100).replace("\n", "\\n") + "...");

        System.out.println("\n  2. 增强提示词策略:");

        System.out.println("    明确指令:");
        System.out.println("      - '仅使用提供的上下文'");
        System.out.println("      - '不要编造信息'");

        System.out.println("\n    格式要求:");
        System.out.println("      - '用中文回答'");
        System.out.println("      - '列出引用来源'");

        System.out.println("\n    思维链 (Chain of Thought):");
        System.out.println("      - '先分析问题'");
        System.out.println("      - '再从上下文找答案'");
        System.out.println("      - '最后组织回答'");

        System.out.println("\n  3. 完整 RAG 流程演示:");

        System.out.println("    Step 1: 用户输入");
        System.out.println("      问题: Elasticsearch 和 Milvus 有什么区别?");

        System.out.println("\n    Step 2: 向量检索");
        System.out.println("      查询向量: 384维");
        System.out.println("      检索结果: 3个相关文档");

        System.out.println("\n    Step 3: 上下文构建");
        System.out.println("      合并文档内容，附加来源信息");

        System.out.println("\n    Step 4: LLM 生成");
        System.out.println("      将上下文 + 问题发送给 LLM");

        System.out.println("\n    Step 5: 输出回答");
        System.out.println("      基于上下文生成回答，附带来源引用");
    }

    /**
     * Search result inner class
     */
    private static class SearchResult {
        String documentId;
        String content;
        float score;

        SearchResult(String documentId, String content, float score) {
            this.documentId = documentId;
            this.content = content;
            this.score = score;
        }
    }
}
