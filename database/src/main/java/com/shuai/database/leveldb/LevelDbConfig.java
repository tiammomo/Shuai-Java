package com.shuai.database.leveldb;

import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

/**
 * LevelDB 配置类
 *
 * @author Shuai
 */
public class LevelDbConfig {

    private final String dbPath;
    private final Options options;

    public LevelDbConfig(String dbPath) {
        this.dbPath = dbPath;
        this.options = new Options();
        this.options.createIfMissing(true);
        this.options.errorIfExists(true);
    }

    public String getDbPath() {
        return dbPath;
    }

    public Options getOptions() {
        return options;
    }

    /**
     * 清理数据库目录
     */
    public void cleanup() throws IOException {
        File dbFile = new File(dbPath);
        deleteRecursively(dbFile);
    }

    private void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        file.delete();
    }
}
