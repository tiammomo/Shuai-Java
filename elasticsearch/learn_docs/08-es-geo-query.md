# 地理查询

> 本章讲解 Elasticsearch 的地理查询功能，实现基于位置的搜索和数据分析。

## 学习目标

完成本章学习后，你将能够：
- 理解 geo_point 和 geo_shape 数据类型
- 实现地理位置距离查询
- 实现地理边界查询
- 进行地理聚合分析

## 1. 地理数据类型

### 1.1 geo_point 类型

```json
{
  "mappings": {
    "properties": {
      "location": {
        "type": "geo_point"
      }
    }
  }
}
```

**geo_point 支持的格式**：

```bash
# 对象格式
{ "lat": 40.712, "lon": -74.006 }

# 字符串格式
"40.712, -74.006"

# geohash 格式
"dr5reg"

# 数组格式 (注意: [lon, lat])
[-74.006, 40.712]
```

### 1.2 geo_shape 类型

```json
{
  "mappings": {
    "properties": {
      "area": {
        "type": "geo_shape",
        "tree": "quadtree",
        "precision": "1m"
      },
      "route": {
        "type": "geo_shape",
        "tree": "geotree"
      }
    }
  }
}
```

**geo_shape 支持的形状**：

| 形状 | 说明 |
|------|------|
| `point` | 点 |
| `linestring` | 线 |
| `polygon` | 多边形 |
| `multipoint` | 多点 |
| `multilinestring` | 多线 |
| `multipolygon` | 多多边形 |
| `envelope` | 矩形 |
| `circle` | 圆 |

## 2. 地理位置查询

### 2.1 geo_distance 查询

```bash
# 距离查询 - 查找距离某点 N 范围内的文档
GET /store/_search
{
  "query": {
    "geo_distance": {
      "distance": "5km",
      "location": {
        "lat": 40.7128,
        "lon": -74.0060
      }
    }
  }
}

# 距离范围查询
GET /store/_search
{
  "query": {
    "geo_distance": {
      "distance": "5km",
      "location": {
        "lat": 40.7128,
        "lon": -74.0060
      }
    }
  }
}

# 优化: 使用 filter 上下文
GET /store/_search
{
  "query": {
    "bool": {
      "filter": {
        "geo_distance": {
          "distance": "5km",
          "location": {
            "lat": 40.7128,
            "lon": -74.0060
          }
        }
      }
    }
  }
}
```

### 2.2 geo_bounding_box 查询

```bash
# 边界框查询 - 查找矩形范围内的文档
GET /store/_search
{
  "query": {
    "geo_bounding_box": {
      "location": {
        "top_left": {
          "lat": 40.8,
          "lon": -74.1
        },
        "bottom_right": {
          "lat": 40.7,
          "lon": -73.9
        }
      }
    }
  }
}

# 使用 geohash
GET /store/_search
{
  "query": {
    "geo_bounding_box": {
      "location": {
        "wkt": "BBOX (-74.1, 40.7, 40.8, -73.9)"
      }
    }
  }
}
```

### 2.3 geo_polygon 查询

```bash
# 多边形查询
GET /store/_search
{
  "query": {
    "geo_polygon": {
      "location": {
        "points": [
          { "lat": 40.8, "lon": -74.1 },
          { "lat": 40.8, "lon": -73.9 },
          { "lat": 40.7, "lon": -73.9 },
          { "lat": 40.7, "lon": -74.1 }
        ]
      }
    }
  }
}
```

### 2.4 geo_shape 查询

```bash
# geo_shape 查询
GET /region/_search
{
  "query": {
    "geo_shape": {
      "location": {
        "shape": {
          "type": "circle",
          "radius": "5km",
          "coordinates": [-74.0060, 40.7128]
        },
        "relation": "within"
      }
    }
  }
}

# 关系类型
# - within: 形状完全在查询区域内
# - intersects: 形状与查询区域相交
# - disjoint: 形状与查询区域不相交
# - contains: 查询区域包含形状
```

## 3. 地理排序

### 3.1 按距离排序

