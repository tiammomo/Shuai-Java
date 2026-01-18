package com.shuai.elasticsearch.data;

import com.shuai.elasticsearch.model.BlogDocument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 博客文档数据生成器
 *
 * 模块概述
 * ----------
 * 本模块提供真实的博客文档数据生成功能，用于 ES 索引初始化和测试。
 *
 * 核心内容
 * ----------
 *   - 技术博客文档 (Elasticsearch, Milvus, AI/ML 等)
 *   - 生活方式文档 (旅行、美食)
 *   - 大数据相关文档
 *
 * 数据规模
 * ----------
 *   - 预定义高质量文档: 13 篇
 *   - 可生成随机文档: 自定义数量
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
public class BlogDataGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * 生成 Elasticsearch 相关文档
     */
    public static BlogDocument createElasticsearchIntro() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Elasticsearch 8.x 新特性详解")
                .content("Elasticsearch 8.0 引入了多项重要新功能，包括：\n\n" +
                        "1. 安全增强\n" +
                        "   - 默认启用安全功能\n" +
                        "   - 简化的身份验证配置\n\n" +
                        "2. 性能优化\n" +
                        "   - 更快的数据索引速度\n" +
                        "   - 减少内存占用\n\n" +
                        "3. 新查询功能\n" +
                        "   - 改进的向量搜索\n" +
                        "   - 增强的聚合能力\n\n" +
                        "这些改进使得 Elasticsearch 在处理大规模数据时更加高效。")
                .author("技术君")
                .tags(Arrays.asList("Elasticsearch", "搜索", "ELK", "分布式"))
                .views(2500)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(5).format(FORMATTER))
                .build();
    }

    public static BlogDocument createElasticsearchSearch() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("使用 Elasticsearch 实现全文检索")
                .content("全文检索是 Elasticsearch 的核心功能。本文介绍如何：\n\n" +
                        "1. 创建索引并配置映射\n" +
                        "2. 导入数据\n" +
                        "3. 使用 match 查询进行搜索\n" +
                        "4. 配置 IK 分词器支持中文\n" +
                        "5. 优化搜索相关性评分")
                .author("搜索专家")
                .tags(Arrays.asList("Elasticsearch", "全文检索", "IK分词器"))
                .views(3200)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(10).format(FORMATTER))
                .build();
    }

    public static BlogDocument createElasticsearchQuery() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Elasticsearch 查询类型详解")
                .content("Elasticsearch 提供多种查询类型：\n\n" +
                        "1. 全文检索查询\n" +
                        "   - match: 标准分词匹配\n" +
                        "   - match_phrase: 短语匹配\n" +
                        "   - multi_match: 多字段匹配\n\n" +
                        "2. 精确匹配查询\n" +
                        "   - term: 精确词条匹配\n" +
                        "   - terms: 多值精确匹配\n" +
                        "   - range: 范围查询\n\n" +
                        "3. 复合查询\n" +
                        "   - bool: 布尔组合查询\n" +
                        "   - function_score: 自定义评分")
                .author("数据工程师")
                .tags(Arrays.asList("Elasticsearch", "查询", "DSL"))
                .views(1800)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(15).format(FORMATTER))
                .build();
    }

    /**
     * 生成 Milvus/向量数据库相关文档
     */
    public static BlogDocument createMilvusIntro() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Milvus 向量数据库入门指南")
                .content("Milvus 是一个开源的向量数据库，专为大规模向量相似度检索设计。\n\n" +
                        "核心特性：\n" +
                        "- 支持多种向量索引类型（IVF, HNSW, ANNOY）\n" +
                        "- 高性能向量搜索\n" +
                        "- 易于扩展\n" +
                        "- 丰富的 SDK 支持\n\n" +
                        "应用场景：\n" +
                        "- 推荐系统\n" +
                        "- 问答系统\n" +
                        "- 图像/视频搜索")
                .author("向量探索者")
                .tags(Arrays.asList("Milvus", "向量数据库", "AI", "向量检索"))
                .views(4100)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(3).format(FORMATTER))
                .build();
    }

    public static BlogDocument createVectorSearch() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("向量检索技术与应用实践")
                .content("向量检索是将文本、图像等转换为向量后进行相似度搜索的技术。\n\n" +
                        "主要流程：\n" +
                        "1. 向量化：使用 Embedding 模型将文本转为向量\n" +
                        "2. 索引构建：使用 IVFFlat、HNSW 等索引加速搜索\n" +
                        "3. 相似度计算：余弦相似度、欧氏距离等\n\n" +
                        "技术选型：\n" +
                        "- Milvus: 开源向量数据库\n" +
                        "- Elasticsearch: 支持 kNN 插件\n" +
                        "- Pinecone: 云服务")
                .author("AI研究员")
                .tags(Arrays.asList("向量检索", "Embedding", "AI"))
                .views(3500)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(7).format(FORMATTER))
                .build();
    }

    /**
     * 生成 RAG 相关文档
     */
    public static BlogDocument createRAGIntro() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("RAG 检索增强生成技术解析")
                .content("RAG (Retrieval-Augmented Generation) 结合了检索和生成两种技术。\n\n" +
                        "工作流程：\n" +
                        "1. 用户输入查询\n" +
                        "2. 将查询转换为向量\n" +
                        "3. 在向量数据库中检索相似文档\n" +
                        "4. 将检索结果作为上下文\n" +
                        "5. 调用 LLM 生成回答\n\n" +
                        "优势：\n" +
                        "- 减少模型幻觉\n" +
                        "- 支持私有知识库\n" +
                        "- 知识可更新")
                .author("AI研究员")
                .tags(Arrays.asList("RAG", "LLM", "大语言模型", "检索增强"))
                .views(5200)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(1).format(FORMATTER))
                .build();
    }

    public static BlogDocument createRAGPipeline() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("构建企业级 RAG 系统实战")
                .content("本文详细介绍如何构建生产级别的 RAG 系统：\n\n" +
                        "1. 文档处理\n" +
                        "   - 文档解析（PDF/Word/Markdown）\n" +
                        "   - 文档分块策略\n" +
                        "   - 元数据提取\n\n" +
                        "2. 向量化\n" +
                        "   - Embedding 模型选择\n" +
                        "   - 批次处理优化\n" +
                        "   - 向量归一化\n\n" +
                        "3. 检索优化\n" +
                        "   - 两阶段检索（召回+精排）\n" +
                        "   - 查询改写（HyDE、同义词）\n" +
                        "   - 混合检索（ES + Milvus）\n\n" +
                        "4. 生成优化\n" +
                        "   - 提示词工程\n" +
                        "   - 上下文压缩\n" +
                        "   - 引用来源标注")
                .author("架构师")
                .tags(Arrays.asList("RAG", "系统架构", "AI", "向量数据库"))
                .views(4800)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(2).format(FORMATTER))
                .build();
    }

    /**
     * 生成 LLM/AI 相关文档
     */
    public static BlogDocument createLLMPrompt() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("大语言模型 Prompt 工程最佳实践")
                .content("Prompt 工程是优化 LLM 输出的关键技术：\n\n" +
                        "1. 明确指令\n" +
                        "   - 使用清晰的动词\n" +
                        "   - 指定输出格式\n\n" +
                        "2. 提供上下文\n" +
                        "   - 背景信息\n" +
                        "   - 示例说明\n\n" +
                        "3. 思维链提示\n" +
                        "   - 分步骤推理\n" +
                        "   - 减少错误\n\n" +
                        "4. 迭代优化\n" +
                        "   - 根据输出调整\n" +
                        "   - 测试不同策略")
                .author("AI产品经理")
                .tags(Arrays.asList("LLM", "Prompt", "AI", "大语言模型"))
                .views(6800)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(2).format(FORMATTER))
                .build();
    }

    public static BlogDocument createTransformer() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Transformer 架构详解")
                .content("Transformer 是现代 NLP 的基石架构：\n\n" +
                        "1. 注意力机制\n" +
                        "   - Self-Attention\n" +
                        "   - Multi-Head Attention\n\n" +
                        "2. 编码器-解码器\n" +
                        "   - Encoder 处理输入\n" +
                        "   - Decoder 生成输出\n\n" +
                        "3. 位置编码\n" +
                        "   - Sinusoidal 编码\n" +
                        "   - 学习的位置嵌入\n\n" +
                        "4. 应用\n" +
                        "   - BERT, GPT\n" +
                        "   - 机器翻译")
                .author("深度学习研究员")
                .tags(Arrays.asList("Transformer", "深度学习", "NLP", "注意力机制"))
                .views(7300)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(4).format(FORMATTER))
                .build();
    }

    /**
     * 生成大数据相关文档
     */
    public static BlogDocument createKafka() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Apache Kafka 大规模消息队列实践")
                .content("Kafka 是一个分布式流处理平台，广泛应用于：\n\n" +
                        "1. 消息队列\n" +
                        "   - 高吞吐量\n" +
                        "   - 持久化存储\n" +
                        "   - 消息持久化\n\n" +
                        "2. 日志收集\n" +
                        "   - 统一日志管道\n" +
                        "   - 实时处理\n\n" +
                        "3. 流处理\n" +
                        "   - Kafka Streams\n" +
                        "   - 与 Flink 集成")
                .author("大数据工程师")
                .tags(Arrays.asList("Kafka", "消息队列", "流处理", "大数据"))
                .views(2800)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(7).format(FORMATTER))
                .build();
    }

    public static BlogDocument createFlink() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("Apache Flink 实时计算实战")
                .content("Flink 是流批一体的分布式计算引擎：\n\n" +
                        "核心特性：\n" +
                        "- 精确一次语义\n" +
                        "- 低延迟高吞吐\n" +
                        "- 强一致的检查点\n\n" +
                        "应用场景：\n" +
                        "- 实时数仓\n" +
                        "- 欺诈检测\n" +
                        "- 实时报表")
                .author("流计算专家")
                .tags(Arrays.asList("Flink", "实时计算", "流处理", "大数据"))
                .views(3200)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(6).format(FORMATTER))
                .build();
    }

    /**
     * 生成生活方式类文档
     */
    public static BlogDocument createTravel() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("日本京都七日游完整攻略")
                .content("京都旅游攻略分享：\n\n" +
                        "第一天：抵达京都，入住酒店，晚上逛祇园\n" +
                        "第二天：清水寺 -二年坂三年坂-八坂神社\n" +
                        "第三天：金阁寺 - 银阁寺 - 哲学之道\n" +
                        "第四天：伏见稻荷大社 - 稻荷山徒步\n" +
                        "第五天：岚山 - 竹林 - 天龙寺\n" +
                        "第六天：京都御所 - 二条城\n" +
                        "第七天：购物和返程")
                .author("旅行达人")
                .tags(Arrays.asList("旅行", "日本", "京都", "旅游攻略"))
                .views(8900)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(15).format(FORMATTER))
                .build();
    }

    public static BlogDocument createCoffee() {
        return BlogDocument.builder()
                .id(UUID.randomUUID().toString())
                .title("在家制作精品咖啡完全指南")
                .content("手冲咖啡制作技巧：\n\n" +
                        "1. 器具准备\n" +
                        "   - 手冲壶\n" +
                        "   - 滤杯\n" +
                        "   - 分享壶\n\n" +
                        "2. 咖啡豆选择\n" +
                        "   - 新鲜烘焙\n" +
                        "   - 适合手冲的烘焙度\n\n" +
                        "3. 冲泡参数\n" +
                        "   - 水温：92-96°C\n" +
                        "   - 粉水比：1:15\n" +
                        "   - 萃取时间：2-3分钟")
                .author("咖啡爱好者")
                .tags(Arrays.asList("咖啡", "生活", "手冲", "美食"))
                .views(4500)
                .status("published")
                .createTime(LocalDateTime.now().minusDays(20).format(FORMATTER))
                .build();
    }

    /**
     * 获取所有预定义的高质量文档
     */
    public static List<BlogDocument> getAllDocuments() {
        return Arrays.asList(
                createElasticsearchIntro(),
                createElasticsearchSearch(),
                createElasticsearchQuery(),
                createMilvusIntro(),
                createVectorSearch(),
                createRAGIntro(),
                createRAGPipeline(),
                createLLMPrompt(),
                createTransformer(),
                createKafka(),
                createFlink(),
                createTravel(),
                createCoffee()
        );
    }

    /**
     * 获取技术类文档（ES, Milvus, 大数据）
     */
    public static List<BlogDocument> getTechDocuments() {
        return Arrays.asList(
                createElasticsearchIntro(),
                createElasticsearchSearch(),
                createElasticsearchQuery(),
                createMilvusIntro(),
                createVectorSearch(),
                createKafka(),
                createFlink()
        );
    }

    /**
     * 获取 AI/RAG 相关文档
     */
    public static List<BlogDocument> getAIDocuments() {
        return Arrays.asList(
                createRAGIntro(),
                createRAGPipeline(),
                createLLMPrompt(),
                createTransformer(),
                createMilvusIntro(),
                createVectorSearch()
        );
    }

    /**
     * 生成指定数量的随机文档
     */
    public static List<BlogDocument> generateRandomDocuments(int count) {
        List<BlogDocument> documents = new ArrayList<>();
        String[] authors = {"技术君", "数据人", "AI研究者", "架构师", "开发者", "产品经理"};
        String[] statuses = {"published", "draft"};

        for (int i = 0; i < count; i++) {
            BlogDocument doc = BlogDocument.builder()
                    .id(UUID.randomUUID().toString())
                    .title("随机文章标题 " + (i + 1))
                    .content("这是一篇关于技术、生活或其他话题的随机文章。" +
                            "文章内容包含各种话题的讨论和分析。" +
                            "读者可以从中了解相关领域的知识和见解。")
                    .author(authors[(int) (Math.random() * authors.length)])
                    .tags(Arrays.asList("技术", "分享", "经验"))
                    .views((int) (Math.random() * 10000))
                    .status(statuses[(int) (Math.random() * statuses.length)])
                    .createTime(LocalDateTime.now().minusDays((int) (Math.random() * 60)).format(FORMATTER))
                    .build();
            documents.add(doc);
        }

        return documents;
    }
}
