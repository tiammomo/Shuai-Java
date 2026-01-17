package com.shuai.testing;

import java.util.*;

/**
 * Java 测试基础演示类
 *
 * 涵盖内容：
 * - 单元测试概念：测试用例、断言、测试覆盖率
 * - JUnit 5 基础：@Test、@BeforeEach、@AfterEach 等注解
 * - 断言方法：assertEquals、assertTrue、assertThrows 等
 * - 参数化测试：@ParameterizedTest、@CsvSource
 * - Mock 概念：模拟对象、stub、verify
 * - 测试最佳实践
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsTestingDemo {

    /**
     * 执行所有测试演示
     */
    public void runAllDemos() {
        unitTestConcepts();
        junit5Annotations();
        assertionMethods();
        parameterizedTests();
        mockConcepts();
        bestPractices();
    }

    /**
     * 演示单元测试概念
     */
    private void unitTestConcepts() {
        // 单元测试特点：
        // - 隔离性：独立测试每个单元
        // - 快速性：毫秒级执行
        // - 可重复性：多次运行结果一致
        // - 自动化：无需人工干预

        // 被测试的类
        Calculator calculator = new Calculator();

        // 测试加法
        int sum = calculator.add(2, 3);
        assert sum == 5 : "加法测试失败";

        // 测试减法
        int diff = calculator.subtract(5, 3);
        assert diff == 2 : "减法测试失败";

        // 测试乘法
        int product = calculator.multiply(4, 5);
        assert product == 20 : "乘法测试失败";

        // 测试除法
        int quotient = calculator.divide(10, 2);
        assert quotient == 5 : "除法测试失败";
    }

    /**
     * 演示 JUnit 5 注解
     */
    private void junit5Annotations() {
        // JUnit 5 核心注解：
        //
        // @Test：标记测试方法
        // @BeforeEach：每个测试前执行（替代 @Before）
        // @AfterEach：每个测试后执行（替代 @After）
        // @BeforeAll：所有测试前执行一次（静态方法）
        // @AfterAll：所有测试后执行一次（静态方法）
        // @Disabled：禁用测试
        // @DisplayName：设置测试显示名称
        // @Nested：嵌套测试类
        // @ParameterizedTest：参数化测试
        // @RepeatedTest：重复测试
        // @TestFactory：动态测试工厂

        // 示例代码结构：
        /*
        class MyTest {
            @BeforeAll
            static void setupAll() { }  // 所有测试前

            @BeforeEach
            void setup() { }  // 每个测试前

            @Test
            void testMethod() { }  // 测试方法

            @AfterEach
            void teardown() { }  // 每个测试后

            @AfterAll
            static void teardownAll() { }  // 所有测试后
        }
        */
    }

    /**
     * 演示断言方法
     */
    private void assertionMethods() {
        // JUnit 5 断言分类：
        //
        // 1. 基本断言
        // assertEquals(expected, actual)
        // assertNotEquals(expected, actual)
        // assertSame(expected, actual)
        // assertNotSame(expected, actual)
        // assertNull(object)
        // assertNotNull(object)
        //
        // 2. 布尔断言
        // assertTrue(condition)
        // assertFalse(condition)
        //
        // 3. 数组断言
        // assertArrayEquals(expected, actual)
        //
        // 4. 异常断言
        // assertThrows(exceptionType, executable)
        // assertDoesNotThrow(executable)
        //
        // 5. 超时断言
        // assertTimeout(duration, executable)
        // assertTimeoutPreemptively(duration, executable)
        //
        // 6. 组装断言
        // assertAll(executables...)
    }

    /**
     * 演示参数化测试
     */
    private void parameterizedTests() {
        // 参数化测试允许用不同参数多次执行同一测试
        //
        // @ParameterizedTest：标记参数化测试
        // @ValueSource：提供简单值数组
        // @CsvSource：提供 CSV 格式数据
        // @MethodSource：引用方法作为数据源
        // @EnumSource：枚举值作为数据源
        // @ArgumentsSource：自定义参数提供器
        //
        // 示例：
        /*
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void testIsPositive(int number) {
            assertTrue(number > 0);
        }

        @ParameterizedTest
        @CsvSource({"1, 1, 2", "2, 3, 5", "10, 20, 30"})
        void testAdd(int a, int b, int expected) {
            assertEquals(expected, a + b);
        }
        */
    }

    /**
     * 演示 Mock 概念
     */
    private void mockConcepts() {
        // Mock 对象概念：
        // - 模拟被测组件的依赖
        // - 控制测试环境
        // - 验证交互行为
        //
        // 常用 Mock 框架：
        // - Mockito（最流行）
        // - EasyMock
        // - JMock
        // - PowerMock（ Mockito 扩展）
        //
        // Mockito 基础：
        //
        // 1. 创建 Mock 对象
        // List mockedList = mock(List.class);
        //
        // 2. Stub 方法（设置期望）
        // when(mockedList.get(0)).thenReturn("first");
        // when(mockedList.size()).thenReturn(1);
        //
        // 3. 验证交互
        // verify(mockedList).get(0);
        // verify(mockedList, times(2)).size();
        // verifyNoMoreInteractions(mockedList);
        //
        // 4. 参数匹配器
        // anyInt(), anyString(), anyList(), eq(value)
    }

    /**
     * 演示测试最佳实践
     */
    private void bestPractices() {
        // 测试命名规范：
        // - testMethodUnderTest_Scenario_ExpectedBehavior
        // - testAdd_withPositiveNumbers_returnsSum
        // - testAdd_withNegativeNumbers_returnsNegativeSum
        //
        // FIRST 原则：
        // - Fast：测试应该快速执行
        // - Isolated：测试应该相互独立
        // - Repeatable：测试应该可重复执行
        // - Self-validating：测试应该自动验证结果
        // - Timely：测试应该及时编写
        //
        // 测试覆盖率：
        // - 语句覆盖（Line Coverage）
        // - 分支覆盖（Branch Coverage）
        // - 路径覆盖（Path Coverage）
        // - 条件覆盖（Condition Coverage）
        //
        // 不推荐测试的内容：
        // - 简单 getter/setter
        // - 第三方库代码
        // - 集成测试代码（单独测试）
    }

    // ==================== 内部类 ====================

    /**
     * 演示用计算器类
     */
    static class Calculator {
        public int add(int a, int b) {
            return a + b;
        }

        public int subtract(int a, int b) {
            return a - b;
        }

        public int multiply(int a, int b) {
            return a * b;
        }

        public int divide(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("除数不能为零");
            }
            return a / b;
        }
    }

    /**
     * 服务接口示例
     */
    interface UserService {
        User findById(Long id);
        List<User> findAll();
        void save(User user);
        void delete(Long id);
    }

    /**
     * 实体类示例
     */
    static class User {
        private Long id;
        private String name;
        private String email;

        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }
}
