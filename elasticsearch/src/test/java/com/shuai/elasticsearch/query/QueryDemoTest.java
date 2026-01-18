package com.shuai.elasticsearch.query;

import com.shuai.elasticsearch.model.BlogDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 查询演示测试类
 *
 * 测试 Elasticsearch 各种查询类型的使用方法。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class QueryDemoTest {

    @Test
    @DisplayName("测试 BlogDocument 创建")
    void testBlogDocumentCreation() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-001")
            .title("测试文章")
            .content("这是一篇测试文章的内容")
            .author("测试作者")
            .tags(java.util.Arrays.asList("测试", "Java"))
            .status("published")
            .views(100)
            .build();

        assertNotNull(doc);
        assertEquals("test-001", doc.getId());
        assertEquals("测试文章", doc.getTitle());
        assertEquals("测试作者", doc.getAuthor());
        assertEquals("published", doc.getStatus());
        assertEquals(100, doc.getViews());
    }

    @Test
    @DisplayName("测试 BlogDocument 标签")
    void testBlogDocumentTags() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-002")
            .tags(java.util.Arrays.asList("Java", "Spring", "Elasticsearch"))
            .build();

        assertNotNull(doc.getTags());
        assertEquals(3, doc.getTags().size());
        assertTrue(doc.getTags().contains("Java"));
        assertTrue(doc.getTags().contains("Spring"));
    }

    @Test
    @DisplayName("测试 BlogDocument 状态")
    void testBlogDocumentStatus() {
        BlogDocument published = BlogDocument.builder()
            .id("test-003")
            .status("published")
            .build();

        BlogDocument draft = BlogDocument.builder()
            .id("test-004")
            .status("draft")
            .build();

        assertEquals("published", published.getStatus());
        assertEquals("draft", draft.getStatus());
    }

    @Test
    @DisplayName("测试 BlogDocument 浏览量")
    void testBlogDocumentViews() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-005")
            .views(500)
            .build();

        assertEquals(500, doc.getViews());
        assertTrue(doc.getViews() > 100);
    }

    @Test
    @DisplayName("测试 BlogDocument 默认值")
    void testBlogDocumentDefaults() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-007")
            .build();

        assertNull(doc.getTitle());
        assertNull(doc.getContent());
        assertNull(doc.getAuthor());
        assertNull(doc.getStatus()); // 无默认值
        assertNull(doc.getViews()); // Integer 类型默认 null
    }

    @Test
    @DisplayName("测试 BlogDocument builder 链式调用")
    void testBlogDocumentBuilderChain() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-008")
            .title("链式构建测试")
            .content("测试内容")
            .author("作者")
            .status("published")
            .views(200)
            .createTime("2024-01-15")
            .build();

        assertNotNull(doc);
        assertEquals("test-008", doc.getId());
        assertEquals("链式构建测试", doc.getTitle());
        assertEquals(200, doc.getViews());
        assertEquals("2024-01-15", doc.getCreateTime());
    }

    @Test
    @DisplayName("测试 BlogDocument 构造函数")
    void testBlogDocumentConstructor() {
        BlogDocument doc = new BlogDocument(
            "test-009",
            "构造函数测试",
            "内容",
            "作者",
            java.util.Arrays.asList("测试"),
            300,
            "published",
            "2024-01-16"
        );

        assertEquals("test-009", doc.getId());
        assertEquals("构造函数测试", doc.getTitle());
        assertEquals(300, doc.getViews());
    }

    @Test
    @DisplayName("测试 BlogDocument 相等性")
    void testBlogDocumentEquality() {
        BlogDocument doc1 = BlogDocument.builder()
            .id("test-010")
            .title("文章1")
            .build();

        BlogDocument doc2 = BlogDocument.builder()
            .id("test-010")
            .title("文章2")
            .build();

        BlogDocument doc3 = BlogDocument.builder()
            .id("test-011")
            .title("文章1")
            .build();

        assertEquals(doc1, doc2); // 相同 ID
        assertNotEquals(doc1, doc3); // 不同 ID
    }

    @Test
    @DisplayName("测试 BlogDocument Setter")
    void testBlogDocumentSetters() {
        BlogDocument doc = new BlogDocument();
        doc.setId("test-012");
        doc.setTitle("Setter 测试");
        doc.setContent("内容");
        doc.setViews(1000);

        assertEquals("test-012", doc.getId());
        assertEquals("Setter 测试", doc.getTitle());
        assertEquals(1000, doc.getViews());
    }

    @Test
    @DisplayName("测试 BlogDocument toString")
    void testBlogDocumentToString() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-013")
            .title("ToString 测试")
            .author("作者")
            .views(150)
            .status("published")
            .build();

        String result = doc.toString();
        assertTrue(result.contains("test-013"));
        assertTrue(result.contains("ToString 测试"));
    }
}
