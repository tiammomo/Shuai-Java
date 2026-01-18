package com.shuai.elasticsearch.rag;

import com.shuai.elasticsearch.llm.OpenAIClient;

import java.util.List;

/**
 * RAG 提示词模板
 *
 * 模块概述
 * ----------
 * 本模块提供 RAG 系统的提示词模板构建功能。
 *
 * 核心内容
 * ----------
 *   - System Prompt: 定义助手行为和规则
 *   - User Prompt: 包含检索到的上下文和问题
 *   - 多轮对话支持
 *   - Token 数量控制
 *
 * 使用示例
 * ----------
 * {@code
 * List<Message> messages = RagPromptTemplate.build(context, question);
 * String response = openaiClient.chatCompletion(messages);
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 * @see <a href="https://platform.openai.com/docs/guides/prompt-engineering">OpenAI Prompt Engineering</a>
 */
public class RagPromptTemplate {

    /**
     * 默认系统提示词 - 技术专家模式
     */
    private static final String DEFAULT_SYSTEM_PROMPT = """
        你是一个技术专家，请基于以下参考信息回答用户的问题。

        规则：
        1. 只使用提供的参考信息回答，不要编造内容
        2. 如果参考信息不足以回答，请明确说明
        3. 回答时使用 Markdown 格式
        4. 在答案末尾标注参考来源
        """;

    /**
     * 简洁模式系统提示词
     */
    private static final String CONCISE_SYSTEM_PROMPT = """
        请基于以下上下文回答问题。回答要简洁明了，直接给出答案。
        如果上下文没有相关信息，请说"根据提供的信息，我无法回答这个问题"。
        """;

    /**
     * 详细模式系统提示词
     */
    private static final String DETAILED_SYSTEM_PROMPT = """
        你是一个专业的技术顾问。请仔细阅读以下参考信息，然后回答用户的问题。

        回答要求：
        1. 首先分析问题的关键点
        2. 从上下文中提取相关信息
        3. 组织清晰的答案结构
        4. 使用 Markdown 格式（包括标题、列表、代码块等）
        5. 标注参考来源（使用 [来源N] 格式）
        6. 如有必要，添加实际应用示例

        如果上下文中没有找到相关信息，请明确说明，并建议用户参考官方文档或其他资源。
        """;

    /**
     * 默认用户提示词模板
     */
    private static final String DEFAULT_USER_TEMPLATE = """
        ## 参考信息

        %s

        ---

        ## 用户问题

        %s

        ## 回答

        """;

    /**
     * 详细用户提示词模板
     */
    private static final String DETAILED_USER_TEMPLATE = """
        ### 背景信息

        以下是与你问题相关的技术文档和参考资料：

        %s

        ---

        ### 用户问题

        %s

        ---

        ### 分析与回答

        请按照以下步骤回答：
        1. 问题分析：简要说明问题的核心是什么
        2. 关键信息：从上下文中提取最重要的信息
        3. 完整答案：组织成清晰的结构

        """;

    /**
     * 构建默认提示词
     *
     * @param context 检索到的上下文信息
     * @param question 用户问题
     * @return 消息列表，可直接传给 OpenAI API
     */
    public static List<OpenAIClient.Message> build(String context, String question) {
        return build(context, question, DEFAULT_SYSTEM_PROMPT, DEFAULT_USER_TEMPLATE);
    }

    /**
     * 构建简洁提示词
     *
     * @param context 检索到的上下文信息
     * @param question 用户问题
     * @return 消息列表
     */
    public static List<OpenAIClient.Message> buildConcise(String context, String question) {
        String userContent = String.format(DEFAULT_USER_TEMPLATE, context, question);
        return List.of(
            OpenAIClient.Message.system(CONCISE_SYSTEM_PROMPT),
            OpenAIClient.Message.user(userContent)
        );
    }

    /**
     * 构建详细提示词
     *
     * @param context 检索到的上下文信息
     * @param question 用户问题
     * @return 消息列表
     */
    public static List<OpenAIClient.Message> buildDetailed(String context, String question) {
        String userContent = String.format(DETAILED_USER_TEMPLATE, context, question);
        return List.of(
            OpenAIClient.Message.system(DETAILED_SYSTEM_PROMPT),
            OpenAIClient.Message.user(userContent)
        );
    }

