package com.shuai.database.rocksdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RocksDB 迭代器测试
 *
 * @author Shuai
 */
class IteratorTest {

    private RocksDbTemplate template;
    private static final String TEST_DB_PATH = "target/rocksdb-test-iterator";

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
    void testIteratorTraverse() throws RocksDBException {
        for (int i = 1; i <= 50; i++) {
            template.put("iter:key" + i, "value" + i);
        }

        RocksIterator iterator = template.iterator();
        int count = 0;
        for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
            count++;
        }
        iterator.close();
        assertEquals(50, count);
    }

    @Test
    void testIteratorSeek() throws RocksDBException {
        for (int i = 1; i <= 10; i++) {
            template.put("seek:key" + i, "value" + i);
        }

        RocksIterator iterator = template.iterator();
        iterator.seek(bytes("seek:key5"));
        int count = 0;
        while (iterator.isValid() && count < 10) {
            String key = new String(iterator.key());
            assertTrue(key.compareTo("seek:key4") >= 0);
            iterator.next();
            count++;
        }
        iterator.close();
        assertEquals(5, count); // key5 到 key9 (key10 iteration ends loop)
    }

    @Test
    void testIteratorSeekToLast() throws RocksDBException {
        template.put("last:1", "first");
        template.put("last:2", "second");
        template.put("last:3", "last");

        RocksIterator iterator = template.iterator();
        iterator.seekToLast();
        assertTrue(iterator.isValid());
        assertEquals("last:3", new String(iterator.key()));
        assertEquals("last", new String(iterator.value()));
        iterator.close();
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
