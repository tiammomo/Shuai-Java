package com.shuai.database.leveldb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LevelDB 基本操作测试
 *
 * @author Shuai
 */
class BasicOpsTest {

    private LevelDbTemplate template;
    private static final String TEST_DB_PATH = "target/leveldb-test-basic";

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
    void testPutAndGet() throws IOException {
        template.put("test:key1", "value1");
        template.put("test:key2", "value2");

        assertEquals("value1", template.get("test:key1"));
        assertEquals("value2", template.get("test:key2"));
    }

    @Test
    void testDelete() throws IOException {
        template.put("delete:key", "value");
        assertEquals("value", template.get("delete:key"));

        template.delete("delete:key");
        assertNull(template.get("delete:key"));
    }

    @Test
    void testExists() throws IOException {
        template.put("exists:key", "value");

        assertTrue(template.exists("exists:key"));
        assertFalse(template.exists("exists:not_exist"));
    }

    @Test
    void testBatchInsert() throws IOException {
        for (int i = 1; i <= 100; i++) {
            template.put("batch:" + i, "Value_" + i);
        }

        for (int i = 1; i <= 100; i++) {
            assertEquals("Value_" + i, template.get("batch:" + i));
        }
    }
}
