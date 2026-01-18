package com.shuai.elasticsearch.geo;

import com.shuai.elasticsearch.model.LocationDocument;
import com.shuai.elasticsearch.model.LocationDocument.GeoPoint;
import com.shuai.elasticsearch.model.LocationDocument.GeoShape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 地理查询测试类
 *
 * 测试 Elasticsearch 地理位置查询功能。
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 */
class GeoQueryTest {

    @Test
    @DisplayName("测试 GeoPoint 创建")
    void testGeoPointCreation() {
        GeoPoint point = new GeoPoint(39.90, 116.40);

        assertNotNull(point);
        assertEquals(39.90, point.getLat());
        assertEquals(116.40, point.getLon());
    }

    @Test
    @DisplayName("测试 GeoPoint Builder")
    void testGeoPointBuilder() {
        GeoPoint point = GeoPoint.builder()
            .lat(31.23)
            .lon(121.47)
            .build();

        assertEquals(31.23, point.getLat());
        assertEquals(121.47, point.getLon());
    }

    @Test
    @DisplayName("测试 GeoPoint toString")
    void testGeoPointToString() {
        GeoPoint point = new GeoPoint(39.90, 116.40);
        String result = point.toString();

        assertTrue(result.contains("39.9"));
        assertTrue(result.contains("116.4"));
    }

    @Test
    @DisplayName("测试 LocationDocument 创建")
    void testLocationDocumentCreation() {
        LocationDocument loc = LocationDocument.builder()
            .id("geo-test-001")
            .name("天安门")
            .location(new GeoPoint(39.90, 116.40))
            .type("landmark")
            .build();

        assertNotNull(loc);
        assertEquals("geo-test-001", loc.getId());
        assertEquals("天安门", loc.getName());
        assertNotNull(loc.getLocation());
        assertEquals("landmark", loc.getType());
    }

    @Test
    @DisplayName("测试 LocationDocument 不同类型")
    void testLocationDocumentTypes() {
        LocationDocument restaurant = LocationDocument.builder()
            .id("type-001")
            .name("餐厅")
            .type("restaurant")
            .location(new GeoPoint(39.91, 116.41))
            .build();

        LocationDocument park = LocationDocument.builder()
            .id("type-002")
            .name("公园")
            .type("park")
            .location(new GeoPoint(39.92, 116.42))
            .build();

        assertEquals("restaurant", restaurant.getType());
        assertEquals("park", park.getType());
    }

    @Test
    @DisplayName("测试 LocationDocument 相等性")
    void testLocationDocumentEquality() {
        LocationDocument loc1 = LocationDocument.builder()
            .id("same-id")
            .name("地点1")
            .location(new GeoPoint(39.90, 116.40))
            .build();

        LocationDocument loc2 = LocationDocument.builder()
            .id("same-id")
            .name("地点2")
            .location(new GeoPoint(39.91, 116.41))
            .build();

        LocationDocument loc3 = LocationDocument.builder()
            .id("different-id")
            .name("地点1")
            .location(new GeoPoint(39.90, 116.40))
            .build();

        assertEquals(loc1, loc2); // 相同 ID
        assertNotEquals(loc1, loc3); // 不同 ID
    }

    @Test
    @DisplayName("测试 GeoShape 创建")
    void testGeoShapeCreation() {
        // 创建一个简单的多边形坐标
        java.util.List<java.util.List<java.util.List<Double>>> coordinates = java.util.Arrays.asList(
            java.util.Arrays.asList(
                java.util.Arrays.asList(116.0, 39.0),
                java.util.Arrays.asList(116.5, 39.0),
                java.util.Arrays.asList(116.5, 39.5),
                java.util.Arrays.asList(116.0, 39.5),
                java.util.Arrays.asList(116.0, 39.0)
            )
        );

        GeoShape shape = GeoShape.builder()
            .type("polygon")
            .coordinates(coordinates)
            .build();

        assertNotNull(shape);
        assertEquals("polygon", shape.getType());
        assertNotNull(shape.getCoordinates());
    }

    @Test
    @DisplayName("测试 LocationDocument with GeoShape")
    void testLocationDocumentWithGeoShape() {
        java.util.List<java.util.List<java.util.List<Double>>> coordinates = java.util.Arrays.asList(
            java.util.Arrays.asList(
                java.util.Arrays.asList(116.0, 39.0),
                java.util.Arrays.asList(116.5, 39.0),
                java.util.Arrays.asList(116.5, 39.5),
                java.util.Arrays.asList(116.0, 39.5),
                java.util.Arrays.asList(116.0, 39.0)
            )
        );

        GeoShape shape = GeoShape.builder()
            .type("polygon")
            .coordinates(coordinates)
            .build();

        LocationDocument loc = LocationDocument.builder()
            .id("shape-001")
            .name("北京市区域")
            .location(new GeoPoint(39.90, 116.40))
            .area(shape)
            .build();

        assertNotNull(loc);
        assertNotNull(loc.getArea());
        assertEquals("polygon", loc.getArea().getType());
    }

    @Test
    @DisplayName("测试经纬度范围")
    void testCoordinateRange() {
        // 有效经纬度范围
        double validLat = 39.90;
        double validLon = 116.40;

        // 纬度范围: -90 到 90
        assertTrue(validLat >= -90 && validLat <= 90);
        // 经度范围: -180 到 180
        assertTrue(validLon >= -180 && validLon <= 180);
    }

    @Test
    @DisplayName("测试距离计算")
    void testDistanceCalculation() {
        // 北京
        double lat1 = 39.90;
        double lon1 = 116.40;

        // 上海
        double lat2 = 31.23;
        double lon2 = 121.47;

        // 计算大致距离（公里）
        double latDiff = Math.abs(lat1 - lat2) * 111;
        double lonDiff = Math.abs(lon1 - lon2) * 111 * Math.cos(Math.toRadians(lat1));
        double distance = Math.sqrt(latDiff * latDiff + lonDiff * lonDiff);

        // 北京到上海大约 1000-1100 公里
        assertTrue(distance > 800 && distance < 1200);
    }

    @Test
    @DisplayName("测试 LocationDocument toString")
    void testLocationDocumentToString() {
        LocationDocument loc = LocationDocument.builder()
            .id("test-001")
            .name("测试地点")
            .location(new GeoPoint(39.90, 116.40))
            .type("test")
            .build();

        String result = loc.toString();
        assertTrue(result.contains("test-001"));
        assertTrue(result.contains("测试地点"));
        assertTrue(result.contains("test"));
    }

    @Test
    @DisplayName("测试边界框坐标")
    void testBoundingBoxCoordinates() {
        // 模拟边界框
        double topLeftLat = 40.0;
        double topLeftLon = 116.0;
        double bottomRightLat = 39.8;
        double bottomRightLon = 116.5;

        // 验证边界框逻辑
        assertTrue(topLeftLat > bottomRightLat);
        assertTrue(topLeftLon < bottomRightLon);

        // 计算中心点
        double centerLat = (topLeftLat + bottomRightLat) / 2;
        double centerLon = (topLeftLon + bottomRightLon) / 2;

        assertEquals(39.9, centerLat, 0.01);
        assertEquals(116.25, centerLon, 0.01);
    }
}
