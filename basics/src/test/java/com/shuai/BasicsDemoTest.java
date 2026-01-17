package com.shuai;

import org.junit.jupiter.api.*;

import com.shuai.oop.BasicsOOPDemo;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 单元测试示例
 *
 * 测试注解说明：
 * - @Test: 定义测试方法
 * - @BeforeEach: 每个测试前执行
 * - @AfterEach: 每个测试后执行
 * - @BeforeAll: 所有测试前执行（静态方法）
 * - @AfterAll: 所有测试后执行（静态方法）
 * - @DisplayName: 测试显示名称
 * - @Disabled: 禁用测试
 *
 * 断言方法说明：
 * - assertEquals(expected, actual)
 * - assertTrue(condition)
 * - assertFalse(condition)
 * - assertNull(object)
 * - assertNotNull(object)
 * - assertThrows(exceptionType, executable)
 * - assertAll(grouped assertions)
 */
class BasicsDemoTest {

    @BeforeAll
    static void setupAll() {
        // 所有测试前执行一次
    }

    @BeforeEach
    void setup() {
        // 每个测试前执行
    }

    // ===== 数据类型测试 =====

    @Test
    @DisplayName("基本类型范围测试")
    void testPrimitiveTypeRanges() {
        // int 范围
        int maxInt = Integer.MAX_VALUE;
        int minInt = Integer.MIN_VALUE;
        assertTrue(maxInt > minInt);

        // long 范围
        long maxLong = Long.MAX_VALUE;
        assertTrue(maxLong > maxInt);
    }

    @Test
    @DisplayName("类型转换测试")
    void testTypeConversion() {
        // 自动转换
        int i = 100;
        double d = i;  // int -> double
        assertEquals(100.0, d);

        // 强制转换
        double pi = 3.14159;
        int truncated = (int) pi;  // double -> int
        assertEquals(3, truncated);
    }

    // ===== 运算符测试 =====

    @Test
    @DisplayName("算术运算符测试")
    void testArithmeticOperators() {
        int a = 10, b = 3;

        assertEquals(13, a + b);
        assertEquals(7, a - b);
        assertEquals(30, a * b);
        assertEquals(3, a / b);  // 整数除法
        assertEquals(1, a % b);
    }

    @Test
    @DisplayName("三元运算符测试")
    void testTernaryOperator() {
        int score = 85;
        String grade = score >= 90 ? "A" : (score >= 80 ? "B" : "C");
        assertEquals("B", grade);
    }

    // ===== 字符串测试 =====

    @Test
    @DisplayName("字符串不可变性测试")
    void testStringImmutability() {
        String original = "Hello";
        String modified = original + " World";

        // 原字符串不变
        assertEquals("Hello", original);
        // 新字符串是新对象
        assertEquals("Hello World", modified);
        assertNotSame(original, modified);
    }

    @Test
    @DisplayName("StringBuilder 测试")
    void testStringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hello");
        sb.append(" ");
        sb.append("World");

        assertEquals("Hello World", sb.toString());

