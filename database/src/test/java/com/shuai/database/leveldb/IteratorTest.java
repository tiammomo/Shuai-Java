package com.shuai.database.leveldb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LevelDB 迭代器测试
 *
 * @author Shuai
 */
class IteratorTest {

    private LevelDbTemplate template;
    private static final String TEST_DB_PATH = "target/leveldb-test-iterator";

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
    void testIteratorTraverse() throws IOException {
        for (int i = 1; i <= 50; i++) {
            template.put("iter:key" + i, "value" + i);
        }

        try (DBIterator iterator = template.iterator()) {
            int count = 0;
            iterator.seekToFirst();
            while (iterator.hasNext()) {
                iterator.next();
                count++;
            }
            assertEquals(50, count);
        }
    }

    @Test
    void testIteratorSeek() throws IOException {
        for (int i = 1; i <= 10; i++) {
            template.put("seek:key" + i, "value" + i);
        }

        try (DBIterator iterator = template.iterator()) {
            iterator.seek(bytes("seek:key5"));
            int count = 0;
            while (iterator.hasNext() && count < 10) {
                Map.Entry<byte[], byte[]> entry = iterator.next();
                String key = new String(entry.getKey());
                assertTrue(key.compareTo("seek:key4") >= 0);
                count++;
            }
            assertEquals(5, count); // key5 到 key9 (key10 is the 10th, but we loop while hasNext)
        }
    }

    @Test
    void testIteratorSeekToLast() throws IOException {
        template.put("last:1", "first");
        template.put("last:2", "second");
        template.put("last:3", "last");

        try (DBIterator iterator = template.iterator()) {
            iterator.seekToLast();
            assertTrue(iterator.hasNext());
            Map.Entry<byte[], byte[]> entry = iterator.next();
            assertEquals("last:3", new String(entry.getKey()));
            assertEquals("last", new String(entry.getValue()));
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
