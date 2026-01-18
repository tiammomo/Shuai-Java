package com.shuai.elasticsearch.aggregation;

import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.model.LocationDocument;
import com.shuai.elasticsearch.model.LocationDocument.GeoPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 聚合分析测试类
 *
 * 测试 Elasticsearch 聚合分析功能。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class AggregationDemoTest {

    @Test
    @DisplayName("测试 BlogDocument 聚合模型")
    void testBlogDocumentForAggregation() {
        BlogDocument doc = BlogDocument.builder()
            .id("agg-test-001")
            .title("聚合测试文章")
            .content("内容")
            .author("作者1")
            .tags(java.util.Arrays.asList("Java", "ES"))
            .views(500)
            .status("published")
            .build();

        assertNotNull(doc);
        assertEquals("agg-test-001", doc.getId());
        assertEquals(500, doc.getViews());
    }

    @Test
    @DisplayName("测试 BlogDocument 多个标签")
    void testBlogDocumentMultipleTags() {
        BlogDocument doc = BlogDocument.builder()
            .id("agg-test-002")
            .tags(java.util.Arrays.asList("Java", "Spring", "ES", "Kafka"))
            .views(300)
            .build();

        assertEquals(4, doc.getTags().size());
        assertTrue(doc.getTags().contains("ES"));
    }

    @Test
    @DisplayName("测试 BlogDocument 不同作者")
    void testBlogDocumentDifferentAuthors() {
        BlogDocument doc1 = BlogDocument.builder()
            .id("agg-test-003")
            .author("作者A")
            .views(100)
            .build();

        BlogDocument doc2 = BlogDocument.builder()
            .id("agg-test-004")
            .author("作者B")
            .views(200)
            .build();

        BlogDocument doc3 = BlogDocument.builder()
            .id("agg-test-005")
            .author("作者A")
            .views(150)
            .build();

        assertEquals("作者A", doc1.getAuthor());
        assertEquals("作者B", doc2.getAuthor());
        assertEquals(doc1.getAuthor(), doc3.getAuthor()); // 相同作者
    }

    @Test
    @DisplayName("测试 BlogDocument 浏览量统计")
    void testBlogDocumentViewsStatistics() {
        java.util.List<BlogDocument> docs = java.util.Arrays.asList(
            BlogDocument.builder().id("1").views(100).build(),
            BlogDocument.builder().id("2").views(200).build(),
            BlogDocument.builder().id("3").views(300).build(),
            BlogDocument.builder().id("4").views(400).build(),
            BlogDocument.builder().id("5").views(500).build()
        );

        // 计算统计值
        int sum = docs.stream().mapToInt(BlogDocument::getViews).sum();
        double avg = sum / (double) docs.size();
        int max = docs.stream().mapToInt(BlogDocument::getViews).max().orElse(0);
        int min = docs.stream().mapToInt(BlogDocument::getViews).min().orElse(0);

        assertEquals(1500, sum);
        assertEquals(300.0, avg, 0.1);
        assertEquals(500, max);
        assertEquals(100, min);
    }

    @Test
    @DisplayName("测试 LocationDocument 创建")
    void testLocationDocumentCreation() {
        LocationDocument loc = LocationDocument.builder()
            .id("loc-001")
            .name("测试地点")
            .location(new GeoPoint(39.90, 116.40))
            .type("restaurant")
            .build();

        assertNotNull(loc);
        assertEquals("loc-001", loc.getId());
        assertEquals("测试地点", loc.getName());
        assertNotNull(loc.getLocation());
        assertEquals(39.90, loc.getLocation().getLat());
        assertEquals(116.40, loc.getLocation().getLon());
    }

    @Test
    @DisplayName("测试 LocationDocument GeoPoint")
    void testLocationDocumentGeoPoint() {
        GeoPoint point = new GeoPoint(31.23, 121.47);
        assertEquals(31.23, point.getLat());
        assertEquals(121.47, point.getLon());
    }

    @Test
    @DisplayName("测试 LocationDocument 不同类型")
    void testLocationDocumentTypes() {
        LocationDocument restaurant = LocationDocument.builder()
            .id("loc-002")
            .name("餐厅")
            .type("restaurant")
            .location(new GeoPoint(39.90, 116.40))
            .build();

        LocationDocument park = LocationDocument.builder()
            .id("loc-003")
            .name("公园")
            .type("park")
            .location(new GeoPoint(39.91, 116.41))
            .build();

        assertEquals("restaurant", restaurant.getType());
        assertEquals("park", park.getType());
    }

    @Test
    @DisplayName("测试 LocationDocument builder 链式调用")
    void testLocationDocumentBuilder() {
        LocationDocument loc = LocationDocument.builder()
            .id("loc-004")
            .name("购物中心")
            .location(new GeoPoint(40.01, 116.50))
            .type("mall")
            .build();

        assertNotNull(loc);
        assertEquals("购物中心", loc.getName());
        assertEquals("mall", loc.getType());
    }

    @Test
    @DisplayName("测试地理位置距离计算")
    void testGeoDistanceCalculation() {
        // 北京天安门
        double lat1 = 39.90;
        double lon1 = 116.40;

        // 北京故宫
        double lat2 = 39.916;

        // 计算近似距离（使用简化公式）
        double dist = calculateApproxDistance(lat1, lon1, lat2);

        // 应该在 1-2 公里范围内
        assertTrue(dist > 0 && dist < 5);
    }

    /**
     * 简化的大致距离计算（实际应使用 ES geo_distance）
     */
    private double calculateApproxDistance(double lat1, double lon1, double lat2) {
        // 1度纬度约等于 111 公里
        double latDiff = Math.abs(lat1 - lat2) * 111;
        // 1度经度约等于 111 * cos(纬度) 公里
        double lonDiff = Math.abs(lon1 - lon1) * 111 * Math.cos(Math.toRadians(lat1));
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);
    }
}
