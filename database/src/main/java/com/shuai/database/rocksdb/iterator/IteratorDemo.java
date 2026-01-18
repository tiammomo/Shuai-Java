package com.shuai.database.rocksdb.iterator;

import com.shuai.database.rocksdb.RocksDbConfig;
import com.shuai.database.rocksdb.RocksDbTemplate;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import java.io.IOException;

/**
 * RocksDB 迭代器演示
 *
 * 核心内容：
 * - 遍历所有数据
 * - 范围查询 (seek)
 * - 反向遍历
 *
 * @author Shuai
 */
public class IteratorDemo {

    private static final String DB_PATH = "target/rocksdb-demo-iterator";

    public static void run() throws IOException, RocksDBException {
        System.out.println("\n--- RocksDB 迭代器（Iterator） ---");

        RocksDbConfig config = new RocksDbConfig(DB_PATH);
        try (RocksDbTemplate template = new RocksDbTemplate(config)) {

            // 准备测试数据
            for (int i = 1; i <= 10; i++) {
                template.put("user:" + i, "User" + i + "_Data");
            }
            template.put("key:a", "Value_A");
            template.put("key:b", "Value_B");
            template.put("key:c", "Value_C");

            // [1] 遍历所有数据
            System.out.println("\n[1] 遍历所有数据");
            RocksIterator iterator = template.iterator();
            int count = 0;
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                if (count < 5) {
                    System.out.println("  " + asString(iterator.key()) + " = " + asString(iterator.value()));
                }
                count++;
            }
            iterator.close();
            System.out.println("  ... 共 " + count + " 条数据");

            // [2] 范围查询
            System.out.println("\n[2] 范围查询 (seek)");
            iterator = template.iterator();
            System.out.println("  查找 user:1 到 user:5:");
            int rangeCount = 0;
            iterator.seek(bytes("user:1"));
            while (iterator.isValid() && rangeCount < 10) {
                String key = asString(iterator.key());
                if (!key.startsWith("user:")) {
                    break;
                }
                System.out.println("  " + key + " = " + asString(iterator.value()));
                iterator.next();
                rangeCount++;
            }
            iterator.close();

            // [3] 反向遍历
            System.out.println("\n[3] 反向遍历 (seekToLast)");
            iterator = template.iterator();
            iterator.seekToLast();
            if (iterator.isValid()) {
                System.out.println("  最后一条: " + asString(iterator.key()));
            }
            iterator.close();

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
