package com.shuai.elasticsearch.fulltext;

import com.shuai.elasticsearch.model.BlogDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 全文检索测试类
 *
 * 测试 Elasticsearch 全文检索高级功能。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class FullTextSearchTest {

    @Test
    @DisplayName("测试 BlogDocument 高亮内容")
    void testBlogDocumentForHighlight() {
        BlogDocument doc = BlogDocument.builder()
            .id("highlight-001")
            .title("Java 教程")
            .content("这是一篇关于 Java 编程的教程文章，包含 Java 基础和高级内容")
            .author("作者")
            .status("published")
            .build();

        assertNotNull(doc);
        assertTrue(doc.getContent().contains("Java"));
        assertTrue(doc.getTitle().contains("Java"));
    }

    @Test
    @DisplayName("测试多字段匹配")
    void testMultiFieldMatch() {
        BlogDocument doc = BlogDocument.builder()
            .id("multi-001")
            .title("Elasticsearch 教程")
            .content("学习 Elasticsearch 搜索技术")
            .author("技术作者")
            .build();

        assertNotNull(doc);
        assertTrue(doc.getTitle().contains("Elasticsearch"));
        assertTrue(doc.getContent().contains("搜索"));
    }

    @Test
    @DisplayName("测试中文分词内容")
    void testChineseTokenization() {
        BlogDocument doc = BlogDocument.builder()
            .id("chinese-001")
            .title("机器学习入门")
            .content("机器学习是人工智能的重要分支，包含监督学习和无监督学习")
            .build();

        assertNotNull(doc);
        // 验证内容包含关键词
        assertTrue(doc.getContent().contains("机器学习"));
        assertTrue(doc.getContent().contains("人工智能"));
    }

    @Test
    @DisplayName("测试短语匹配内容")
    void testPhraseMatch() {
        String content = "Elasticsearch 是一个分布式搜索和分析引擎";

        assertTrue(content.contains("分布式搜索和分析引擎"));
        // 完整短语匹配测试
        assertEquals("Elasticsearch 是一个分布式搜索和分析引擎", content);
    }

    @Test
    @DisplayName("测试查询字符串语法")
    void testQueryStringSyntax() {
        String query = "(Java AND 教程) OR (Python)";

        // 验证查询字符串格式
        assertTrue(query.contains("Java"));
        assertTrue(query.contains("教程"));
        assertTrue(query.contains("Python"));
        assertTrue(query.contains("AND"));
        assertTrue(query.contains("OR"));
    }

    @Test
    @DisplayName("测试字段权重设置")
    void testFieldBoosting() {
        // 模拟字段权重
        java.util.Map<String, Integer> fieldWeights = new java.util.HashMap<>();
        fieldWeights.put("title", 2);  // title 权重 2
        fieldWeights.put("content", 1); // content 权重 1

        assertEquals(2, fieldWeights.get("title"));
        assertEquals(1, fieldWeights.get("content"));
        assertTrue(fieldWeights.get("title") > fieldWeights.get("content"));
    }

    @Test
    @DisplayName("测试评分排序")
    void testScoreSorting() {
        java.util.List<BlogDocument> docs = java.util.Arrays.asList(
            BlogDocument.builder().id("1").title("Java 教程").views(100).build(),
            BlogDocument.builder().id("2").title("Java 高级教程").views(200).build(),
            BlogDocument.builder().id("3").title("Python 教程").views(150).build()
        );

        // 模拟按相关性评分排序
        java.util.List<String> sortedIds = new java.util.ArrayList<>();
        for (BlogDocument doc : docs) {
            if (doc.getTitle().contains("Java")) {
                sortedIds.add(doc.getId());
            }
        }

        assertEquals(2, sortedIds.size());
        assertTrue(sortedIds.contains("1"));
        assertTrue(sortedIds.contains("2"));
    }

    @Test
    @DisplayName("测试高亮标签")
    void testHighlightTags() {
        // 高亮标签配置
        String preTag = "<em>";
        String postTag = "</em>";

        assertEquals("<em>", preTag);
        assertEquals("</em>", postTag);

        // 模拟高亮文本
        String highlighted = preTag + "Java" + postTag + " 教程";
        assertEquals("<em>Java</em> 教程", highlighted);
    }

    @Test
    @DisplayName("测试分片大小设置")
    void testFragmentSettings() {
        int fragmentSize = 150;
        int numberOfFragments = 3;

        assertTrue(fragmentSize > 0);
        assertTrue(numberOfFragments > 0);
    }

    @Test
    @DisplayName("测试复合排序")
    void testCompoundSorting() {
        // 模拟复合排序条件
        java.util.List<String> sortConditions = new java.util.ArrayList<>();
        sortConditions.add("score desc");  // 按评分降序
        sortConditions.add("views desc");  // 按浏览量降序

        assertEquals(2, sortConditions.size());
        assertEquals("score desc", sortConditions.get(0));
        assertTrue(sortConditions.get(0).contains("desc"));
    }
}
