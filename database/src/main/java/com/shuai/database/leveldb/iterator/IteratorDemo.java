package com.shuai.database.leveldb.iterator;

import com.shuai.database.leveldb.LevelDbConfig;
import com.shuai.database.leveldb.LevelDbTemplate;
import org.iq80.leveldb.DBIterator;

import java.io.IOException;
import java.util.Map;

/**
 * LevelDB 迭代器演示
 *
 * 核心内容：
 * - 遍历所有数据
 * - 范围查询 (seek)
 * - 反向遍历
 *
 * @author Shuai
 */
public class IteratorDemo {

    private static final String DB_PATH = "target/leveldb-demo-iterator";

    public static void run() throws IOException {
        System.out.println("\n--- LevelDB 迭代器（Iterator） ---");

        LevelDbConfig config = new LevelDbConfig(DB_PATH);
        try (LevelDbTemplate template = new LevelDbTemplate(config)) {

            // 准备测试数据
            for (int i = 1; i <= 10; i++) {
                template.put("user:" + i, "User" + i + "_Data");
            }
            template.put("key:a", "Value_A");
            template.put("key:b", "Value_B");
            template.put("key:c", "Value_C");

            // [1] 遍历所有数据
            System.out.println("\n[1] 遍历所有数据");
            int count = 0;
            try (DBIterator iterator = template.iterator()) {
                iterator.seekToFirst();
                while (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    if (count < 5) {
                        System.out.println("  " + asString(entry.getKey()) + " = " + asString(entry.getValue()));
                    }
                    count++;
                }
            }
            System.out.println("  ... 共 " + count + " 条数据");

            // [2] 范围查询
            System.out.println("\n[2] 范围查询 (seek)");
            int rangeCount = 0;
            try (DBIterator iterator = template.iterator()) {
                System.out.println("  查找 user:1 到 user:5:");
                iterator.seek(bytes("user:1"));
                while (iterator.hasNext() && rangeCount < 10) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    String key = asString(entry.getKey());
                    if (!key.startsWith("user:")) {
                        break;
                    }
                    System.out.println("  " + key + " = " + asString(entry.getValue()));
                    rangeCount++;
                }
            }

            // [3] 反向遍历
            System.out.println("\n[3] 反向遍历 (seekToLast)");
            try (DBIterator iterator = template.iterator()) {
                iterator.seekToLast();
                if (iterator.hasNext()) {
                    Map.Entry<byte[], byte[]> entry = iterator.next();
                    System.out.println("  最后一条: " + asString(entry.getKey()));
                }
            }

            System.out.println("\n迭代器演示完成！");
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static String asString(byte[] value) {
        return new String(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
