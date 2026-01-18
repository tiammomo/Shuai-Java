package com.shuai.database.rocksdb.columnfamily;

import com.shuai.database.rocksdb.RocksDbConfig;
import com.shuai.database.rocksdb.RocksDbTemplate;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDBException;

import java.io.IOException;

/**
 * RocksDB 列族演示
 *
 * 核心内容：
 * - 创建列族
 * - 跨列族读写
 * - 列族隔离数据
 *
 * @author Shuai
 */
public class ColumnFamilyDemo {

    private static final String DB_PATH = "target/rocksdb-demo-cf";

    public static void run() throws IOException, RocksDBException {
        System.out.println("\n--- RocksDB 列族（Column Family） ---");

        RocksDbConfig config = new RocksDbConfig(DB_PATH);
        try (RocksDbTemplate template = new RocksDbTemplate(config)) {

            // [1] 使用默认列族
            System.out.println("\n[1] 使用默认列族");
            template.put("default:1", "Default Data 1");
            template.put("default:2", "Default Data 2");
            System.out.println("  default:1 = " + template.get("default:1"));

            // [2] 创建 users 列族
            System.out.println("\n[2] 创建 users 列族");
            ColumnFamilyHandle usersHandle = template.createColumnFamily("users");
            System.out.println("  列族 'users' 已创建");

            // [3] 创建 orders 列族
            System.out.println("\n[3] 创建 orders 列族");
            ColumnFamilyHandle ordersHandle = template.createColumnFamily("orders");
            System.out.println("  列族 'orders' 已创建");

            // [4] 使用列族写入数据
            System.out.println("\n[4] 使用列族写入数据");
            template.put(usersHandle, "user:1", "{\"name\":\"张三\",\"age\":25}");
            template.put(usersHandle, "user:2", "{\"name\":\"李四\",\"age\":30}");
            System.out.println("  users:user:1 = " + template.get(usersHandle, "user:1"));

            template.put(ordersHandle, "order:1", "{\"amount\":100,\"product\":\"A\"}");
            template.put(ordersHandle, "order:2", "{\"amount\":200,\"product\":\"B\"}");
            System.out.println("  orders:order:1 = " + template.get(ordersHandle, "order:1"));

            // [5] 跨列族读取验证
            System.out.println("\n[5] 跨列族数据隔离验证");
            System.out.println("  default:1 (default) = " + template.get("default:1"));
            System.out.println("  user:1 (users) = " + template.get(usersHandle, "user:1"));
            System.out.println("  order:1 (orders) = " + template.get(ordersHandle, "order:1"));

            System.out.println("\n列族演示完成！");
        }
    }
}
