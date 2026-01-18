package com.shuai.database.rocksdb;

import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.RocksDB;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RocksDB 配置类
 *
 * @author Shuai
 */
public class RocksDbConfig {

    private final String dbPath;
    private final DBOptions dbOptions;
    private final List<ColumnFamilyDescriptor> columnFamilyDescriptors;

    public RocksDbConfig(String dbPath) {
        this.dbPath = dbPath;
        this.dbOptions = new DBOptions();
        this.dbOptions.setCreateIfMissing(true);
        this.dbOptions.setErrorIfExists(true);
        this.columnFamilyDescriptors = new ArrayList<>();
        this.columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY));
    }

    public String getDbPath() {
        return dbPath;
    }

    public DBOptions getDbOptions() {
        return dbOptions;
    }

    public List<ColumnFamilyDescriptor> getColumnFamilyDescriptors() {
        return columnFamilyDescriptors;
    }

    /**
     * 添加列族
     */
    public void addColumnFamily(String name) {
        columnFamilyDescriptors.add(new ColumnFamilyDescriptor(name.getBytes()));
    }

    /**
     * 清理数据库目录
     */
    public void cleanup() throws IOException {
        File dbFile = new File(dbPath);
        deleteRecursively(dbFile);
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
