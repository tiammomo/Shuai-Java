package com.shuai.elasticsearch.document;

import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.model.ProductDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 文档操作测试类
 *
 * 测试文档的 CRUD 操作、批量处理等功能。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class DocumentOperationDemoTest {

    @Test
    @DisplayName("测试文档创建")
    void testDocumentCreation() {
        // 测试文档实体创建
        var doc = new com.shuai.elasticsearch.model.BlogDocument();
        doc.setId("test-001");
        doc.setTitle("测试文章");
        doc.setContent("这是一篇测试文章的内容");
        doc.setAuthor("测试作者");
        doc.setTags(java.util.Arrays.asList("测试", "单元测试"));

        assertNotNull(doc);
        assertEquals("test-001", doc.getId());
        assertEquals("测试文章", doc.getTitle());
        assertEquals("测试作者", doc.getAuthor());
        assertEquals(2, doc.getTags().size());
    }

    @Test
    @DisplayName("测试文档 Builder 模式")
    void testDocumentBuilder() {
        BlogDocument doc = BlogDocument.builder()
            .id("test-002")
            .title("Builder 测试")
            .content("使用 Builder 模式创建文档")
            .author("Builder Author")
            .views(100)
            .build();

        assertNotNull(doc);
        assertEquals("test-002", doc.getId());
        assertEquals("Builder 测试", doc.getTitle());
        assertEquals(100, doc.getViews());
    }

    @Test
    @DisplayName("测试 ProductDocument 创建")
    void testProductDocumentCreation() {
        var product = com.shuai.elasticsearch.model.ProductDocument.builder()
            .id("P001")
            .name("测试产品")
            .category("电子产品")
            .price(1999.00)
            .stock(100)
            .status("on_sale")
            .build();

        assertNotNull(product);
        assertEquals("P001", product.getId());
        assertEquals("测试产品", product.getName());
        assertEquals("on_sale", product.getStatus());
        assertTrue(product.isOnSale());
    }

    @Test
    @DisplayName("测试 ProductProperty 嵌套属性")
    void testProductProperty() {
        var property = new com.shuai.elasticsearch.model.ProductDocument.ProductProperty("内存", "16GB");
        assertEquals("内存", property.getName());
        assertEquals("16GB", property.getValue());
        assertEquals("string", property.getType());

        var propertyWithType = new com.shuai.elasticsearch.model.ProductDocument.ProductProperty("重量", "1.5kg", "number");
        assertEquals("重量", propertyWithType.getName());
        assertEquals("1.5kg", propertyWithType.getValue());
        assertEquals("number", propertyWithType.getType());
    }

    @Test
    @DisplayName("测试 ProductDocument 属性获取")
    void testProductPropertyAccess() {
        var product = com.shuai.elasticsearch.model.ProductDocument.builder()
            .id("P002")
            .name("笔记本电脑")
            .category("电子产品")
            .price(5999.00)
            .properties(java.util.Arrays.asList(
                new com.shuai.elasticsearch.model.ProductDocument.ProductProperty("内存", "16GB"),
                new com.shuai.elasticsearch.model.ProductDocument.ProductProperty("硬盘", "512GB")
            ))
            .build();

        assertEquals("16GB", product.getPropertyValue("内存"));
        assertEquals("512GB", product.getPropertyValue("硬盘"));
        assertNull(product.getPropertyValue("不存在的属性"));
    }

    @Test
    @DisplayName("测试 ProductDocument 库存检查")
    void testProductStockCheck() {
        var product1 = com.shuai.elasticsearch.model.ProductDocument.builder()
            .stock(100)
            .build();

        var product2 = com.shuai.elasticsearch.model.ProductDocument.builder()
            .stock(5)
            .build();

        var product3 = com.shuai.elasticsearch.model.ProductDocument.builder()
            .build();

        assertTrue(product1.hasEnoughStock(50));
        assertFalse(product2.hasEnoughStock(50));
        assertFalse(product3.hasEnoughStock(50));
    }

    @Test
    @DisplayName("测试 ProductDocument 标签检查")
    void testProductTagCheck() {
        var productWithTags = com.shuai.elasticsearch.model.ProductDocument.builder()
            .tags(java.util.Arrays.asList("热销", "新品", "推荐"))
            .build();

        var productNoTags = com.shuai.elasticsearch.model.ProductDocument.builder()
            .build();

        assertTrue(productWithTags.hasTag("热销"));
        assertTrue(productWithTags.hasTag("推荐"));
        assertFalse(productWithTags.hasTag("促销"));
        assertFalse(productNoTags.hasTag("任何标签"));
    }

    @Test
    @DisplayName("测试 ProductProperty toString")
    void testProductPropertyToString() {
        ProductDocument.ProductProperty property = new ProductDocument.ProductProperty("CPU", "i7");
        assertEquals("CPU: i7", property.toString());
    }
}
