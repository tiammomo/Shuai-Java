package com.shuai.elasticsearch.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.collection.CollectionSchemaParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.index.CreateIndexParam;

import io.milvus.param.RpcStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Milvus 向量数据库客户端配置类
 * <p>
 * 模块概述
 * ----------
 * 本模块提供 Milvus 向量数据库的客户端连接管理和集合初始化功能，
 * 支持单例模式获取客户端实例。
 * <p>
 * 核心内容
 * ----------
 *   - 单例客户端管理: getClient() 获取连接实例
 *   - 集合初始化: initDocumentCollection() 创建文档向量集合
 *   - 连接状态检查: isConnected() 验证连接状态
 *   - 资源释放: close() 关闭连接
 * <p>
 * 集合结构 (document_embeddings)
 * ----------
 *   - id: Int64, 主键 (自动生成)
 *   - document_id: VarChar(64), 文档ID (关联 Elasticsearch)
 *   - content: VarChar(4096), 文档内容片段
 *   - vector: FloatVector(384), 文档向量嵌入
 *   - metadata: VarChar(2048), 元数据 JSON
 * <p>
 * 索引配置
 * ----------
 *   - 索引类型: IVF_FLAT
 *   - 距离度量: COSINE (余弦相似度)
 *   - nlist: 1024
 * <p>
 * 配置说明
 * ----------
 * 支持以下配置方式（优先级从高到低）：
 *   1. 环境变量: MILVUS_HOST, MILVUS_PORT
 *   2. 默认值: localhost:19530
 * <p>
 * 依赖服务
 * ----------
 *   - Milvus 2.5.10 (默认端口: 19530)
 *   - MinIO (对象存储，默认端口: 9000)
 *   - etcd (元数据存储，默认端口: 2379)
 *
 * @author Shuai
 * @version 1.0
 * @since 2026-01-17
 * @see <a href="https://milvus.io/docs">Milvus Documentation</a>
 */
public class MilvusConfig {

    private static MilvusServiceClient client;
    private static final String COLLECTION_NAME = "document_embeddings";
    private static final int VECTOR_DIM = 384; // 嵌入向量维度
    private static final String MILVUS_HOST = System.getenv("MILVUS_HOST") != null ?
        System.getenv("MILVUS_HOST") : "localhost";
    private static final int MILVUS_PORT = System.getenv("MILVUS_PORT") != null ?
        Integer.parseInt(System.getenv("MILVUS_PORT")) : 19530;

    /**
     * 获取 Milvus 客户端单例实例
     */
    public static synchronized MilvusServiceClient getClient() {
        if (client == null) {
            client = createClient();
        }
        return client;
    }

    /**
     * 创建 Milvus 客户端实例
     */
    private static MilvusServiceClient createClient() {
        ConnectParam connectParam = ConnectParam.newBuilder()
            .withHost(MILVUS_HOST)
            .withPort(MILVUS_PORT)
            .build();

        return new MilvusServiceClient(connectParam);
    }

    /**
     * 初始化文档向量集合
     *
     * 创建包含以下字段的集合：
     *   - id: 主键
     *   - document_id: 文档ID (用于关联 ES)
     *   - content: 文档内容片段
     *   - vector: 384维向量 (bge-small-zh 模型)
     *   - metadata: 元数据 (JSON)
     */
    public static void initDocumentCollection() {
        MilvusServiceClient milvusClient = getClient();

        // 检查集合是否存在
        R<Boolean> existsResponse = milvusClient.hasCollection(
            io.milvus.param.collection.HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build()
        );

        if (existsResponse.getData()) {
            System.out.println("  集合 [" + COLLECTION_NAME + "] 已存在");
            return;
        }

        // 创建字段列表
        List<FieldType> fields = new ArrayList<>();
        fields.add(FieldType.newBuilder()
            .withName("id")
            .withDataType(io.milvus.grpc.DataType.Int64)
            .withPrimaryKey(true)
            .withAutoID(true)
            .build());
        fields.add(FieldType.newBuilder()
            .withName("document_id")
            .withDataType(io.milvus.grpc.DataType.VarChar)
            .withMaxLength(64)
            .build());
        fields.add(FieldType.newBuilder()
            .withName("content")
            .withDataType(io.milvus.grpc.DataType.VarChar)
            .withMaxLength(4096)
            .build());
        fields.add(FieldType.newBuilder()
            .withName("vector")
            .withDataType(io.milvus.grpc.DataType.FloatVector)
            .withDimension(VECTOR_DIM)
            .build());
        fields.add(FieldType.newBuilder()
            .withName("metadata")
            .withDataType(io.milvus.grpc.DataType.VarChar)
            .withMaxLength(2048)
            .build());

        // 创建集合
        CreateCollectionParam createCollectionParam = CreateCollectionParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withDescription("RAG 文档向量集合")
            .withSchema(CollectionSchemaParam.newBuilder().withFieldTypes(fields).build())
            .build();

        R<RpcStatus> createResponse = milvusClient.createCollection(createCollectionParam);
        if (createResponse.getStatus() == R.Status.Success.getCode()) {
            System.out.println("  集合 [" + COLLECTION_NAME + "] 创建成功");
        } else {
            System.out.println("  集合创建失败: " + createResponse.getMessage());
            return;
        }

        // 创建索引 (IVF_FLAT)
        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withFieldName("vector")
            .withIndexType(IndexType.IVF_FLAT)
            .withMetricType(MetricType.COSINE)
            .withExtraParam("{\"nlist\":1024}")
            .build();

        R<RpcStatus> indexResponse = milvusClient.createIndex(createIndexParam);
        if (indexResponse.getStatus() == R.Status.Success.getCode()) {
            System.out.println("  索引创建成功");
        }

        // 加载集合到内存
        milvusClient.loadCollection(
            LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build()
        );

        System.out.println("  集合已加载到内存");
    }

    /**
     * 获取集合名称
     */
    public static String getCollectionName() {
        return COLLECTION_NAME;
    }

    /**
     * 获取向量维度
     */
    public static int getVectorDim() {
        return VECTOR_DIM;
    }

    /**
     * 检查连接状态
     */
    public static boolean isConnected() {
        if (client == null) {
            return false;
        }
        try {
            R<Boolean> response = client.hasCollection(
                io.milvus.param.collection.HasCollectionParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .build()
            );
            return response.getStatus() == R.Status.Success.getCode();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 关闭客户端连接并释放资源
     * <p>
     * 此方法应在应用程序关闭前调用，确保 Milvus 连接被正确关闭。
     */
    public static synchronized void close() {
        if (client != null) {
            client.close();
            client = null;
            System.out.println("  Milvus 客户端已关闭");
        }
    }
}
