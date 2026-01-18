package com.shuai.database.rocksdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksDBException;
import org.rocksdb.Snapshot;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RocksDB 快照测试
 *
 * @author Shuai
 */
class SnapshotTest {

    private RocksDbTemplate template;
    private static final String TEST_DB_PATH = "target/rocksdb-test-snapshot";

    @BeforeEach
    void setUp() throws IOException, RocksDBException {
        RocksDbConfig config = new RocksDbConfig(TEST_DB_PATH);
        template = new RocksDbTemplate(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (template != null) {
            template.close();
        }
    }

    @Test
    void testSnapshotIsolation() throws RocksDBException {
        // 准备数据并创建快照
        template.put("snapshot:key", "original_value");
        Snapshot snapshot = template.getSnapshot();

        // 修改数据
        template.put("snapshot:key", "modified_value");

        // 当前读取应返回新值
        assertEquals("modified_value", template.get("snapshot:key"));

        template.releaseSnapshot(snapshot);
    }

    @Test
    void testMultipleSnapshots() throws RocksDBException {
        template.put("multi:key", "v1");
        Snapshot s1 = template.getSnapshot();

        template.put("multi:key", "v2");
        Snapshot s2 = template.getSnapshot();

        template.put("multi:key", "v3");

        // 释放所有快照
        template.releaseSnapshot(s1);
        template.releaseSnapshot(s2);

        assertEquals("v3", template.get("multi:key"));
    }
}
