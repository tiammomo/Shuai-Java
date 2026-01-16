package com.shuai.common;

import com.shuai.common.config.MqConfigLoader;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MqConfigLoader 测试
 *
 * @author Shuai
 */
public class MqConfigLoaderTest {

    @Test
    void testGetString() {
        Properties props = new Properties();
        props.setProperty("key1", "value1");
        props.setProperty("key2", "");

        // 正常获取
        assertEquals("value1", MqConfigLoader.getString(props, "key1", "default"));

        // 使用默认值
        assertEquals("default", MqConfigLoader.getString(props, "notExist", "default"));

        // 空值返回空字符串（Properties 特性）
        assertEquals("", MqConfigLoader.getString(props, "key2", "default"));

        // 不存在使用默认值
        assertEquals("default", MqConfigLoader.getString(props, "notExist", "default"));
    }

    @Test
    void testGetInt() {
        Properties props = new Properties();
        props.setProperty("int1", "100");
        props.setProperty("int2", "abc");
        props.setProperty("empty", "");

        // 正常获取
        assertEquals(100, MqConfigLoader.getInt(props, "int1", 0));

        // 格式错误使用默认值
        assertEquals(50, MqConfigLoader.getInt(props, "int2", 50));

        // 空值使用默认值
        assertEquals(10, MqConfigLoader.getInt(props, "empty", 10));

        // 不存在使用默认值
        assertEquals(20, MqConfigLoader.getInt(props, "notExist", 20));
    }

    @Test
    void testGetLong() {
        Properties props = new Properties();
        props.setProperty("long1", "1000000000000");

        assertEquals(1000000000000L, MqConfigLoader.getLong(props, "long1", 0L));
        assertEquals(0L, MqConfigLoader.getLong(props, "notExist", 0L));
    }

    @Test
    void testGetBoolean() {
        Properties props = new Properties();
        props.setProperty("bool1", "true");
        props.setProperty("bool2", "false");
        props.setProperty("bool3", "anything");

        assertTrue(MqConfigLoader.getBoolean(props, "bool1", false));
        assertFalse(MqConfigLoader.getBoolean(props, "bool2", true));
        assertFalse(MqConfigLoader.getBoolean(props, "bool3", false));
        assertTrue(MqConfigLoader.getBoolean(props, "notExist", true));
    }

    @Test
    void testLoadWithDefaults() {
        Properties defaults = new Properties();
        defaults.setProperty("key1", "default1");
        defaults.setProperty("key2", "default2");

        Properties props = new Properties();
        props.setProperty("key1", "value1");

        Properties merged = MqConfigLoader.load("kafka.properties", defaults);

        // key1 应该有值
        assertNotNull(merged.getProperty("key1"));
    }
}
