package com.shuai.config;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Java 配置管理演示 (Properties/YAML)
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsPropertiesDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Java 配置管理");
        System.out.println("=".repeat(50));

        propertiesOverview();
        propertiesReadWrite();
        propertiesBestPractices();
    }

    /**
     * Properties 概述
     */
    private void propertiesOverview() {
        System.out.println("\n--- Properties 概述 ---");

        System.out.println("\n  Properties 特点:");
        System.out.println("    - 继承自 Hashtable");
        System.out.println("    - 键值对都是 String");
        System.out.println("    - 支持 # 注释");
        System.out.println("    - 支持 \\ 转义");
        System.out.println("    - 适合简单配置");

        System.out.println("\n  常用方法:");
        System.out.println("    setProperty(key, value)  - 设置属性");
        System.out.println("    getProperty(key)         - 获取属性");
        System.out.println("    getProperty(key, default) - 获取默认值");
        System.out.println("    containsKey(key)         - 是否存在");
        System.out.println("    keys() / stringPropertyNames() - 获取所有键");
        System.out.println("    load(InputStream)        - 从流加载");
        System.out.println("    store(OutputStream, comments) - 保存到流");

        System.out.println("\n(");
        System.out.println("    # 这是一个配置文件");
        System.out.println("    # 数据库配置");
        System.out.println("    db.url=jdbc:mysql://localhost:3306/mydb");
        System.out.println("    db.username=root");
        System.out.println("    db.password=secret");
        System.out.println("    ");
        System.out.println("    # Redis 配置");
        System.out.println("    redis.host=localhost");
        System.out.println("    redis.port=6379");
    }

    /**
     * Properties 读写
     */
    private void propertiesReadWrite() {
        System.out.println("\n--- Properties 读写 ---");

        System.out.println("\n  读取文件:");
        System.out.println("    Properties props = new Properties();");
        System.out.println("    try (InputStream in = new FileInputStream(\"config.properties\")) {");
        System.out.println("        props.load(in);");
        System.out.println("        String value = props.getProperty(\"key\");");
        System.out.println("    }");

        System.out.println("\n  读取类路径:");
        System.out.println("    Properties props = new Properties();");
        System.out.println("    try (InputStream in = getClass().getClassLoader()");
        System.out.println("            .getResourceAsStream(\"application.properties\")) {");
        System.out.println("        props.load(in);");
        System.out.println("    }");

        System.out.println("\n  写入文件:");
        System.out.println("    Properties props = new Properties();");
        System.out.println("    props.setProperty(\"name\", \"value\");");
        System.out.println("    try (OutputStream out = new FileOutputStream(\"output.properties\")) {");
        System.out.println("        props.store(out, \"Comments at file header\");");
        System.out.println("    }");

        System.out.println("\n  从字符串读取:");
        System.out.println("    String config = \"key=value\\nkey2=value2\";");
        System.out.println("    Properties props = new Properties();");
        System.out.println("    props.load(new ByteArrayInputStream(config.getBytes()));");
    }

    /**
     * 最佳实践
     */
    private void propertiesBestPractices() {
        System.out.println("\n--- 最佳实践 ---");

        System.out.println("\n  1. 使用默认值:");
        System.out.println("    String value = props.getProperty(\"key\", \"defaultValue\");");

        System.out.println("\n  2. 类型转换:");
        System.out.println("    int port = Integer.parseInt(props.getProperty(\"port\", \"8080\"));");
        System.out.println("    boolean enabled = Boolean.parseBoolean(props.getProperty(\"enabled\", \"false\"));");

        System.out.println("\n  3. 配置文件分离:");
        System.out.println("    // dev.properties");
        System.out.println("    db.url=jdbc:mysql://localhost:3306/devdb");
        System.out.println("    ");
        System.out.println("    // prod.properties");
        System.out.println("    db.url=jdbc:mysql://prod.example.com:3306/proddb");

        System.out.println("\n  4. 环境变量覆盖:");
        System.out.println("    String dbUrl = System.getenv(\"DB_URL\");");
        System.out.println("    if (dbUrl != null) {");
        System.out.println("        props.setProperty(\"db.url\", dbUrl);");
        System.out.println("    }");

        System.out.println("\n  5. 命令行参数覆盖:");
        System.out.println("    for (String arg : args) {");
        System.out.println("        if (arg.startsWith(\"--\")) {");
        System.out.println("            String[] parts = arg.substring(2).split(\"=\");");
        System.out.println("            if (parts.length == 2) {");
        System.out.println("                props.setProperty(parts[0], parts[1]);");
        System.out.println("            }");
        System.out.println("        }");
        System.out.println("    }");
    }
}
