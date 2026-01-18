package com.shuai.elasticsearch.geo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.BlogDocument;
import com.shuai.elasticsearch.model.LocationDocument;
import com.shuai.elasticsearch.model.LocationDocument.GeoPoint;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

/**
 * 地理查询演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 地理位置查询功能。
 *
 * 核心内容
 * ----------
 *   - 地理位置类型: geo_point、geo_shape
 *   - 地理距离查询: 距离范围内搜索
 *   - 地理范围查询: 矩形、圆形区域搜索
 *   - 地理位置聚合: geo_grid、geo_centroid
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html">Geo Queries</a>
 */
public class GeoQueryDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "locations";

    public GeoQueryDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有地理查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("地理查询演示", "Elasticsearch 地理位置查询功能");

        geoTypes();
        geoDistanceQuery();
        geoBoundingBoxQuery();
        geoAggregation();
    }

    /**
     * 地理位置类型说明
     */
    private void geoTypes() {
        ResponsePrinter.printMethodInfo("geoTypes", "地理位置类型");

        System.out.println("  geo_point - 经纬度点:");
        System.out.println("    用于表示一个具体的地理位置点");
        System.out.println("    格式: { \"lat\": 39.90, \"lon\": 116.40 }");
        System.out.println("    也支持: \"39.90,116.40\" 或 geohash");

        System.out.println("\n  geo_shape - 几何图形:");
        System.out.println("    用于表示复杂的地理形状");
        System.out.println("    支持: point, linestring, polygon, multipoint 等");

        System.out.println("\n  Java Client 创建地理索引:");
        System.out.println("    client.indices().create(c -> c");
        System.out.println("      .index(INDEX_NAME)");
        System.out.println("      .mappings(m -> m");
        System.out.println("        .properties(\"location\", p -> p.geoPoint(gp -> gp))");
        System.out.println("      )");
        System.out.println("    )");
    }

    /**
     * 地理距离查询
     *
     * REST API:
     *   GET /locations/_search
     *   { "query": { "geo_distance": { "distance": "5km", "location": { "lat": 39.90, "lon": 116.40 }}}}
     */
    private void geoDistanceQuery() throws IOException {
        ResponsePrinter.printMethodInfo("geoDistanceQuery", "地理距离查询");

        // 1. geo_distance - 距离范围内查询
        System.out.println("\n  1. geo_distance 查询（5km 范围内）:");
        SearchResponse<LocationDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.geoDistance(gd -> gd
                .field("location")
                .distance("5km")
                .location(loc -> loc.latlon(ll -> ll.lat(39.90).lon(116.40)))
            ))
        , LocationDocument.class);

        printGeoResults(response1, "5km 范围内");

        // 2. 距离范围查询
        System.out.println("\n  2. 距离范围查询 (1km - 10km):");
        SearchResponse<LocationDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.bool(b -> b
                .filter(f -> f.geoDistance(gd -> gd
                    .field("location")
                    .distance("10km")
                    .location(loc -> loc.latlon(ll -> ll.lat(39.90).lon(116.40)))
                ))
                .mustNot(mn -> mn.geoDistance(gd -> gd
                    .field("location")
                    .distance("1km")
                    .location(loc -> loc.latlon(ll -> ll.lat(39.90).lon(116.40)))
                ))
            ))
        , LocationDocument.class);

        printGeoResults(response2, "1km - 10km 范围");

        // 3. 距离排序
        System.out.println("\n  3. 按距离排序（由近到远）:");
        SearchResponse<LocationDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchAll(m -> m))
            .sort(so -> so.geoDistance(gd -> gd
                .field("location")
                .location(loc -> loc.latlon(ll -> ll.lat(39.90).lon(116.40)))
                .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                .unit(co.elastic.clients.elasticsearch._types.DistanceUnit.Kilometers)
            ))
        , LocationDocument.class);

        printGeoResults(response3, "按距离排序");
    }

    /**
     * 地理边界框查询
     *
     * REST API:
     *   GET /locations/_search
     *   { "query": { "geo_bounding_box": { "location": { "top_left": {...}, "bottom_right": {...} }}}}
     */
    private void geoBoundingBoxQuery() throws IOException {
        ResponsePrinter.printMethodInfo("geoBoundingBoxQuery", "地理边界框查询");

        // 1. geo_bounding_box - 矩形范围
        System.out.println("\n  1. geo_bounding_box 矩形查询:");
        SearchResponse<LocationDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.geoBoundingBox(gbb -> gbb
                .field("location")
                .boundingBox(bb -> bb
                    .tlbr(tlbr -> tlbr
                        .topLeft(tl -> tl.latlon(ll -> ll.lat(40.0).lon(116.0)))
                        .bottomRight(br -> br.latlon(ll -> ll.lat(39.8).lon(116.5)))
                    )
                )
            ))
        , LocationDocument.class);

        printGeoResults(response1, "矩形范围内");

        // 2. geo_distance 模拟圆形查询 (使用较大距离范围)
        System.out.println("\n  2. geo_distance 模拟圆形查询:");
        SearchResponse<LocationDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.geoDistance(gd -> gd
                .field("location")
                .distance("5km")
                .location(loc -> loc.latlon(ll -> ll.lat(39.90).lon(116.40)))
            ))
        , LocationDocument.class);

        printGeoResults(response2, "圆形范围内");
    }

    /**
     * 地理位置聚合
     *
     * REST API:
     *   GET /locations/_search
     *   { "size": 0, "aggs": { "geo_grid": { "geotile_grid": { "field": "location", "precision": 12 }}}}
     */
    private void geoAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("geoAggregation", "地理位置聚合");

        // 1. geotile_grid - 地理网格聚合
        System.out.println("\n  1. geotile_grid 地理网格聚合:");
        SearchResponse<LocationDocument> response1 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("geo_grid", a -> a.geotileGrid(gtg -> gtg
                .field("location")
                .precision(12)
            ))
        , LocationDocument.class);

        if (response1.aggregations().containsKey("geo_grid")) {
            response1.aggregations().get("geo_grid").geotileGrid().buckets().array()
                .forEach(b -> System.out.println("    瓦片: " + b.key() + ", 文档数: " + b.docCount()));
        }

        // 2. geo_centroid - 地理中心聚合
        System.out.println("\n  2. geo_centroid 地理中心聚合:");
        SearchResponse<LocationDocument> response2 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("center", a -> a.geoCentroid(gc -> gc.field("location")))
        , LocationDocument.class);

        if (response2.aggregations().containsKey("center")) {
            var centroid = response2.aggregations().get("center").geoCentroid();
            System.out.println("    中心点: " + centroid.location());
        }

        // 3. 按距离范围聚合
        System.out.println("\n  3. 按距离范围聚合:");
        SearchResponse<LocationDocument> response3 = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("by_distance", a -> a.geoDistance(gd -> gd
                .field("location")
                .origin(o -> o.latlon(ll -> ll.lat(39.90).lon(116.40)))
                .ranges(r -> r
                    .to(1000d)
                    .from(1000d).to(5000d)
                    .from(5000d)
                )
            ))
        , LocationDocument.class);

        if (response3.aggregations().containsKey("by_distance")) {
            response3.aggregations().get("by_distance").geoDistance().buckets().array()
                .forEach(b -> System.out.println("    " + b.key() + ": " + b.docCount() + " 个点"));
        }
    }

    /**
     * 打印地理查询结果
     */
    private void printGeoResults(SearchResponse<LocationDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    描述: " + description + ", 命中: " + total);

        for (Hit<LocationDocument> hit : response.hits().hits()) {
            LocationDocument doc = hit.source();
            if (doc != null && doc.getLocation() != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getName() +
                    " (" + doc.getLocation().getLat() + ", " + doc.getLocation().getLon() + ")");
            }
        }
    }
}
