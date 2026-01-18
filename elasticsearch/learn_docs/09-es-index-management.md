# 索引管理

> 本章讲解 Elasticsearch 的索引管理操作，包括索引生命周期、别名、模板和索引优化。

## 学习目标

完成本章学习后，你将能够：
- 掌握索引的创建、修改和删除
- 理解和使用索引别名
- 配置索引模板
- 实现索引生命周期管理

## 1. 索引操作

### 1.1 创建索引

```bash
# 创建索引 (完整配置)
PUT /blog
{
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "refresh_interval": "1s",
    "max_result_window": 100000
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "content": { "type": "text" },
      "author": { "type": "keyword" },
      "views": { "type": "integer" },
      "createTime": { "type": "date" }
    }
  }
}

# 仅创建映射
PUT /blog
{
  "mappings": {
    "properties": { ... }
  }
}
```

### 1.2 查看索引信息

```bash
# 查看索引信息
GET /blog

# 查看多个索引
GET /blog,product/_settings

# 查看所有索引
GET /_cat/indices?v

# 查看索引映射
GET /blog/_mapping

# 查看索引统计
GET /blog/_stats
```

### 1.3 修改索引

```bash
# 修改副本数
PUT /blog/_settings
{
  "number_of_replicas": 2
}

# 修改刷新间隔
PUT /blog/_settings
{
  "refresh_interval": "5s"
}

# 暂停刷新 (批量导入时)
PUT /blog/_settings
{
  "refresh_interval": "-1"
}

# 注意: 以下设置不可修改
# - number_of_shards
# - index.uuid
# - _id
```

### 1.4 删除索引

```bash
# 删除单个索引
DELETE /blog

# 删除多个索引
DELETE /blog,product

# 按模式删除
DELETE /blog-*

# 删除全部 (危险!)
DELETE /*
```

## 2. 索引别名

### 2.1 基本操作

```bash
# 创建别名
POST /_aliases
{
  "actions": [
    { "add": { "index": "blog", "alias": "blog_alias" }}
  ]
}

# 创建索引并添加别名
PUT /blog-v1
{
  "aliases": {
    "blog_current": {}
  }
}

# 查看别名
GET /blog/_alias

# 查看所有别名
GET /_alias
```

### 2.2 复杂操作

```bash
# 为多个索引添加别名
POST /_aliases
{
  "actions": [
    { "add": { "index": "blog-2024-01", "alias": "blog_current" }},
    { "add": { "index": "blog-2024-02", "alias": "blog_current" }}
  ]
}

# 移除别名
POST /_aliases
{
  "actions": [
    { "remove": { "index": "blog-2023", "alias": "blog_current" }}
  ]
}

# 原子切换别名
POST /_aliases
{
  "actions": [
    { "remove": { "index": "blog-v1", "alias": "blog_current" }},
    { "add": { "index": "blog-v2", "alias": "blog_current" }}
  ]
}
```

### 2.3 别名过滤

```bash
# 带过滤的别名
PUT /blog-2024
{
  "aliases": {
    "blog_active": {
      "filter": {
        "term": { "status": "published" }
      }
    }
  }
}
```

### 2.4 别名路由

```bash
# 带路由的别名
PUT /blog
{
  "aliases": {
    "blog": {
      "index_routing": "category",
      "search_routing": "category"
    }
  }
}
```

## 3. 索引模板

### 3.1 创建模板

```bash
# 创建索引模板
PUT /_template/blog_template
{
  "index_patterns": ["blog-*"],
  "order": 0,
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "author": { "type": "keyword" },
      "createTime": { "type": "date" }
    }
  }
}
```

### 3.2 模板优先级

```bash
# 通用模板 (优先级低)
PUT /_template/base_template
{
  "index_patterns": ["*"],
  "order": 0,
  "settings": {
    "number_of_shards": 1
  }
}

# 特定模板 (优先级高)
PUT /_template/blog_template
{
  "index_patterns": ["blog-*"],
  "order": 1,
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 2
  }
}
```

### 3.3 组件模板