```bash
# 按距离排序 (从近到远)
GET /store/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 40.7128,
          "lon": -74.0060
        },
        "order": "asc",
        "unit": "km"
      }
    }
  ]
}

# 按距离排序 (从远到近)
GET /store/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": "40.7128, -74.0060",
        "order": "desc",
        "unit": "km"
      }
    }
  ]
}
```

### 3.2 带距离信息的排序

```bash
# 返回距离信息
GET /store/_search
{
  "query": {
    "geo_distance": {
      "distance": "10km",
      "location": "40.7128, -74.0060"
    }
  },
  "sort": [
    {
      "_geo_distance": {
        "location": "40.7128, -74.0060",
        "order": "asc",
        "unit": "km",
        "mode": "min",
        "distance_type": "arc"
      }
    }
  ],
  "_source": ["name", "location"]
}
```

## 4. 地理聚合

### 4.1 geohash 聚合

```bash
# geohash 聚合 - 网格化展示
GET /store/_search
{
  "size": 0,
  "aggs": {
    "geo_hash": {
      "geohash_grid": {
        "field": "location",
        "precision": 5
      }
    }
  }
}

# precision 和 geohash 长度对应
# 1: ~5,000km x 5,000km
# 3: ~125km x 125km
# 5: ~5km x 5km
# 7: ~150m x 150m
# 9: ~5m x 5m
```

### 4.2 距离聚合

```bash
# 距离范围聚合
GET /store/_search
{
  "size": 0,
  "aggs": {
    "distance_ranges": {
      "geo_distance": {
        "field": "location",
        "origin": "40.7128, -74.0060",
        "ranges": [
          { "to": "1km" },
          { "from": "1km", "to": "5km" },
          { "from": "5km", "to": "10km" },
          { "from": "10km" }
        ]
      }
    }
  }
}
```

### 4.3 周边热力图

```bash
# 附近门店热力图
GET /store/_search
{
  "size": 0,
  "aggs": {
    "popular_stores": {
      "geohash_grid": {
        "field": "location",
        "precision": 6,
        "bounds": {
          "top_left": "40.9, -74.2",
          "bottom_right": "40.6, -73.8"
        }
      },
      "aggs": {
        "avg_rating": {
          "avg": { "field": "rating" }
        }
      }
    }
  }
}
```

## 5. 实践部分

### 5.1 创建地理索引

```bash
# 创建门店索引
PUT /store
{
  "mappings": {
    "properties": {
      "name": { "type": "text" },
      "location": { "type": "geo_point" },
      "type": { "type": "keyword" },
      "rating": { "type": "float" }
    }
  }
}

# 插入门店数据
PUT /store/_doc/1
{
  "name": "星巴克旗舰店",
  "location": { "lat": 40.7128, "lon": -74.0060 },
  "type": "cafe",
  "rating": 4.5
}

PUT /store/_doc/2
{
  "name": "中央公园店",
  "location": "40.7829, -73.9654",
  "type": "cafe",
  "rating": 4.8
}

PUT /store/_doc/3
{
  "name": "时代广场店",
  "location": [-73.9855, 40.7580],
  "type": "cafe",
  "rating": 4.2
}
```

### 5.2 Java 客户端实现

