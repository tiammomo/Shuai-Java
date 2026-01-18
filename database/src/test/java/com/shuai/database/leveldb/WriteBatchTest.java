package com.shuai.database.leveldb;

import org.iq80.leveldb.WriteBatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LevelDB WriteBatch 测试
 *
 * @author Shuai
 */
class WriteBatchTest {

    private LevelDbTemplate template;
    private static final String TEST_DB_PATH = "target/leveldb-test-writebatch";

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
    void testWriteBatchPut() throws IOException {
        try (WriteBatch batch = template.createWriteBatch()) {
            batch.put(bytes("batch:1"), bytes("value1"));
            batch.put(bytes("batch:2"), bytes("value2"));
            batch.put(bytes("batch:3"), bytes("value3"));
            template.write(batch);
        }

        assertEquals("value1", template.get("batch:1"));
        assertEquals("value2", template.get("batch:2"));
        assertEquals("value3", template.get("batch:3"));
    }

    @Test
    void testWriteBatchDelete() throws IOException {
        template.put("delete:test", "will_be_deleted");

        try (WriteBatch batch = template.createWriteBatch()) {
            batch.delete(bytes("delete:test"));
            template.write(batch);
        }

        assertNull(template.get("delete:test"));
    }

    @Test
    void testWriteBatchMixed() throws IOException {
        template.put("mixed:1", "original1");
        template.put("mixed:2", "original2");

        try (WriteBatch batch = template.createWriteBatch()) {
            batch.put(bytes("mixed:1"), bytes("updated1"));
            batch.put(bytes("mixed:3"), bytes("new3"));
            batch.delete(bytes("mixed:2"));
            template.write(batch);
        }

        assertEquals("updated1", template.get("mixed:1"));
        assertNull(template.get("mixed:2"));
        assertEquals("new3", template.get("mixed:3"));
    }

    @Test
    void testWriteBatchPerformance() throws IOException {
        long startTime = System.currentTimeMillis();
        try (WriteBatch batch = template.createWriteBatch()) {
            for (int i = 1; i <= 1000; i++) {
                batch.put(bytes("perf:" + i), bytes("Value_" + i));
            }
            template.write(batch);
        }
        long endTime = System.currentTimeMillis();

        // 验证数据
        for (int i = 1; i <= 1000; i++) {
            assertEquals("Value_" + i, template.get("perf:" + i));
        }

        // 性能应该在合理范围内
        assertTrue(endTime - startTime < 5000, "Batch write should complete quickly");
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
