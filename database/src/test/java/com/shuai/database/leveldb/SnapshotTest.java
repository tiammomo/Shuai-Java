package com.shuai.database.leveldb;

import org.iq80.leveldb.Snapshot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LevelDB 快照测试
 *
 * @author Shuai
 */
class SnapshotTest {

    private LevelDbTemplate template;
    private static final String TEST_DB_PATH = "target/leveldb-test-snapshot";

    @BeforeEach
    void setUp() throws IOException {
        LevelDbConfig config = new LevelDbConfig(TEST_DB_PATH);
        template = new LevelDbTemplate(config);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (template != null) {
            template.close();
        }
    }

    @Test
    void testSnapshotIsolation() throws IOException {
        // 准备数据并创建快照
        template.put("snapshot:key", "original_value");
        Snapshot snapshot = template.getSnapshot();

        // 修改数据
        template.put("snapshot:key", "modified_value");

        // 快照读取应返回原始值
        assertEquals("original_value", template.get("snapshot:key", snapshot));
        // 当前读取应返回新值
        assertEquals("modified_value", template.get("snapshot:key"));
    }

    @Test
    void testMultipleSnapshots() throws IOException {
        template.put("multi:key", "v1");
        Snapshot s1 = template.getSnapshot();

        template.put("multi:key", "v2");
        Snapshot s2 = template.getSnapshot();

        template.put("multi:key", "v3");

        assertEquals("v1", template.get("multi:key", s1));
        assertEquals("v2", template.get("multi:key", s2));
        assertEquals("v3", template.get("multi:key"));
    }
}