        // 链式调用
        StringBuilder sb2 = new StringBuilder()
                .append("A").append("B").append("C");
        assertEquals("ABC", sb2.toString());
    }

    // ===== 数组测试 =====

    @Test
    @DisplayName("数组操作测试")
    void testArrayOperations() {
        int[] numbers = {1, 2, 3, 4, 5};

        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);
    }

    @Test
    @DisplayName("二维数组测试")
    void test2DArray() {
        int[][] matrix = {
                {1, 2, 3},
                {4, 5, 6},
                {7, 8, 9}
        };

        assertEquals(3, matrix.length);       // 行数
        assertEquals(3, matrix[0].length);    // 列数
        assertEquals(5, matrix[1][1]);        // 中间元素
    }

    // ===== 集合测试 =====

    @Test
    @DisplayName("List 操作测试")
    void testListOperations() {
        List<String> list = new java.util.ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        assertEquals(3, list.size());
        assertEquals("A", list.get(0));
        assertTrue(list.contains("B"));

        list.remove(0);
        assertEquals(2, list.size());
        assertEquals("B", list.get(0));
    }

    @Test
    @DisplayName("Map 操作测试")
    void testMapOperations() {
        Map<String, Integer> map = new java.util.HashMap<>();
        map.put("A", 1);
        map.put("B", 2);
        map.put("C", 3);

        assertEquals(3, map.size());
        assertEquals(1, map.get("A"));
        assertTrue(map.containsKey("B"));
        assertTrue(map.containsValue(3));

        map.remove("A");
        assertEquals(2, map.size());
    }

    // ===== Stream API 测试 =====

    @Test
    @DisplayName("Stream filter 测试")
    void testStreamFilter() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)
                .toList();

        assertEquals(5, evens.size());
        assertTrue(evens.contains(2));
        assertTrue(evens.contains(4));
        assertFalse(evens.contains(3));
    }

    @Test
    @DisplayName("Stream map 测试")
    void testStreamMap() {
        List<Integer> numbers = Arrays.asList(1, 2, 3);

        List<Integer> doubled = numbers.stream()
                .map(n -> n * 2)
                .toList();

        assertEquals(Arrays.asList(2, 4, 6), doubled);
    }

    @Test
    @DisplayName("Stream reduce 测试")
    void testStreamReduce() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

        int sum = numbers.stream()
                .reduce(0, Integer::sum);

        assertEquals(15, sum);
    }

    @Test
    @DisplayName("Stream collect 分组测试")
    void testStreamCollectGrouping() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        Map<String, List<Integer>> grouped = numbers.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        n -> n % 2 == 0 ? "偶数" : "奇数"
                ));

        assertEquals(3, grouped.get("偶数").size());
        assertEquals(3, grouped.get("奇数").size());
    }

    // ===== 异常测试 =====

    @Test
    @DisplayName("异常测试")
    void testException() {
        // 断言抛出特定异常
        assertThrows(ArithmeticException.class, () -> {
            int result = 1 / 0;  // 除以零
        });

        // 断言不抛出异常
        assertDoesNotThrow(() -> {
            int result = 10 / 2;  // 正常除法
            assertEquals(5, result);
        });
    }

    // ===== 封装测试 =====

    @Test
    @DisplayName("封装测试 - User 类")
    void testEncapsulation() {
        BasicsOOPDemo.User user = new BasicsOOPDemo.User();
        user.setName("张三");
        user.setEmail("zhang@example.com");

        assertEquals("张三", user.getName());
        assertEquals("zhang@example.com", user.getEmail());
    }

    // ===== 继承测试 =====

    @Test
    @DisplayName("继承测试 - Dog 类")
    void testInheritance() {
        BasicsOOPDemo.Dog dog = new BasicsOOPDemo.Dog("旺财");

        // Dog 继承自 Animal，有 name 属性
        assertEquals("旺财", dog.getName());
    }

    // ===== BigDecimal 测试 =====

    @Test
    @DisplayName("BigDecimal 精确计算测试")
    void testBigDecimal() {
        java.math.BigDecimal a = new java.math.BigDecimal("0.1");
        java.math.BigDecimal b = new java.math.BigDecimal("0.2");
        java.math.BigDecimal sum = a.add(b);

        // 精确结果
        assertEquals(new java.math.BigDecimal("0.3"), sum);

        // 对比 double 的精度问题
        assertFalse(0.1 + 0.2 == 0.3);  // double 有精度问题
    }

    // ===== Lambda 测试 =====

    @Test
    @DisplayName("Lambda 表达式测试")
    void testLambda() {
        List<String> names = Arrays.asList("张三", "李四", "王五");

        // 使用 Lambda 过滤
        List<String> filtered = names.stream()
                .filter(name -> name.startsWith("张"))
                .toList();

        assertEquals(1, filtered.size());
        assertEquals("张三", filtered.get(0));
    }

    // ===== 其他断言 =====

    @Test
    @DisplayName("组合断言测试")
    void testAssertAll() {
        // 分组断言，所有断言都执行
        assertAll(
                () -> assertEquals(2, 1 + 1),
                () -> assertTrue(3 > 2),
                () -> assertNotNull("Hello"),
                () -> assertEquals("abc", "abc")
        );
    }

    @Test
    @DisplayName("数组相等断言")
    void testArrayEquals() {
        int[] expected = {1, 2, 3};
        int[] actual = {1, 2, 3};

        assertArrayEquals(expected, actual);
    }

    // ===== 多态测试 =====

    @Test
    @DisplayName("多态测试 - 方法重写")
    void testPolymorphism() {
        BasicsOOPDemo.Shape circle = new BasicsOOPDemo.Circle(2.0);
        BasicsOOPDemo.Shape rectangle = new BasicsOOPDemo.Rectangle(3.0, 4.0);

        // 调用的是各自重写的 area() 方法
        double circleArea = circle.area();
        double rectangleArea = rectangle.area();

        assertEquals(Math.PI * 4, circleArea, 0.001);  // PI * r^2 = PI * 4
        assertEquals(12.0, rectangleArea);              // 3 * 4 = 12
    }

    @Test
    @DisplayName("多态测试 - 向上/向下转型")
    void testTypeCasting() {
        // 向上转型
        BasicsOOPDemo.Animal animal = new BasicsOOPDemo.Dog("旺财");
        assertEquals("旺财", animal.getName());

        // 向下转型
        if (animal instanceof BasicsOOPDemo.Dog) {
            BasicsOOPDemo.Dog dog = (BasicsOOPDemo.Dog) animal;
            assertNotNull(dog);
        }
    }

    // ===== 抽象类与接口测试 =====

    @Test
    @DisplayName("抽象类测试")
    void testAbstractClass() {
        BasicsOOPDemo.Shape circle = new BasicsOOPDemo.Circle(3.0);
        double area = circle.area();
        assertEquals(Math.PI * 9, area, 0.001);
    }

    @Test
    @DisplayName("接口测试")
    void testInterface() {
        BasicsOOPDemo.Flyable bird = new BasicsOOPDemo.Bird();

        // 调用接口方法
        bird.fly();

        // 调用默认方法
        bird.land();  // 默认方法
    }

    // ===== Objects 工具类测试 =====

    @Test
    @DisplayName("Objects 工具类测试")
    void testObjects() {
        String nullStr = null;
        String notNullStr = "Hello";

        assertTrue(java.util.Objects.isNull(nullStr));
        assertTrue(java.util.Objects.nonNull(notNullStr));
        assertFalse(java.util.Objects.isNull(notNullStr));

        // toString with default
        assertEquals("default", java.util.Objects.toString(nullStr, "default"));
        assertEquals("Hello", java.util.Objects.toString(notNullStr, "default"));

        // hashCode
        assertEquals(notNullStr.hashCode(), java.util.Objects.hashCode(notNullStr));
    }

    // ===== Collections 工具类测试 =====

    @Test
    @DisplayName("Collections 工具类测试")
    void testCollections() {
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add("C");
        list.add("A");
        list.add("B");

        // sort
        java.util.Collections.sort(list);
        assertEquals(Arrays.asList("A", "B", "C"), list);

        // reverse
        java.util.Collections.reverse(list);
        assertEquals(Arrays.asList("C", "B", "A"), list);

        // max/min
        assertEquals("C", java.util.Collections.max(list));
        assertEquals("A", java.util.Collections.min(list));

        // frequency
        list.add("A");
        assertEquals(2, java.util.Collections.frequency(list, "A"));

        // binarySearch (需要已排序且无重复)
        java.util.Collections.sort(list);
        assertEquals(2, java.util.Collections.binarySearch(list, "B"));
    }

    @Test
    @DisplayName("Collections 只读集合测试")
    void testUnmodifiableCollections() {
        java.util.List<String> original = new java.util.ArrayList<>();
        original.add("A");

        // 只读集合
        java.util.List<String> unmodifiable = java.util.Collections.unmodifiableList(original);

        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            unmodifiable.add("B");
        });
    }

    // ===== Math 工具类测试 =====

    @Test
    @DisplayName("Math 工具类测试")
    void testMath() {
        assertEquals(5, java.lang.Math.abs(-5));
        assertEquals(4.0, java.lang.Math.sqrt(16));
        assertEquals(8.0, java.lang.Math.pow(2, 3));
        assertEquals(3, java.lang.Math.max(1, 3));
        assertEquals(1, java.lang.Math.min(1, 3));
        assertEquals(4.0, java.lang.Math.round(3.6));
        assertTrue(java.lang.Math.random() >= 0);
        assertTrue(java.lang.Math.random() < 1);
    }

    // ===== 方法重载测试 =====

    @Test
    @DisplayName("方法重载测试")
    void testMethodOverloading() {
        BasicsOOPDemo.Calculator calc = new BasicsOOPDemo.Calculator();

        assertEquals(3, calc.add(1, 2));
        assertEquals(6, calc.add(1, 2, 3));
    }

    // ===== 边界条件测试 =====

    @Test
    @DisplayName("边界测试 - 整数溢出")
    void testIntegerOverflow() {
        int maxInt = Integer.MAX_VALUE;  // 2147483647
        int minInt = Integer.MIN_VALUE;  // -2147483648

        // 溢出后循环
        assertEquals(Integer.MIN_VALUE, maxInt + 1);
        assertEquals(Integer.MAX_VALUE, minInt - 1);
    }

    @Test
    @DisplayName("边界测试 - 浮点数精度")
    void testFloatingPointPrecision() {
        // double 的精度问题
        double result = 0.1 + 0.2;
        assertNotEquals(0.3, result, 0.0);  // 不相等！

        // 使用 BigDecimal 解决
        java.math.BigDecimal bd1 = new java.math.BigDecimal("0.1");
        java.math.BigDecimal bd2 = new java.math.BigDecimal("0.2");
        java.math.BigDecimal bdResult = bd1.add(bd2);
        assertEquals(new java.math.BigDecimal("0.3"), bdResult);
    }

    @Test
    @DisplayName("边界测试 - 字符边界值")
    void testCharBoundary() {
        char minChar = Character.MIN_VALUE;      // 0
        char maxChar = Character.MAX_VALUE;      // 65535
        char letterA = 'A';

        assertEquals(65, (int) letterA);         // ASCII 码
        assertTrue(letterA >= 'A' && letterA <= 'Z');
    }

    @Test
    @DisplayName("边界测试 - 数组边界")
    void testArrayBoundary() {
        int[] arr = {1, 2, 3};

        assertEquals(1, arr[0]);    // 第一个元素
        assertEquals(3, arr[2]);    // 最后一个元素

        // 越界会抛出异常
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            int x = arr[3];
        });
    }

    @Test
    @DisplayName("边界测试 - 空集合操作")
    void testEmptyCollection() {
        java.util.List<String> emptyList = java.util.Collections.emptyList();

        assertTrue(emptyList.isEmpty());
        assertEquals(0, emptyList.size());

        // 只读集合不能修改
        assertThrows(java.lang.UnsupportedOperationException.class, () -> {
            emptyList.add("A");
        });
    }

    @Test
    @DisplayName("边界测试 - 字符串边界")
    void testStringBoundary() {
        String empty = "";
        String singleChar = "A";
        String longStr = "1234567890";

        assertEquals(0, empty.length());
        assertEquals(1, singleChar.length());
        assertEquals(10, longStr.length());

        // 空字符串的比较
        assertEquals("", empty);
        assertTrue(empty.isEmpty());
    }

    // ===== 错误处理测试 =====

    @Test
    @DisplayName("错误测试 - 空指针异常")
    void testNullPointerException() {
        String nullStr = null;

        assertThrows(NullPointerException.class, () -> {
            nullStr.length();
        });
    }

    @Test
    @DisplayName("错误测试 - 数字格式异常")
    void testNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("abc");
        });

        assertThrows(NumberFormatException.class, () -> {
            Integer.parseInt("123.45");
        });
    }

    @Test
    @DisplayName("错误测试 - 非法参数异常")
    void testIllegalArgumentException() {
        // 自定义异常
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("参数无效");
        });
    }

    @Test
    @DisplayName("错误测试 - 算术异常")
    void testArithmeticException() {
        // 除以零
        assertThrows(ArithmeticException.class, () -> {
            int x = 1 / 0;
        });

        // 取模为零
        assertThrows(ArithmeticException.class, () -> {
            int x = 1 % 0;
        });
    }

    @Test
    @DisplayName("错误测试 - 字符串索引越界")
    void testStringIndexOutOfBoundsException() {
        String str = "Hello";

        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            char c = str.charAt(10);
        });

        assertEquals('H', str.charAt(0));
        assertEquals('o', str.charAt(4));
    }

    // ===== 字符串方法测试 =====

    @Test
    @DisplayName("字符串方法测试")
    void testStringMethods() {
        String str = "Hello, World!";

        assertEquals(13, str.length());
        assertEquals('H', str.charAt(0));
        assertEquals("World", str.substring(7, 12));
        assertEquals("HELLO, WORLD!", str.toUpperCase());
        assertEquals("hello, world!", str.toLowerCase());
        assertTrue(str.contains("World"));
        assertTrue(str.startsWith("Hello"));
        assertTrue(str.endsWith("!"));
        assertEquals("Hello", str.split(", ")[0]);
        assertEquals("Hello, Java!", "Hello, World!".replace("World", "Java"));
    }

    @Test
    @DisplayName("字符串 trim 和 strip")
    void testStringTrim() {
        String withSpaces = "  Hello  ";
        String withTabs = "\tHello\t";

        assertEquals("Hello", withSpaces.trim());
        assertEquals("Hello", withTabs.trim());

        // Java 11+ strip() 更好（处理 Unicode）
        assertEquals("Hello", withSpaces.strip());
    }

    // ===== 进制转换测试 =====

    @Test
    @DisplayName("进制转换测试")
    void testRadixConversion() {
        // 十进制转其他进制
        assertEquals("1010", Integer.toBinaryString(10));
        assertEquals("12", Integer.toOctalString(10));
        assertEquals("a", Integer.toHexString(10));  // 小写字母

        // 其他进制转十进制
        assertEquals(10, Integer.parseInt("1010", 2));
        assertEquals(10, Integer.parseInt("12", 8));
        assertEquals(10, Integer.parseInt("a", 16));
    }

    // ===== 随机数测试 =====

    @Test
    @DisplayName("随机数测试")
    void testRandomNumbers() {
        java.util.Random random = new java.util.Random(42);  // 固定种子

        // 下一个随机整数
        int nextInt = random.nextInt();
        assertTrue(nextInt != random.nextInt());  // 每次不同（除非种子相同）

        // 指定范围的随机数
        int rand0to99 = random.nextInt(100);  // 0-99
        assertTrue(rand0to99 >= 0 && rand0to99 < 100);

        // 随机布尔值
        boolean randBool = random.nextBoolean();
        assertTrue(randBool == true || randBool == false);

        // 固定种子产生相同序列
        java.util.Random random1 = new java.util.Random(123);
        java.util.Random random2 = new java.util.Random(123);
        assertEquals(random1.nextInt(), random2.nextInt());
    }

    @AfterEach
    void teardown() {
        // 每个测试后执行
    }

    @AfterAll
    static void teardownAll() {
        // 所有测试后执行一次
    }
}
