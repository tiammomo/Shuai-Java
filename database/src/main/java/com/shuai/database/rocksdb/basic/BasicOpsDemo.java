package com.shuai.database.rocksdb.basic;

import com.shuai.database.rocksdb.RocksDbConfig;
import com.shuai.database.rocksdb.RocksDbTemplate;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteOptions;

import java.io.IOException;

/**
 * RocksDB 基本操作演示
 *
 * 核心内容：
 * - 数据库打开与关闭
 * - put/get/delete 操作
 * - WriteOptions 同步写入
 *
 * @author Shuai
 */
public class BasicOpsDemo {

    private static final String DB_PATH = "target/rocksdb-demo-basic";

    public static void run() throws IOException, RocksDBException {
        System.out.println("\n--- RocksDB 基本操作 ---");

        RocksDbConfig config = new RocksDbConfig(DB_PATH);
        try (RocksDbTemplate template = new RocksDbTemplate(config)) {

            // [1] 写入数据
            System.out.println("\n[1] 写入数据 (put)");
            template.put("key1", "Hello RocksDB!");
            template.put("key2", "Key-Value Store");
            template.put("key3", "Embedded Database");
            System.out.println("  put(key1, 'Hello RocksDB!')");
            System.out.println("  put(key2, 'Key-Value Store')");
            System.out.println("  put(key3, 'Embedded Database')");

            // [2] 读取数据
            System.out.println("\n[2] 读取数据 (get)");
            System.out.println("  get(key1) = " + template.get("key1"));
            System.out.println("  get(key2) = " + template.get("key2"));

            // [3] 同步写入
            System.out.println("\n[3] 同步写入 (WriteOptions)");
            WriteOptions writeOptions = new WriteOptions();
            writeOptions.setSync(true);
            template.put("sync_key", "synced data", writeOptions);
            System.out.println("  put(sync_key, 'synced data') - 同步写入");

            // [4] 检查键是否存在
            System.out.println("\n[4] 检查键是否存在");
            System.out.println("  exists(key1) = " + template.exists("key1"));
            System.out.println("  exists(not_exist) = " + template.exists("not_exist"));

            // [5] 删除数据
            System.out.println("\n[5] 删除数据 (delete)");
            template.delete("key3");
            System.out.println("  delete(key3)");
            System.out.println("  get(key3) = " + template.get("key3"));

            // [6] 批量插入测试数据
            System.out.println("\n[6] 批量插入测试数据");
            for (int i = 1; i <= 10; i++) {
                template.put("user:" + i, "User" + i + "_Data");
            }
            System.out.println("  插入 10 条 user 数据");

        }
        System.out.println("\n基本操作演示完成！");
    }
}