    /**
     * 构建自定义提示词
     *
     * @param context 检索到的上下文信息
     * @param question 用户问题
     * @param systemPrompt 自定义系统提示词
     * @param userTemplate 自定义用户模板
     * @return 消息列表
     */
    public static List<OpenAIClient.Message> build(
            String context,
            String question,
            String systemPrompt,
            String userTemplate) {

        String userContent = String.format(userTemplate, context, question);
        return List.of(
            OpenAIClient.Message.system(systemPrompt),
            OpenAIClient.Message.user(userContent)
        );
    }

    /**
     * 构建简单提示词（无系统提示）
     *
     * @param question 用户问题
     * @return 消息列表
     */
    public static List<OpenAIClient.Message> buildSimple(String question) {
        return List.of(OpenAIClient.Message.user(question));
    }

    /**
     * 构建多轮对话提示词
     *
     * @param conversationHistory 对话历史
     * @param context 当前检索到的上下文
     * @param currentQuestion 当前问题
     * @return 消息列表
     */
    public static List<OpenAIClient.Message> buildMultiTurn(
            List<OpenAIClient.Message> conversationHistory,
            String context,
            String currentQuestion) {

        String userContent = String.format(DEFAULT_USER_TEMPLATE, context, currentQuestion);

        // 构建消息列表：系统提示 + 对话历史 + 当前上下文和问题
        int historySize = conversationHistory.size();
        OpenAIClient.Message[] messages = new OpenAIClient.Message[historySize + 2];

        messages[0] = OpenAIClient.Message.system(DEFAULT_SYSTEM_PROMPT);

        // 添加对话历史（保留最近 N 轮）
        int startIndex = Math.max(0, historySize - 10); // 最多保留10轮
        for (int i = startIndex; i < historySize; i++) {
            messages[i - startIndex + 1] = conversationHistory.get(i);
        }

        // 添加当前上下文和问题
        messages[messages.length - 1] = OpenAIClient.Message.user(userContent);

        return List.of(messages);
    }

    /**
     * 截断上下文以适应 Token 限制
     *
     * @param context 原始上下文
     * @param maxTokens 最大 Token 数（估算）
     * @return 截断后的上下文
     */
    public static String truncateContext(String context, int maxTokens) {
        // 估算 Token 数：中文约2字符/Token，英文约4字符/Token
        int estimatedTokens = estimateTokens(context);
        if (estimatedTokens <= maxTokens) {
            return context;
        }

        // 截断到目标 Token 数对应的字符数
        int targetChars = maxTokens * 2;
        String truncated = context.substring(0, Math.min(targetChars, context.length()));

        // 找到最后一个句号或换行符，避免截断句子
        int lastPeriod = Math.max(
            truncated.lastIndexOf('。'),
            Math.max(
                truncated.lastIndexOf('.'),
                truncated.lastIndexOf('\n')
            )
        );

        if (lastPeriod > targetChars / 2) {
            truncated = truncated.substring(0, lastPeriod + 1);
        }

        return truncated + "\n\n[上下文已截断]";
    }

    /**
     * 估算文本 Token 数
     *
     * @param text 输入文本
     * @return 估算的 Token 数量
     */
    public static int estimateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // 简单估算：中文约2字符/Token，英文约4字符/Token
        int chineseChars = 0;
        int otherChars = 0;

        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS) {
                chineseChars++;
            } else {
                otherChars++;
            }
        }

        return chineseChars / 2 + otherChars / 4;
    }

    /**
     * 为引用编号创建格式化字符串
     *
     * @param sources 原始文档列表
     * @return 带编号的引用字符串
     */
    public static String formatReferences(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sources.size(); i++) {
            sb.append(String.format("[来源%d]: %s%n", i + 1, sources.get(i)));
        }
        return sb.toString();
    }

    /**
     * 获取默认系统提示词
     */
    public static String getDefaultSystemPrompt() {
        return DEFAULT_SYSTEM_PROMPT;
    }

    /**
     * 获取简洁模式系统提示词
     */
    public static String getConciseSystemPrompt() {
        return CONCISE_SYSTEM_PROMPT;
    }

    /**
     * 获取详细模式系统提示词
     */
    public static String getDetailedSystemPrompt() {
        return DETAILED_SYSTEM_PROMPT;
    }
}
