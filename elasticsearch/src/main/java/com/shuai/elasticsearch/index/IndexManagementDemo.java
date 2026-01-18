package com.shuai.elasticsearch.index;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import com.shuai.elasticsearch.config.ElasticsearchConfig;
import com.shuai.elasticsearch.util.ResponsePrinter;

import java.io.IOException;

/**
 * 索引管理演示类
 *
 * 模块概述
 * ----------
 * 本模块演示 Elasticsearch 索引管理操作。
 *
 * 核心内容
 * ----------
 *   - 索引创建: 自定义 mapping 和 settings
 *   - 索引查询: 查看索引信息、mapping、settings
 *   - 索引删除: 删除指定索引
 *   - 索引别名: alias 管理
 *
 * @author Shuai
 * @version 1.0
 * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html">Index Management</a>
 */
public class IndexManagementDemo {

    private final ElasticsearchClient client;

    public IndexManagementDemo() {
        this.client = ElasticsearchConfig.getClient();
    }

    /**
     * 运行所有索引管理演示
     */
    public void runAllDemos() throws IOException {
        ResponsePrinter.printMethodInfo("索引管理演示", "Elasticsearch 索引管理功能");

        indexOverview();
        createIndex();
        getIndexInfo();
        updateIndexSettings();
        indexAliasOperations();
    }

    /**
     * 索引管理概述
     */
    private void indexOverview() {
        ResponsePrinter.printMethodInfo("indexOverview", "索引管理概述");

        System.out.println("  索引管理操作:");
        System.out.println("    - 创建索引: createIndex()");
        System.out.println("    - 查询索引: getIndex()");
        System.out.println("    - 删除索引: deleteIndex()");
        System.out.println("    - 更新设置: updateSettings()");
        System.out.println("    - 别名管理: addAlias(), removeAlias()");

        System.out.println("\n  索引结构:");
        System.out.println("    - Settings: 分片数、副本数、分析器");
        System.out.println("    - Mapping: 字段类型、字段属性");
        System.out.println("    - Aliases: 索引别名");

        System.out.println("\n  最佳实践:");
        System.out.println("    - 合理设置分片数 (1-3)");
        System.out.println("    - 副本数根据可用性需求设置");
        System.out.println("    - 字段类型选择合适的类型");
        System.out.println("    - 为文本字段选择合适的分词器");
    }

    /**
     * 创建索引
     */
    private void createIndex() throws IOException {
        ResponsePrinter.printMethodInfo("createIndex", "创建索引");

        String indexName = "demo_index";

        System.out.println("\n  1. 创建索引 (包含自定义 mapping 和 settings):");
        System.out.println("    REST API: PUT /demo_index");
        System.out.println("    {");
        System.out.println("      \"settings\": { \"number_of_shards\": 1, \"number_of_replicas\": 0 },");
        System.out.println("      \"mappings\": {");
        System.out.println("        \"properties\": {");
        System.out.println("          \"title\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },");
        System.out.println("          \"content\": { \"type\": \"text\", \"analyzer\": \"ik_smart\" },");
        System.out.println("          \"status\": { \"type\": \"keyword\" },");
        System.out.println("          \"views\": { \"type\": \"long\" }");
        System.out.println("        }");
        System.out.println("      }");
        System.out.println("    }");

        // 检查索引是否存在
        ExistsRequest existsRequest = ExistsRequest.of(e -> e.index(indexName));
        if (client.indices().exists(existsRequest).value()) {
            System.out.println("    索引已存在，跳过创建");
            return;
        }

        // 创建索引
        CreateIndexRequest request = CreateIndexRequest.of(c -> c
            .index(indexName)
            .settings(s -> s
                .numberOfShards("1")
                .numberOfReplicas("0")
            )
            .mappings(m -> m
                .properties("title", p -> p.text(t -> t.analyzer("ik_max_word")))
                .properties("content", p -> p.text(t -> t.analyzer("ik_smart")))
                .properties("status", p -> p.keyword(k -> k))
                .properties("views", p -> p.long_(l -> l))
                .properties("tags", p -> p.keyword(k -> k))
            )
        );

        client.indices().create(request);
        System.out.println("    索引创建成功: " + indexName);

        System.out.println("\n  2. 创建带别名的索引:");
        String aliasName = "demo_alias";
        CreateIndexRequest requestWithAlias = CreateIndexRequest.of(c -> c
            .index(indexName + "_alias")
            .settings(s -> s.numberOfShards("1").numberOfReplicas("0"))
            .mappings(m -> m
                .properties("name", p -> p.text(t -> t))
                .properties("category", p -> p.keyword(k -> k))
            )
            .aliases(aliasName, a -> a)
        );

        client.indices().create(requestWithAlias);
        System.out.println("    索引创建成功: " + indexName + "_alias (别名: " + aliasName + ")");
    }

