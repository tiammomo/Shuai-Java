package com.shuai.database.leveldb.snapshot;

import com.shuai.database.leveldb.LevelDbConfig;
import com.shuai.database.leveldb.LevelDbTemplate;
import org.iq80.leveldb.Snapshot;

import java.io.IOException;

/**
 * LevelDB 快照演示
 *
 * 核心内容：
 * - 快照创建
 * - 使用快照读取一致性数据
 * - 快照与写入的隔离
 *
 * @author Shuai
 */
public class SnapshotDemo {

    private static final String DB_PATH = "target/leveldb-demo-snapshot";

    public static void run() throws IOException {
        System.out.println("\n--- LevelDB 快照（Snapshot） ---");

        LevelDbConfig config = new LevelDbConfig(DB_PATH);
        try (LevelDbTemplate template = new LevelDbTemplate(config)) {

            // 准备测试数据
            template.put("key1", "Original Value");

            // [1] 创建快照
            System.out.println("\n[1] 创建快照");
            Snapshot snapshot = template.getSnapshot();
            System.out.println("  快照已创建");

            // [2] 使用快照读取（读取原始值）
            System.out.println("\n[2] 使用快照读取");
            String valueFromSnapshot = template.get("key1", snapshot);
            System.out.println("  快照中 key1 = " + valueFromSnapshot);

            // [3] 在快照外修改数据
            System.out.println("\n[3] 修改数据（不影响快照）");
            template.put("key1", "Modified Value");
            System.out.println("  当前 key1 = " + template.get("key1"));
            System.out.println("  快照中 key1 = " + template.get("key1", snapshot));

            // [4] 验证快照隔离
            System.out.println("\n[4] 快照隔离验证");
            System.out.println("  快照读取的值: " + valueFromSnapshot);
            System.out.println("  当前读取的值: " + template.get("key1"));

            // 恢复数据
            template.put("key1", "Original Value");
            System.out.println("\n快照演示完成！");

        }
    }
}
