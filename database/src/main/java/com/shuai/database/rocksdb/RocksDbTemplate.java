package com.shuai.database.rocksdb;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.Snapshot;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteOptions;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * RocksDB 模板类，封装常用操作
 *
 * @author Shuai
 */
public class RocksDbTemplate implements Closeable {

    private final RocksDbConfig config;
    private final RocksDB db;
    private final List<ColumnFamilyHandle> columnFamilyHandles;

    public RocksDbTemplate(RocksDbConfig config) throws RocksDBException, IOException {
        this.config = config;
        config.cleanup();
        RocksDB.loadLibrary();

        columnFamilyHandles = new ArrayList<>();
        db = RocksDB.open(config.getDbOptions(), config.getDbPath(),
            config.getColumnFamilyDescriptors(), columnFamilyHandles);
    }

    // ==================== 基本操作（默认列族） ====================

    /**
     * 写入数据
     */
    public void put(String key, String value) throws RocksDBException {
        db.put(bytes(key), bytes(value));
    }

    /**
     * 写入数据（带 WriteOptions）
     */
    public void put(String key, String value, WriteOptions writeOptions) throws RocksDBException {
        db.put(writeOptions, bytes(key), bytes(value));
    }

    /**
     * 读取数据
     */
    public String get(String key) throws RocksDBException {
        byte[] value = db.get(bytes(key));
        return value != null ? asString(value) : null;
    }

    /**
     * 检查键是否存在
     */
    public boolean exists(String key) throws RocksDBException {
        return db.get(bytes(key)) != null;
    }

    /**
     * 删除数据
     */
    public void delete(String key) throws RocksDBException {
        db.delete(bytes(key));
    }

    // ==================== 列族操作 ====================

    /**
     * 创建列族
     */
    public ColumnFamilyHandle createColumnFamily(String name) throws RocksDBException {
        ColumnFamilyHandle handle = db.createColumnFamily(
            new ColumnFamilyDescriptor(bytes(name)));
        columnFamilyHandles.add(handle);
        return handle;
    }

    /**
     * 使用列族写入
     */
    public void put(ColumnFamilyHandle handle, String key, String value) throws RocksDBException {
        db.put(handle, bytes(key), bytes(value));
    }

    /**
     * 使用列族读取
     */
    public String get(ColumnFamilyHandle handle, String key) throws RocksDBException {
        byte[] value = db.get(handle, bytes(key));
        return value != null ? asString(value) : null;
    }

    /**
     * 使用列族删除
     */
    public void delete(ColumnFamilyHandle handle, String key) throws RocksDBException {
        db.delete(handle, bytes(key));
    }

    /**
     * 获取默认列族 Handle
     */
    public ColumnFamilyHandle getDefaultColumnFamilyHandle() {
        return columnFamilyHandles.get(0);
    }

    // ==================== 快照操作 ====================

    /**
     * 创建快照
     */
    public Snapshot getSnapshot() {
        return db.getSnapshot();
    }

    /**
     * 释放快照
     */
    public void releaseSnapshot(Snapshot snapshot) {
        db.releaseSnapshot(snapshot);
    }

    // ==================== 迭代器操作 ====================

    /**
     * 创建迭代器
     */
    public RocksIterator iterator() {
        return db.newIterator();
    }

    /**
     * 创建使用快照的迭代器
     */
    public RocksIterator iterator(Snapshot snapshot) {
        ReadOptions readOptions = new ReadOptions();
        readOptions.setSnapshot(snapshot);
        return db.newIterator(readOptions);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量写入
     */
    public void write(WriteBatch batch) throws RocksDBException {
        db.write(new WriteOptions(), batch);
    }

    /**
     * 创建 WriteBatch
     */
    public WriteBatch createWriteBatch() {
        return new WriteBatch();
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
            for (ColumnFamilyHandle handle : columnFamilyHandles) {
                handle.close();
            }
            columnFamilyHandles.clear();
            db.close();
        }
    }
}
