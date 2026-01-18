package com.shuai.database.rocksdb;

import com.shuai.database.rocksdb.basic.BasicOpsDemo;
import com.shuai.database.rocksdb.columnfamily.ColumnFamilyDemo;
import com.shuai.database.rocksdb.iterator.IteratorDemo;
import com.shuai.database.rocksdb.snapshot.SnapshotDemo;
import com.shuai.database.rocksdb.writebatch.WriteBatchDemo;

/**
 * RocksDB 入口类
 *
 * @author Shuai
 */
public class RocksDbDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("       RocksDB 键值存储");
        System.out.println("=".repeat(50));

        BasicOpsDemo.run();
        ColumnFamilyDemo.run();
        SnapshotDemo.run();
        IteratorDemo.run();
        WriteBatchDemo.run();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("       RocksDB 演示完成");
        System.out.println("=".repeat(50));
    }
}
