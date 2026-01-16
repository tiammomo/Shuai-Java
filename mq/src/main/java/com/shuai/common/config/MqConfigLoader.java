package com.shuai.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * MQ 配置加载器
 *
 * 【使用示例】
 *   Properties kafkaProps = MqConfigLoader.load("kafka.properties");
 *   String servers = kafkaProps.getProperty("bootstrap.servers");
 *
 * @author Shuai
 */
public final class MqConfigLoader {

    private MqConfigLoader() {}

    /**
     * 加载配置文件
     *
     * @param fileName 配置文件名（classpath 路径）
     * @return 属性对象
     */
    public static Properties load(String fileName) {
        Properties props = new Properties();
        try (InputStream is = MqConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Config file not found: " + fileName);
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config: " + fileName, e);
        }
        return props;
    }

    /**
     * 加载配置文件（带默认值）
     *
     * @param fileName 配置文件名
     * @param defaultProps 默认属性
     * @return 合并后的属性对象
     */
    public static Properties load(String fileName, Properties defaultProps) {
        Properties props = load(fileName);
        for (String key : defaultProps.stringPropertyNames()) {
            if (!props.containsKey(key)) {
                props.setProperty(key, defaultProps.getProperty(key));
            }
        }
        return props;
    }

    /**
     * 获取字符串配置
     *
     * @param props 属性对象
     * @param key 键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getString(Properties props, String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    /**
     * 获取整型配置
     *
     * @param props 属性对象
     * @param key 键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static int getInt(Properties props, String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取长整型配置
     *
     * @param props 属性对象
     * @param key 键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static long getLong(Properties props, String key, long defaultValue) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔型配置
     *
     * @param props 属性对象
     * @param key 键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static boolean getBoolean(Properties props, String key, boolean defaultValue) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
