package com.shuai.database.leveldb.writebatch;

import com.shuai.database.leveldb.LevelDbConfig;
import com.shuai.database.leveldb.LevelDbTemplate;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.WriteOptions;

import java.io.IOException;

/**
 * LevelDB 批量写操作演示
 *
 * 核心内容：
 * - WriteBatch 原子操作
 * - 批量 put/delete
 * - 性能测试
 *
 * @author Shuai
 */
public class WriteBatchDemo {

    private static final String DB_PATH = "target/leveldb-demo-writebatch";

    public static void run() throws IOException {
        System.out.println("\n--- LevelDB 批量写操作（WriteBatch） ---");

        LevelDbConfig config = new LevelDbConfig(DB_PATH);
        try (LevelDbTemplate template = new LevelDbTemplate(config)) {

            // [1] 基本批量写入
            System.out.println("\n[1] 基本批量写入");
            try (WriteBatch batch = template.createWriteBatch()) {
                batch.put(bytes("batch:1"), bytes("Batch Data 1"));
                batch.put(bytes("batch:2"), bytes("Batch Data 2"));
                batch.put(bytes("batch:3"), bytes("Batch Data 3"));
                batch.delete(bytes("key_to_delete"));

                template.write(batch);
                System.out.println("  批量写入 3 条，删除 1 条");
            }

            // [2] 验证结果
            System.out.println("\n[2] 验证结果");
            System.out.println("  batch:1 = " + template.get("batch:1"));
            System.out.println("  batch:2 = " + template.get("batch:2"));
            System.out.println("  batch:3 = " + template.get("batch:3"));
            System.out.println("  key_to_delete = " + (template.exists("key_to_delete") ? "存在" : "已删除"));

            // [3] 性能测试
            System.out.println("\n[3] 性能测试");
            long startTime = System.currentTimeMillis();
            try (WriteBatch batch = template.createWriteBatch()) {
                for (int i = 1; i <= 1000; i++) {
                    batch.put(bytes("perf:" + i), bytes("Value_" + i));
                }
                template.write(batch);
            }
            long endTime = System.currentTimeMillis();
            System.out.println("  批量写入 1000 条耗时: " + (endTime - startTime) + "ms");

            // [4] 清理测试数据
            System.out.println("\n[4] 清理测试数据");
            try (WriteBatch batch = template.createWriteBatch()) {
                for (int i = 1; i <= 1000; i++) {
                    batch.delete(bytes("perf:" + i));
                }
                batch.delete(bytes("batch:1"));
                batch.delete(bytes("batch:2"));
                batch.delete(bytes("batch:3"));
                template.write(batch);
                System.out.println("  清理完成");
            }

            System.out.println("\n批量写操作演示完成！");
        }
    }

    private static byte[] bytes(String value) {
        return value.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}
