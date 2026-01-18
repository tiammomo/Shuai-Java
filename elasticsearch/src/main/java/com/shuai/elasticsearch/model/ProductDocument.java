package com.shuai.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 产品文档实体类
 * <p>
 * 模块概述
 * ----------
 * 用于演示 Elasticsearch nested 嵌套查询功能。
 * 产品包含多个属性 (properties)，每个属性有名称和值，
 * 需要使用 nested 类型来保持属性间的关联关系。
 * <p>
 * 核心内容
 * ----------
 *   - 产品基本信息 (id, name, category, price)
 *   - 产品属性列表 (properties) - nested 类型
 *   - 标签和状态
 * <p>
 * 使用示例
 * ----------
 * {@code
 * ProductDocument product = ProductDocument.builder()
 *     .id("P001")
 *     .name("笔记本电脑")
 *     .category("电子产品")
 *     .price(5999.00)
 *     .properties(List.of(
 *         new ProductDocument.ProductProperty("内存", "16GB"),
 *         new ProductDocument.ProductProperty("硬盘", "512GB"),
 *         new ProductDocument.ProductProperty("CPU", "i7")
 *     ))
 *     .build();
 * }
 * <p>
 * 索引映射
 * ----------
 * {
 *   "properties": {
 *     "id": { "type": "keyword" },
 *     "name": { "type": "text", "analyzer": "ik_max_word" },
 *     "category": { "type": "keyword" },
 *     "price": { "type": "double" },
 *     "properties": {
 *       "type": "nested",
 *       "properties": {
 *         "name": { "type": "keyword" },
 *         "value": { "type": "keyword" }
 *       }
 *     }
 *   }
 * }
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-18
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/nested.html">ES Nested Query</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDocument {

    /**
     * 产品 ID
     */
    @JsonProperty("id")
    private String id;

    /**
     * 产品名称
     */
    @JsonProperty("name")
    private String name;

    /**
     * 产品分类
     */
    @JsonProperty("category")
    private String category;

    /**
     * 产品价格
     */
    @JsonProperty("price")
    private Double price;

    /**
     * 产品属性列表 - nested 类型
     * <p>
     * 每个产品可以有多个属性，如内存大小、硬盘容量、CPU型号等。
     * 使用 nested 类型可以独立查询每个属性的值。
     */
    @JsonProperty("properties")
    private List<ProductProperty> properties;

    /**
     * 产品标签
     */
    @JsonProperty("tags")
    private List<String> tags;

    /**
     * 产品状态 (在售/下架/预售)
     */
    @JsonProperty("status")
    private String status;

    /**
     * 库存数量
     */
    @JsonProperty("stock")
    private Integer stock;

    /**
     * 评分 (1-5)
     */
    @JsonProperty("rating")
    private Double rating;

    /**
     * 产品属性内部类
     * <p>
     * 用于存储产品属性的名称和值。
     * 例如: name="内存", value="16GB"
     */
    @Data
    @Builder
    @NoArgsConstructor
    public static class ProductProperty {

        /**
         * 属性名称
         */
        @JsonProperty("name")
        private String name;

        /**
         * 属性值
         */
        @JsonProperty("value")
        private String value;

        /**
         * 属性类型 (string/number/boolean)
         */
        @JsonProperty("type")
        private String type;

        /**
         * 构造函数 - 简化的字符串属性
         *
         * @param name  属性名
         * @param value 属性值
         */
        public ProductProperty(String name, String value) {
            this.name = name;
            this.value = value;
            this.type = "string";
        }

        /**
         * 构造函数 - 带类型的属性
         *
         * @param name  属性名
         * @param value 属性值
         * @param type  属性类型
         */
        public ProductProperty(String name, String value, String type) {
            this.name = name;
            this.value = value;
            this.type = type;
        }

        @Override
        public String toString() {
            return String.format("%s: %s", name, value);
        }
    }

    /**
     * 获取属性值的便捷方法
     *
     * @param propertyName 属性名称
     * @return 属性值，未找到返回 null
     */
    public String getPropertyValue(String propertyName) {
        if (properties == null || propertyName == null) {
            return null;
        }
        return properties.stream()
            .filter(p -> propertyName.equals(p.getName()))
            .findFirst()
            .map(ProductProperty::getValue)
            .orElse(null);
    }

    /**
     * 检查产品是否有特定标签
     *
     * @param tag 标签
     * @return 是否包含该标签
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    /**
     * 产品是否在售
     *
     * @return 是否在售状态
     */
    public boolean isOnSale() {
        return "on_sale".equals(status);
    }

    /**
     * 库存是否充足
     *
     * @param threshold 阈值
     * @return 库存是否充足
     */
    public boolean hasEnoughStock(int threshold) {
        return stock != null && stock >= threshold;
    }
}
