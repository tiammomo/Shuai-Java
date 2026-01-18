package com.shuai.database.leveldb;

import com.shuai.database.leveldb.basic.BasicOpsDemo;
import com.shuai.database.leveldb.iterator.IteratorDemo;
import com.shuai.database.leveldb.snapshot.SnapshotDemo;
import com.shuai.database.leveldb.writebatch.WriteBatchDemo;

/**
 * LevelDB 入口类
 *
 * @author Shuai
 */
public class LevelDbDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(50));
        System.out.println("       LevelDB 键值存储");
        System.out.println("=".repeat(50));

        BasicOpsDemo.run();
        SnapshotDemo.run();
        IteratorDemo.run();
        WriteBatchDemo.run();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("       LevelDB 演示完成");
        System.out.println("=".repeat(50));
    }
}
