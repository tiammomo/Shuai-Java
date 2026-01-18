package com.shuai.caffeine;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CaffeineDemo 主入口测试
 */
class CaffeineDemoTest {

    @Test
    void testMainMethodRunsWithoutException() {
        // 捕获 System.out 输出以验证 main 方法正常执行
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        try {
            // 执行 main 方法，不应抛出异常
            assertDoesNotThrow(() -> {
                try {
                    CaffeineDemo.main(new String[0]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } finally {
            System.setOut(originalOut);
        }

        // 验证有输出内容
        assertTrue(outContent.toString().length() > 0, "Main method should produce output");
        assertTrue(outContent.toString().contains("Caffeine"), "Output should contain 'Caffeine'");
    }

    @Test
    void testMainMethodOutputContainsSections() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(outContent));

        try {
            try {
                CaffeineDemo.main(new String[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } finally {
            System.setOut(originalOut);
        }

        String output = outContent.toString();
        assertTrue(output.contains("Caffeine 核心概念"), "Should contain core concepts section");
        assertTrue(output.contains("基础使用"), "Should contain basic usage section");
    }
}
