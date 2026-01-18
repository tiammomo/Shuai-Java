package com.shuai.elasticsearch.query;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 嵌套查询演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 嵌套查询功能。
 *
 * 核心内容
 * ----------
 *   - 嵌套类型: nested 数据类型
 *   - 嵌套查询: nested 查询语法
 *   - 反嵌套聚合: reverse_nested 聚合
 *
 * 应用场景
 * ----------
 *   - 数组中对象需要独立索引和查询
 *   - 保持数组元素间的关系
 *   - 复杂数据结构建模
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/nested.html">Nested Query</a>
 */
public class NestedQueryDemo {

    private final ElasticsearchClient client;
    private static final String NESTED_INDEX = "product";

    public NestedQueryDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有嵌套查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("嵌套查询演示", "Elasticsearch 嵌套查询功能");

        nestedTypes();
        nestedQuery();
        nestedAggregation();
    }

    /**
     * 嵌套类型说明
     */
    private void nestedTypes() {
        ResponsePrinter.printMethodInfo("nestedTypes", "嵌套类型说明");

        System.out.println("  nested 数据类型:");
        System.out.println("    - 将数组中的每个对象作为独立文档索引");
        System.out.println("    - 保持数组元素间的关联关系");
        System.out.println("    - 适用于需要独立查询数组内对象的场景");
        System.out.println("\n  与 object 类型的区别:");
        System.out.println("    - object: 扁平化存储，丢失元素间关系");
        System.out.println("    - nested: 独立索引，保持元素间关系");

        System.out.println("\n  Java Client 创建嵌套索引:");
        System.out.println("    .mappings(m -> m");
        System.out.println("      .properties(\"properties\", p -> p.object(o -> o");
        System.out.println("        .properties(\"name\", pp -> pp.keyword(k -> k))");
        System.out.println("        .properties(\"value\", pp -> pp.double_(d -> d))");
        System.out.println("      ))");
    }

    /**
     * 嵌套查询
     *
     * REST API:
     *   GET /product/_search
     *   { "query": { "nested": { "path": "properties", "query": { "match": { "properties.name": "color" }}}}}
     */
    private void nestedQuery() throws IOException {
        ResponsePrinter.printMethodInfo("nestedQuery", "嵌套查询");

        // 1. 基本嵌套查询
        System.out.println("\n  1. 基本嵌套查询 (properties.name = \"内存\"):");
        SearchResponse<ProductDocument> response1 = client.search(s -> s
            .index(NESTED_INDEX)
            .query(q -> q.nested(n -> n
                .path("properties")
                .query(nq -> nq.match(m -> m
                    .field("properties.name")
                    .query("内存")
                ))
            ))
        , ProductDocument.class);

        printProductResults(response1, "嵌套查询结果");

        // 2. 多条件嵌套查询 (name=内存 AND value 包含"16")
        System.out.println("\n  2. 多条件嵌套查询 (name=内存 AND value 包含\"16\"):");
        try {
            SearchResponse<ProductDocument> response2 = client.search(s -> s
                .index(NESTED_INDEX)
                .query(q -> q.nested(n -> n
                    .path("properties")
                    .query(nq -> nq.bool(b -> b
                        .must(m -> m.match(mt -> mt.field("properties.name").query("内存")))
                        .filter(f -> f.match(mf -> mf.field("properties.value").query("16")))
                    ))
                ))
            , ProductDocument.class);

            printProductResults(response2, "多条件嵌套查询结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }

        // 3. 跨嵌套字段查询 (任一属性名为"颜色"或"内存")
        System.out.println("\n  3. 跨嵌套字段查询 (任一属性名为\"颜色\"或\"内存\"):");
        try {
            SearchResponse<ProductDocument> response3 = client.search(s -> s
                .index(NESTED_INDEX)
                .query(q -> q.nested(n -> n
                    .path("properties")
                    .query(nq -> nq.bool(b -> b
                        .should(s1 -> s1.match(m -> m.field("properties.name").query("颜色")))
                        .should(s2 -> s2.match(m -> m.field("properties.name").query("内存")))
                        .minimumShouldMatch("1")
                    ))
                ))
            , ProductDocument.class);

            printProductResults(response3, "跨字段查询结果");
        } catch (Exception e) {
            System.out.println("    [跳过] 查询执行失败: " + e.getMessage());
        }
    }

    /**
     * 嵌套聚合
     *
     * REST API:
     *   GET /product/_search
     *   { "size": 0, "aggs": { "properties_name": { "nested": { "path": "properties" }, "aggs": { "name_terms": { "terms": { "field": "properties.name" }}}}}}
     */
    private void nestedAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("nestedAggregation", "嵌套聚合");

        // 1. nested 聚合
        System.out.println("\n  1. nested 嵌套聚合:");
        System.out.println("    REST API: POST /product/_search");
        System.out.println("    {");
        System.out.println("      \"aggs\": {");
        System.out.println("        \"properties_nested\": { \"nested\": { \"path\": \"properties\" }},");
        System.out.println("        \"aggs\": { \"name_terms\": { \"terms\": { \"field\": \"properties.name\" }}}");
        System.out.println("      }");
        System.out.println("    }");

        // 2. 按嵌套属性值范围聚合
        System.out.println("\n  2. 按属性值范围聚合:");
        System.out.println("    REST API: POST /product/_search");
        System.out.println("    {");
        System.out.println("      \"aggs\": {");
        System.out.println("        \"properties_nested\": { \"nested\": { \"path\": \"properties\" }},");
        System.out.println("        \"aggs\": { \"value_ranges\": { \"range\": { \"field\": \"properties.value\", \"ranges\": [");
        System.out.println("          { \"key\": \"small\", \"to\": 8 },");
        System.out.println("          { \"key\": \"medium\", \"from\": 8, \"to\": 16 },");
        System.out.println("          { \"key\": \"large\", \"from\": 16 }");
        System.out.println("        ]}}}");
        System.out.println("    }");

        System.out.println("\n  注意: 嵌套聚合需要在嵌套索引上执行，");
        System.out.println("        请确保 'product' 索引已创建并包含 nested 类型映射");
    }

    /**
     * 打印产品查询结果
     */
    private void printProductResults(SearchResponse<ProductDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("    " + description + "，命中: " + total);

        for (Hit<ProductDocument> hit : response.hits().hits()) {
            ProductDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getName() + " - " + doc.getCategory());
            }
        }
    }

    /**
     * 产品文档实体（嵌套类型示例）
     */
    public static class ProductDocument {
        private String id;
        private String name;
        private String category;
        private Double price;
        private List<Property> properties;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public List<Property> getProperties() { return properties; }
        public void setProperties(List<Property> properties) { this.properties = properties; }

        public static ProductDocumentBuilder builder() { return new ProductBuilder(); }

        public interface ProductDocumentBuilder {
            ProductDocumentBuilder id(String id);
            ProductDocumentBuilder name(String name);
            ProductDocumentBuilder category(String category);
            ProductDocumentBuilder price(Double price);
            ProductDocumentBuilder properties(List<Property> properties);
            ProductDocument build();
        }

        public static class Property {
            private String name;
            private Double value;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
            public Double getValue() { return value; }
            public void setValue(Double value) { this.value = value; }
        }

        public static class ProductBuilder implements ProductDocumentBuilder {
            private final ProductDocument doc = new ProductDocument();

            public ProductDocumentBuilder id(String id) { doc.setId(id); return this; }
            public ProductDocumentBuilder name(String name) { doc.setName(name); return this; }
            public ProductDocumentBuilder category(String category) { doc.setCategory(category); return this; }
            public ProductDocumentBuilder price(Double price) { doc.setPrice(price); return this; }
            public ProductDocumentBuilder properties(List<Property> properties) { doc.setProperties(properties); return this; }
            public ProductDocument build() { return doc; }
        }
    }
}