```java
// GeoQueryDemo.java
package com.shuai.elasticsearch.geo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.GeoDistanceType;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.model.StoreDocument;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

public class GeoQueryDemo {

    private final ElasticsearchClient client;
    private static final String INDEX_NAME = "store";

    public GeoQueryDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有地理查询演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("GeoQueryDemo", "地理查询演示");

        geoDistanceQuery();
        geoBoundingBoxQuery();
        geoSortByDistance();
        geoAggregation();
    }

    /**
     * geo_distance 查询
     */
    private void geoDistanceQuery() throws IOException {
        ResponsePrinter.printMethodInfo("geoDistanceQuery", "距离查询");

        SearchResponse<StoreDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .geoDistance(gd -> gd
                    .field("location")
                    .distance("5km")
                    .location(loc -> loc.latlon(ll -> ll.lat(40.7128).lon(-74.0060)))
                )
            )
        , StoreDocument.class);

        printResults(response, "5km 范围内");
    }

    /**
     * geo_bounding_box 查询
     */
    private void geoBoundingBoxQuery() throws IOException {
        ResponsePrinter.printMethodInfo("geoBoundingBoxQuery", "边界框查询");

        SearchResponse<StoreDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q
                .geoBoundingBox(bb -> bb
                    .field("location")
                    .boundaries(b -> b
                        .tlbr(tlbr -> tlbr
                            .tl(ll -> ll.lat(40.8).lon(-74.1))
                            .br(ll -> ll.lat(40.7).lon(-73.9))
                        )
                    )
                )
            )
        , StoreDocument.class);

        printResults(response, "边界框内");
    }

    /**
     * 按距离排序
     */
    private void geoSortByDistance() throws IOException {
        ResponsePrinter.printMethodInfo("geoSortByDistance", "按距离排序");

        SearchResponse<StoreDocument> response = client.search(s -> s
            .index(INDEX_NAME)
            .query(q -> q.matchAll(m -> m))
            .sort(so -> so
                .geoDistance(gs -> gs
                    .field("location")
                    .location(loc -> loc.latlon(ll -> ll.lat(40.7128).lon(-74.0060)))
                    .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                    .unit(co.elastic.clients.elasticsearch._types.DistanceUnit.Kilometers)
                )
            )
        , StoreDocument.class);

        System.out.println("  按距离排序结果:");
        for (Hit<StoreDocument> hit : response.hits().hits()) {
            StoreDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getName() +
                    " (距离: " + String.format("%.2f", hit.sort().get(0).doubleValue()) + "km)");
            }
        }
    }

    /**
     * 地理聚合
     */
    private void geoAggregation() throws IOException {
        ResponsePrinter.printMethodInfo("geoAggregation", "地理聚合");

        SearchResponse<Void> response = client.search(s -> s
            .index(INDEX_NAME)
            .size(0)
            .aggregations("store_clusters", a -> a
                .geohashGrid(gh -> gh
                    .field("location")
                    .precision(5)
                )
            )
        , Void.class);

        System.out.println("  Geohash 聚合结果:");
        var buckets = response.aggregations().get("store_clusters")
            .geohashGrid().buckets().array();
        for (var bucket : buckets) {
            System.out.println("    " + bucket.keyAsString() + ": " + bucket.docCount() + " 个");
        }
    }

    private void printResults(SearchResponse<StoreDocument> response, String description) {
        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        System.out.println("  " + description + "，命中: " + total);

        for (Hit<StoreDocument> hit : response.hits().hits()) {
            StoreDocument doc = hit.source();
            if (doc != null) {
                System.out.println("    [" + hit.id() + "] " + doc.getName() +
                    " (评分: " + doc.getRating() + ")");
            }
        }
    }
}
```

### 5.3 模型类定义

```java
// StoreDocument.java
package com.shuai.elasticsearch.model;

public class StoreDocument {

    private String id;
    private String name;
    private Location location;
    private String type;
    private Double rating;

    public static class Location {
        private Double lat;
        private Double lon;

        // getters and setters
    }

    // getters and setters
}
```

## 6. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| geo_point 格式错误 | lat/lon 顺序 | 注意: lat 在前 |
| 距离计算慢 | 大数据量 | 使用 filter 上下文 |
| geo_shape 性能差 | precision 设置不当 | 调整 tree 和 precision |
| 排序不稳定 | 距离精度 | 使用 distance_type: arc |

## 7. 扩展阅读

**代码位置**:
- [GeoQueryDemo.java](src/main/java/com/shuai/elasticsearch/geo/GeoQueryDemo.java) - 地理查询演示
- [GeoQueryTest.java](src/test/java/com/shuai/elasticsearch/geo/GeoQueryTest.java) - 地理查询测试
- [LocationDocument.java](src/main/java/com/shuai/elasticsearch/model/LocationDocument.java) - 地理位置文档模型

- [Geo Queries](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-queries.html)
- [Geo-point Datatype](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-point.html)
- [Geo-shape Datatype](https://www.elastic.co/guide/en/elasticsearch/reference/current/geo-shape.html)
- [Geo Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket-geohashgrid-aggregation.html)
- [上一章: 全文检索](07-es-fulltext-search.md) | [下一章: 索引管理](09-es-index-management.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