```bash
# 创建组件模板
PUT /_component_template/mappings-component
{
  "template": {
    "mappings": {
      "properties": {
        "timestamp": { "type": "date" },
        "message": { "type": "text" }
      }
    }
  }
}

PUT /_component_template/settings-component
{
  "template": {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1
    }
  }
}

# 使用组件模板
PUT /_index_template/logs-template
{
  "index_patterns": ["logs-*"],
  "template": {
    "settings": {
      "index.number_of_shards": 3
    },
    "mappings": {
      "properties": {
        "timestamp": { "type": "date" }
      }
    }
  },
  "composed_of": ["mappings-component", "settings-component"]
}
```

## 4. 索引生命周期管理 (ILM)

### 4.1 策略配置

```bash
# 创建 ILM 策略
PUT /_ilm/policy blog-policy
{
  "policy": {
    "phases": {
      "hot": {
        "min_age": "0ms",
        "actions": {
          "rollover": {
            "max_age": "1d",
            "max_size": "50gb"
          },
          "set_priority": {
            "priority": 100
          }
        }
      },
      "warm": {
        "min_age": "7d",
        "actions": {
          "shrink": {
            "number_of_shards": 1
          },
          "forcemerge": {
            "max_num_segments": 1
          },
          "set_priority": {
            "priority": 50
          }
        }
      },
      "cold": {
        "min_age": "30d",
        "actions": {
          "freeze": {},
          "set_priority": {
            "priority": 0
          }
        }
      },
      "delete": {
        "min_age": "90d",
        "actions": {
          "delete": {}
        }
      }
    }
  }
}
```

### 4.2 应用策略

```bash
# 创建带 ILM 的索引模板
PUT /_index_template/blog-index-template
{
  "index_patterns": ["blog-*"],
  "template": {
    "settings": {
      "number_of_shards": 3,
      "number_of_replicas": 1,
      "index.lifecycle.name": "blog-policy",
      "index.lifecycle.rollover_alias": "blog-alias"
    },
    "mappings": {
      "properties": {
        "title": { "type": "text" },
        "content": { "type": "text" }
      }
    }
  }
}

# 初始化索引
PUT /blog-000001
{
  "aliases": {
    "blog-alias": {
      "is_write_index": true
    }
  }
}
```

### 4.3 ILM 操作

```bash
# 查看 ILM 状态
GET /_ilm/status

# 查看索引生命周期状态
GET /blog/_ilm/explain

# 手动执行 rollover
POST /blog-alias/_rollover

# 手动执行 policy
POST /blog/_ilm/retry
```

## 5. 索引优化

### 5.1 分片优化

```bash
# 查看分片分配
GET /_cat/shards?v

# 查看分片大小
GET /_cat/indices?v&h=index,pri,rep,docs.count,store.size

# 手动移动分片
POST /_cluster/reroute
{
  "commands": [
    {
      "move": {
        "index": "blog",
        "shard": 0,
        "from_node": "node-1",
        "to_node": "node-2"
      }
    }
  ]
}
```

### 5.2 索引压缩

```bash
# Force Merge (减少段数量)
POST /blog/_forcemerge

# Force Merge (只保留 1 个段)
POST /blog/_forcemerge?max_num_segments=1

# Shrink (减少分片数)
POST /blog-shrunk/_shrink/blog-shrunk
{
  "settings": {
    "index.number_of_shards": 1
  }
}
```

## 6. 实践部分

### 6.1 Java 客户端实现

