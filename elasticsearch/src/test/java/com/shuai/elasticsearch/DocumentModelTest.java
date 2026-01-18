package com.shuai.elasticsearch;

import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.model.LocationDocument;
import com.shuai.elasticsearch.model.LocationDocument.GeoPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 文档模型测试类
 *
 * 测试说明
 * ----------
 * 本测试类用于验证文档模型的创建和属性设置。
 */
public class DocumentModelTest {

    /**
     * 测试 BlogDocument 创建
     */
    @Test
    @DisplayName("BlogDocument 创建测试")
    void testBlogDocumentCreation() {
        BlogDocument doc = BlogDocument.builder()
            .id("1")
            .title("测试标题")
            .content("测试内容")
            .author("测试作者")
            .tags(Arrays.asList("Java", "测试"))
            .views(100)
            .status("published")
            .createTime("2024-01-15")
            .build();

        assertNotNull(doc, "文档不应为空");
        assertEquals("1", doc.getId(), "ID 应匹配");
        assertEquals("测试标题", doc.getTitle(), "标题应匹配");
        assertEquals("测试内容", doc.getContent(), "内容应匹配");
        assertEquals("测试作者", doc.getAuthor(), "作者应匹配");
        assertEquals(2, doc.getTags().size(), "标签数量应匹配");
        assertEquals(100, doc.getViews(), "浏览量应匹配");
        assertEquals("published", doc.getStatus(), "状态应匹配");
        assertEquals("2024-01-15", doc.getCreateTime(), "创建时间应匹配");

        System.out.println("BlogDocument 创建测试通过");
    }

    /**
     * 测试 BlogDocument 标签操作
     */
    @Test
    @DisplayName("BlogDocument 标签测试")
    void testBlogDocumentTags() {
        BlogDocument doc = BlogDocument.builder()
            .id("2")
            .title("标签测试")
            .tags(Arrays.asList("Spring", "Boot"))
            .build();

        List<String> tags = doc.getTags();
        assertNotNull(tags, "标签列表不应为空");
        assertTrue(tags.contains("Spring"), "应包含 Spring 标签");
        assertTrue(tags.contains("Boot"), "应包含 Boot 标签");

        System.out.println("BlogDocument 标签测试通过");
    }

    /**
     * 测试 LocationDocument 创建
     */
    @Test
    @DisplayName("LocationDocument 创建测试")
    void testLocationDocumentCreation() {
        GeoPoint location = GeoPoint.builder()
            .lat(39.9042)
            .lon(116.4074)
            .build();

        LocationDocument doc = LocationDocument.builder()
            .id("1")
            .name("天安门")
            .location(location)
            .type("景点")
            .build();

        assertNotNull(doc, "文档不应为空");
        assertEquals("1", doc.getId(), "ID 应匹配");
        assertEquals("天安门", doc.getName(), "名称应匹配");
        assertNotNull(doc.getLocation(), "位置不应为空");
        assertEquals(39.9042, doc.getLocation().getLat(), 0.0001, "纬度应匹配");
        assertEquals(116.4074, doc.getLocation().getLon(), 0.0001, "经度应匹配");
        assertEquals("景点", doc.getType(), "类型应匹配");

        System.out.println("LocationDocument 创建测试通过");
    }

    /**
     * 测试 GeoPoint 创建
     */
    @Test
    @DisplayName("GeoPoint 创建测试")
    void testGeoPointCreation() {
        GeoPoint point = GeoPoint.builder()
            .lat(31.2304)
            .lon(121.4737)
            .build();

        assertNotNull(point, "GeoPoint 不应为空");
        assertEquals(31.2304, point.getLat(), 0.0001, "纬度应匹配");
        assertEquals(121.4737, point.getLon(), 0.0001, "经度应匹配");

        System.out.println("GeoPoint 创建测试通过");
    }

    /**
     * 测试 BlogDocument 链式构建
     */
    @Test
    @DisplayName("BlogDocument 链式构建测试")
    void testBlogDocumentBuilderChaining() {
        BlogDocument doc = BlogDocument.builder()
            .id("3")
            .title("链式构建测试")
            .content("测试链式构建是否正常工作")
            .author("测试")
            .tags(Arrays.asList("测试"))
            .views(50)
            .status("draft")
            .createTime("2024-01-16")
            .build();

        assertNotNull(doc, "文档不应为空");
        assertEquals("链式构建测试", doc.getTitle(), "标题应匹配");

        System.out.println("链式构建测试通过");
    }
}
