package com.shuai.database.leveldb;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.Snapshot;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * LevelDB 模板类，封装常用操作
 *
 * @author Shuai
 */
public class LevelDbTemplate implements Closeable {

    private final LevelDbConfig config;
    private DB db;

    public LevelDbTemplate(LevelDbConfig config) throws IOException {
        this.config = config;
        config.cleanup();
        this.db = JniDBFactory.factory.open(new File(config.getDbPath()), config.getOptions());
    }

    // ==================== 基本操作 ====================

    /**
     * 写入数据
     */
    public void put(String key, String value) throws IOException {
        db.put(bytes(key), bytes(value));
    }

    /**
     * 写入数据（带 WriteOptions）
     */
    public void put(String key, String value, WriteOptions writeOptions) throws IOException {
        db.put(bytes(key), bytes(value), writeOptions);
    }

    /**
     * 读取数据
     */
    public String get(String key) throws IOException {
        byte[] value = db.get(bytes(key));
        return value != null ? asString(value) : null;
    }

    /**
     * 使用快照读取数据
     */
    public String get(String key, Snapshot snapshot) throws IOException {
        ReadOptions readOptions = new ReadOptions();
        readOptions.snapshot(snapshot);
        byte[] value = db.get(bytes(key), readOptions);
        return value != null ? asString(value) : null;
    }

    /**
     * 删除数据
     */
    public void delete(String key) throws IOException {
        db.delete(bytes(key));
    }

    /**
     * 检查键是否存在
     */
    public boolean exists(String key) throws IOException {
        return db.get(bytes(key)) != null;
    }

    // ==================== 批量操作 ====================

    /**
     * 批量写入
     */
    public void write(WriteBatch batch) throws IOException {
        db.write(batch);
    }

    /**
     * 创建 WriteBatch
     */
    public WriteBatch createWriteBatch() {
        return db.createWriteBatch();
    }

    // ==================== 快照操作 ====================

    /**
     * 创建快照
     */
    public Snapshot getSnapshot() {
        return db.getSnapshot();
    }

    // ==================== 迭代器操作 ====================

    /**
     * 创建迭代器
     */
    public DBIterator iterator() {
        return db.iterator();
    }

    /**
     * 创建使用快照的迭代器
     */
    public DBIterator iterator(Snapshot snapshot) {
        ReadOptions readOptions = new ReadOptions();
        readOptions.snapshot(snapshot);
        return db.iterator(readOptions);
    }

    // ==================== 辅助方法 ====================

    private byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private String asString(byte[] value) {
        return new String(value, StandardCharsets.UTF_8);
    }

    /**
     * 关闭数据库
     */
    @Override
    public void close() throws IOException {
        if (db != null) {
            db.close();
            db = null;
        }
    }
}