```java
// IndexManagementDemo.java
package com.shuai.elasticsearch.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.*;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

public class IndexManagementDemo {

    private final ElasticsearchClient client;

    public IndexManagementDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有索引管理演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("IndexManagementDemo", "索引管理演示");

        createIndex();
        updateSettings();
        manageAliases();
        checkIndexStatus();
    }

    /**
     * 创建索引
     */
    private void createIndex() throws IOException {
        ResponsePrinter.printMethodInfo("createIndex", "创建索引");

        String indexName = "blog";

        boolean exists = client.indices().exists(e -> e.index(indexName)).value();
        if (!exists) {
            CreateIndexResponse response = client.indices().create(c -> c
                .index(indexName)
                .settings(s -> s
                    .numberOfShards(3)
                    .numberOfReplicas(1)
                    .refreshInterval(t -> t.time("1s"))
                )
                .mappings(m -> m
                    .properties("title", p -> p.text(t -> t.analyzer("ik_max_word")))
                    .properties("content", p -> p.text(t -> t.analyzer("ik_max_word")))
                    .properties("author", p -> p.keyword(k -> k))
                    .properties("views", p -> p.integer(i -> i))
                    .properties("createTime", p -> p.date(d -> d.format("yyyy-MM-dd||epoch_millis")))
                )
            );

            System.out.println("  索引创建: " + response.acknowledged());
        } else {
            System.out.println("  索引已存在: " + indexName);
        }
    }

    /**
     * 更新设置
     */
    private void updateSettings() throws IOException {
        ResponsePrinter.printMethodInfo("updateSettings", "更新设置");

        String indexName = "blog";

        UpdateIndexSettingsResponse response = client.indices().putSettings(p -> p
            .index(indexName)
            .settings(s -> s
                .numberOfReplicas(2)
                .refreshInterval(t -> t.time("5s"))
            )
        );

        System.out.println("  设置更新: " + response.acknowledged());
    }

    /**
     * 别名管理
     */
    private void manageAliases() throws IOException {
        ResponsePrinter.printMethodInfo("manageAliases", "别名管理");

        // 添加别名
        client.indices().updateAliases(a -> a
            .add(ar -> ar.index("blog").alias("blog_alias"))
        );

        // 查看别名
        var aliasesResponse = client.indices().getAliases(g -> g.index("blog"));
        System.out.println("  别名: " + aliasesResponse.result().get("blog").aliases().keySet());

        // 移除别名
        client.indices().updateAliases(a -> a
            .add(ar -> ar.index("blog").alias("blog_alias").removeIndex(true))
        );
    }

    /**
     * 检查索引状态
     */
    private void checkIndexStatus() throws IOException {
        ResponsePrinter.printMethodInfo("checkIndexStatus", "检查索引状态");

        var stats = client.indices().stats(s -> s.index("blog"));
        System.out.println("  文档数: " + stats.all().primaries().docs().count());
        System.out.println("  存储大小: " + stats.all().primaries().store().sizeInBytes() + " bytes");
    }
}
```

### 6.2 批量索引操作

```bash
# Reindex (索引间复制)
POST /_reindex
{
  "source": {
    "index": "blog"
  },
  "dest": {
    "index": "blog-new"
  }
}

# Reindex (带过滤)
POST /_reindex
{
  "source": {
    "index": "blog",
    "query": {
      "range": { "views": { "gte": 1000 }}
    }
  },
  "dest": {
    "index": "blog-popular"
  }
}

# Clone Index
POST /blog/_clone/blog-clone
{
  "settings": {
    "index.number_of_shards": 5
  }
}
```

## 7. 常见问题

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 分片不均衡 | 热点数据 | 使用 shard routing |
| 索引无法删除 | 正在执行操作 | 等待或强制删除 |
| ILM 不生效 | policy 未绑定 | 检查 index_template 配置 |
| 别名冲突 | 同名别名已存在 | 先移除再添加 |

## 8. 扩展阅读

**代码位置**:
- [IndexManagementDemo.java](src/main/java/com/shuai/elasticsearch/index/IndexManagementDemo.java) - 索引管理演示
- [IndexConfig.java](src/main/java/com/shuai/elasticsearch/config/IndexConfig.java) - 索引配置类
- [DataInitUtil.java](src/main/java/com/shuai/elasticsearch/util/DataInitUtil.java) - 数据初始化工具

- [Index Modules](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-modules.html)
- [Index Aliases](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-add-alias.html)
- [Index Templates](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-templates.html)
- [ILM](https://www.elastic.co/guide/en/elasticsearch/reference/current/index-lifecycle-management.html)
- [上一章: 地理查询](08-es-geo-query.md) | [下一章: Milvus 入门](10-milvus-introduction.md)

---

**学习进度**: ⬜⬜⬜⬜⬜⬜⬜⬜ 0/18 文档完成

**最后更新**: 2026-01-17
