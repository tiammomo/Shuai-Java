package com.shuai.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Properties 配置测试类
 */
@DisplayName("Properties 测试")
class BasicsPropertiesTest {

    @TempDir
    Path tempDir;

    private Properties testProps;

    @BeforeEach
    void setUp() {
        testProps = new Properties();
    }

    @AfterEach
    void tearDown() {
        testProps.clear();
    }

    @Test
    @DisplayName("创建和设置属性")
    void createAndSetProperties() {
        testProps.setProperty("username", "admin");
        testProps.setProperty("password", "123456");
        testProps.setProperty("timeout", "30");

        assertEquals("admin", testProps.getProperty("username"));
        assertEquals("123456", testProps.getProperty("password"));
        assertEquals("30", testProps.getProperty("timeout"));
    }

    @Test
    @DisplayName("获取不存在的属性返回默认值")
    void getPropertyWithDefault() {
        String value = testProps.getProperty("nonexistent", "defaultValue");
        assertEquals("defaultValue", value);
    }

    @Test
    @DisplayName("属性包含检查")
    void containsProperty() {
        testProps.setProperty("key1", "value1");
        testProps.setProperty("key2", "value2");

        assertTrue(testProps.containsKey("key1"));
        assertTrue(testProps.containsValue("value1"));
        assertFalse(testProps.containsKey("key3"));
    }

    @Test
    @DisplayName("获取所有键")
    void getPropertyNames() {
        testProps.setProperty("a", "1");
        testProps.setProperty("b", "2");
        testProps.setProperty("c", "3");

        Set<String> keys = testProps.stringPropertyNames();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("a"));
        assertTrue(keys.contains("b"));
        assertTrue(keys.contains("c"));
    }

    @Test
    @DisplayName("保存到文件")
    void saveToFile() throws IOException {
        testProps.setProperty("app.name", "TestApp");
        testProps.setProperty("app.version", "1.0.0");

        File file = tempDir.resolve("config.properties").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            testProps.store(fos, "Test Configuration");
        }

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("从文件加载")
    void loadFromFile() throws IOException {
        File file = tempDir.resolve("test.properties").toFile();
        try (FileWriter fw = new FileWriter(file)) {
            fw.write("# Test config\n");
            fw.write("key1=value1\n");
            fw.write("key2=value2\n");
        }

        Properties loaded = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            loaded.load(fis);
        }

        assertEquals("value1", loaded.getProperty("key1"));
        assertEquals("value2", loaded.getProperty("key2"));
    }

    @Test
    @DisplayName("保存为 XML 格式")
    void saveAsXml() throws IOException {
        testProps.setProperty("xml.test", "value");
        testProps.setProperty("xml.name", "test");

        File file = tempDir.resolve("config.xml").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            testProps.storeToXML(fos, "XML Configuration");
        }

        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    @Test
    @DisplayName("从 XML 加载")
    void loadFromXml() throws IOException {
        // 先设置测试值
        testProps.setProperty("test.key", "testValue");
        testProps.setProperty("another.key", "anotherValue");

        File file = tempDir.resolve("test.xml").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            testProps.storeToXML(fos, "Test XML");
        }

        Properties loaded = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            loaded.loadFromXML(fis);
        }

        assertEquals("testValue", loaded.getProperty("test.key"));
        assertEquals("anotherValue", loaded.getProperty("another.key"));
    }

    @Test
    @DisplayName("Properties 基本概念")
    void basicConcepts() {
        System.out.println("  Properties 特点:");
        System.out.println("    - 继承自 Hashtable<K,V>");
        System.out.println("    - 键值对必须是字符串");
        System.out.println("    - 支持从文件加载/保存");
        System.out.println("    - 支持 XML 格式");
        System.out.println("");
        System.out.println("  常用方法:");
        System.out.println("    setProperty(key, value)  - 设置属性");
        System.out.println("    getProperty(key)         - 获取属性");
        System.out.println("    getProperty(key, default)- 获取属性，不存在返回默认值");
        System.out.println("    load(InputStream)        - 从流加载");
        System.out.println("    store(OutputStream, comments) - 保存到文件");
        System.out.println("    loadFromXML(InputStream) - 从 XML 加载");
        System.out.println("    storeToXML(OutputStream, comments) - 保存为 XML");
        assertTrue(true);
    }

    @Test
    @DisplayName("Properties 与 System.getProperty")
    void systemProperties() {
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");

        System.out.println("  System.getProperty() 示例:");
        System.out.println("    java.version = " + javaVersion);
        System.out.println("    os.name = " + osName);
        System.out.println("    user.home = " + System.getProperty("user.home"));

        assertNotNull(javaVersion);
        assertNotNull(osName);
    }

    @Test
    @DisplayName("Properties 遍历")
    void iterateProperties() {
        testProps.setProperty("first", "1");
        testProps.setProperty("second", "2");
        testProps.setProperty("third", "3");

        System.out.println("  遍历 Properties:");
        for (String key : testProps.stringPropertyNames()) {
            System.out.println("    " + key + " = " + testProps.getProperty(key));
        }

        assertEquals(3, testProps.stringPropertyNames().size());
    }

    @Test
    @DisplayName("Properties 序列化警告")
    void serializationWarning() {
        System.out.println("  Properties 注意事项:");
        System.out.println("    1. 不要存储非字符串对象（会被强转）");
        System.out.println("    2. 使用 getProperty() 而不是 get()");
        System.out.println("    3. 敏感信息不要存 Properties");
        System.out.println("    4. 编码问题：使用 InputStreamReader 指定编码");
        System.out.println("");
        System.out.println("  正确加载方式（指定编码）:");
        System.out.println("    try (InputStreamReader isr = new InputStreamReader(");
        System.out.println("            new FileInputStream(file), StandardCharsets.UTF_8)) {");
        System.out.println("        properties.load(isr);");
        System.out.println("    }");
        assertTrue(true);
    }
}
