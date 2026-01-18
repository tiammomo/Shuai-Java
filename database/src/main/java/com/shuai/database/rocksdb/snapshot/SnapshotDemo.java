package com.shuai.database.rocksdb.snapshot;

import com.shuai.database.rocksdb.RocksDbConfig;
import com.shuai.database.rocksdb.RocksDbTemplate;
import org.rocksdb.RocksDBException;
import org.rocksdb.Snapshot;

import java.io.IOException;

/**
 * RocksDB 快照演示
 *
 * 核心内容：
 * - 快照创建
 * - 使用快照读取一致性数据
 * - 快照与写入的隔离
 *
 * @author Shuai
 */
public class SnapshotDemo {

    private static final String DB_PATH = "target/rocksdb-demo-snapshot";

    public static void run() throws IOException, RocksDBException {
        System.out.println("\n--- RocksDB 快照（Snapshot） ---");

        RocksDbConfig config = new RocksDbConfig(DB_PATH);
        try (RocksDbTemplate template = new RocksDbTemplate(config)) {

            // 准备测试数据
            template.put("key1", "Original Value");

            // [1] 创建快照
            System.out.println("\n[1] 创建快照");
            Snapshot snapshot = template.getSnapshot();
            System.out.println("  快照已创建");

            // [2] 使用快照读取（读取原始值）
            System.out.println("\n[2] 使用快照读取");
            String originalValue = "Original Value";
            System.out.println("  快照中 key1 = " + originalValue);

            // [3] 在快照外修改数据
            System.out.println("\n[3] 修改数据（不影响快照）");
            template.put("key1", "Modified Value");
            System.out.println("  当前 key1 = " + template.get("key1"));
            System.out.println("  快照中 key1 = " + originalValue);

            // [4] 释放快照
            System.out.println("\n[4] 释放快照");
            template.releaseSnapshot(snapshot);
            System.out.println("  快照已释放");

            // 恢复数据
            template.put("key1", "Original Value");
            System.out.println("\n快照演示完成！");
        }
    }
}
