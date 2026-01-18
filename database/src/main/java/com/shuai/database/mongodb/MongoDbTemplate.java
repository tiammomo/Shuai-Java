package com.shuai.database.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB 操作模板类
 * 提供 CRUD 操作的封装
 */
public class MongoDbTemplate {

    private final String collectionName;

    public MongoDbTemplate(String collectionName) {
        this.collectionName = collectionName;
    }

    /**
     * 获取集合
     */
    private MongoCollection<Document> getCollection() {
        return MongoDbConfig.getCollection(collectionName);
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单个文档
     */
    public InsertOneResult insert(Document doc) {
        return getCollection().insertOne(doc);
    }

    /**
     * 批量插入文档
     */
    public InsertManyResult insertMany(List<Document> docs) {
        return getCollection().insertMany(docs);
    }

    /**
     * 插入或更新（根据 _id）
     */
    public boolean save(Document doc) {
        if (doc.containsKey("_id")) {
            return replaceById(doc.getObjectId("_id"), doc).isPresent();
        } else {
            insert(doc);
            return true;
        }
    }

    // ==================== 查询操作 ====================

    /**
     * 查询所有文档
     */
    public List<Document> findAll() {
        List<Document> results = new ArrayList<>();
        getCollection().find().into(results);
        return results;
    }

    /**
     * 根据条件查询
     */
    public List<Document> find(Bson filter) {
        List<Document> results = new ArrayList<>();
        getCollection().find(filter).into(results);
        return results;
    }

    /**
     * 根据条件查询（带排序）
     */
    public List<Document> find(Bson filter, Bson sort) {
        List<Document> results = new ArrayList<>();
        getCollection().find(filter).sort(sort).into(results);
        return results;
    }

    /**
     * 查询单个文档
     */
    public Optional<Document> findOne(Bson filter) {
        Document doc = getCollection().find(filter).first();
        return Optional.ofNullable(doc);
    }

    /**
     * 根据 ID 查询
     */
    public Optional<Document> findById(Object id) {
        return findOne(Filters.eq("_id", id));
    }

    /**
     * 统计数量
     */
    public long count() {
        return getCollection().countDocuments();
    }

    /**
     * 根据条件统计数量
     */
    public long count(Bson filter) {
        return getCollection().countDocuments(filter);
    }

    /**
     * 检查是否存在
     */
    public boolean exists(Bson filter) {
        return count(filter) > 0;
    }

    /**
     * 分页查询
     */
    public List<Document> findPage(Bson filter, int skip, int limit) {
        List<Document> results = new ArrayList<>();
        getCollection().find(filter != null ? filter : new Document())
                .skip(skip)
                .limit(limit)
                .into(results);
        return results;
    }

    /**
     * 游标遍历
     */
    public void forEach(Bson filter, DocumentConsumer consumer) {
        try (MongoCursor<Document> cursor = getCollection().find(filter).iterator()) {
            while (cursor.hasNext()) {
                consumer.accept(cursor.next());
            }
        }
    }

    // ==================== 更新操作 ====================

    /**
     * 更新单个文档
     */
    public Optional<Document> updateOne(Bson filter, Bson update) {
        UpdateResult result = getCollection().updateOne(filter, update);
        if (result.getModifiedCount() > 0) {
            return findOne(filter);
        }
        return Optional.empty();
    }

    /**
     * 更新多个文档
     */
    public long updateMany(Bson filter, Bson update) {
        UpdateResult result = getCollection().updateMany(filter, update);
        return result.getModifiedCount();
    }

    /**
     * 替换文档
     */
    public Optional<Document> replace(Bson filter, Document replacement) {
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        getCollection().replaceOne(filter, replacement, options);
        return findOne(replacement);
    }

    /**
     * 根据 ID 替换
     */
    public Optional<Document> replaceById(Object id, Document replacement) {
        return replace(Filters.eq("_id", id), replacement);
    }

    /**
     * 查找并更新
     */
    public Optional<Document> findOneAndUpdate(Bson filter, Bson update) {
        Document doc = getCollection().findOneAndUpdate(filter, update);
        return Optional.ofNullable(doc);
    }

    /**
     * 查找并替换
     */
    public Optional<Document> findOneAndReplace(Bson filter, Document replacement) {
        Document doc = getCollection().findOneAndReplace(filter, replacement);
        return Optional.ofNullable(doc);
    }

    /**
     * 查找并删除
     */
    public Optional<Document> findOneAndDelete(Bson filter) {
        Document doc = getCollection().findOneAndDelete(filter);
        return Optional.ofNullable(doc);
    }

    /**
     * 设置字段值
     */
    public long set(Bson filter, String field, Object value) {
        return updateMany(filter, new Document("$set", new Document(field, value)));
    }

    /**
     * 递增字段值
     */
    public long inc(Bson filter, String field, Number amount) {
        return updateMany(filter, new Document("$inc", new Document(field, amount)));
    }

    /**
     * 数组追加元素
     */
    public long push(Bson filter, String arrayField, Object value) {
        return updateMany(filter, new Document("$push", new Document(arrayField, value)));
    }

    /**
     * 数组移除元素
     */
    public long pull(Bson filter, String arrayField, Object value) {
        return updateMany(filter, new Document("$pull", new Document(arrayField, value)));
    }

    // ==================== 删除操作 ====================

    /**
     * 删除单个文档
     */
    public long deleteOne(Bson filter) {
        DeleteResult result = getCollection().deleteOne(filter);
        return result.getDeletedCount();
    }

    /**
     * 删除多个文档
     */
    public long deleteMany(Bson filter) {
        DeleteResult result = getCollection().deleteMany(filter);
        return result.getDeletedCount();
    }

    /**
     * 根据 ID 删除
     */
    public long deleteById(Object id) {
        return deleteOne(Filters.eq("_id", id));
    }

    /**
     * 清空集合
     */
    public long deleteAll() {
        return deleteMany(new Document());
    }

    // ==================== 索引操作 ====================

    /**
     * 创建单字段索引
     */
    public void createIndex(String field) {
        getCollection().createIndex(Indexes.ascending(field));
    }

    /**
     * 创建多字段索引
     */
    public void createCompoundIndex(String... fields) {
        List<Bson> indexList = new ArrayList<>();
        for (String field : fields) {
            indexList.add(Indexes.ascending(field));
        }
        getCollection().createIndex(Indexes.compoundIndex(indexList));
    }

    /**
     * 创建文本索引
     */
    public void createTextIndex(String... fields) {
        getCollection().createIndex(Indexes.text(
                String.join(" ", fields)
        ));
    }

    /**
     * 删除索引
     */
    public void dropIndex(String indexName) {
        getCollection().dropIndex(indexName);
    }

    /**
     * 删除所有索引
     */
    public void dropIndexes() {
        getCollection().dropIndexes();
    }

    /**
     * 获取集合名称
     */
    public String getCollectionName() {
        return collectionName;
    }

    /**
     * 函数式接口：文档消费者
     */
    @FunctionalInterface
    public interface DocumentConsumer {
        void accept(Document doc);
    }
}