    /**
     * 获取索引信息
     */
    private void getIndexInfo() throws IOException {
        ResponsePrinter.printMethodInfo("getIndexInfo", "获取索引信息");

        String indexName = "blog";

        System.out.println("\n  1. 获取索引 mapping:");
        System.out.println("    REST API: GET /" + indexName + "/_mapping");

        var mappingResponse = client.indices().getMapping(g -> g.index(indexName));
        System.out.println("    索引名称: " + indexName);

        if (mappingResponse.result().containsKey(indexName)) {
            var mappings = mappingResponse.result().get(indexName).mappings();
            if (mappings != null) {
                System.out.println("    字段数量: " + mappings.properties().size());
            }
        }

        System.out.println("\n  2. 获取索引 settings:");
        System.out.println("    REST API: GET /" + indexName + "/_settings");

        var settingsResponse = client.indices().getSettings(g -> g.index(indexName));
        var indexSettings = settingsResponse.get(indexName);
        if (indexSettings != null && indexSettings.settings() != null) {
            var settings = indexSettings.settings();
            System.out.println("    分片数: " + settings.numberOfShards());
            System.out.println("    副本数: " + settings.numberOfReplicas());
        }

        System.out.println("\n  3. 获取索引 stats:");
        System.out.println("    REST API: GET /" + indexName + "/_stats");

        var statsResponse = client.indices().stats(s -> s.index(indexName));
        var primaries = statsResponse.all().primaries();
        if (primaries != null && primaries.docs() != null) {
            System.out.println("    文档数: " + primaries.docs().count());
        }
        if (primaries != null && primaries.store() != null) {
            System.out.println("    存储大小: " + primaries.store().sizeInBytes() / 1024 + " KB");
        }
    }

    /**
     * 更新索引设置
     */
    private void updateIndexSettings() throws IOException {
        ResponsePrinter.printMethodInfo("updateIndexSettings", "更新索引设置");

        String indexName = "demo_index";

        System.out.println("\n  1. 更新索引设置:");
        System.out.println("    REST API: PUT /" + indexName + "/_settings");
        System.out.println("    { \"number_of_replicas\": 1 }");

        // 动态更新索引设置
        client.indices().putSettings(p -> p
            .index(indexName)
            .settings(s -> s.numberOfReplicas("1"))
        );

        System.out.println("    副本数已更新为: 1");

        System.out.println("\n  2. 刷新索引 (使变更生效):");
        System.out.println("    REST API: POST /" + indexName + "/_refresh");
        client.indices().refresh(r -> r.index(indexName));
        System.out.println("    索引已刷新");
    }

    /**
     * 索引别名操作
     */
    private void indexAliasOperations() throws IOException {
        ResponsePrinter.printMethodInfo("indexAliasOperations", "索引别名操作");

        String indexName = "blog";
        String aliasName = "blog_latest";

        System.out.println("\n  1. 创建别名:");
        System.out.println("    REST API: POST /_aliases");
        System.out.println("    { \"actions\": [ { \"add\": { \"index\": \"" + indexName + "\", \"alias\": \"" + aliasName + "\" } } ] }");

        client.indices().updateAliases(u -> u
            .actions(a -> a
                .add(add -> add.index(indexName).alias(aliasName))
            )
        );

        System.out.println("    别名创建成功: " + aliasName + " -> " + indexName);

        System.out.println("\n  2. 查询别名:");
        System.out.println("    REST API: GET /" + indexName + "/_alias");

        var aliasResponse = client.indices().getAlias(g -> g.index(indexName));
        var indexAliasResult = aliasResponse.get(indexName);
        if (indexAliasResult != null) {
            var aliases = indexAliasResult.aliases();
            if (aliases != null) {
                aliases.forEach((name, alias) -> System.out.println("    别名: " + name));
            }
        }

        System.out.println("\n  3. 删除别名:");
        System.out.println("    REST API: DELETE /" + indexName + "/_alias/" + aliasName);

        client.indices().updateAliases(u -> u
            .actions(a -> a
                .remove(remove -> remove.index(indexName).alias(aliasName))
            )
        );

        System.out.println("    别名已删除: " + aliasName);
    }
}
