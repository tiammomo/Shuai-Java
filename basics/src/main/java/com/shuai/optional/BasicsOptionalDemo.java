package com.shuai.optional;

import java.util.*;

/**
 * Java Optional 演示类
 *
 * 涵盖内容：
 * - Optional 概念：解决空指针问题
 * - 创建 Optional：empty、of、ofNullable
 * - 判断是否存在：isPresent、isEmpty
 * - 获取值：get、orElse、orElseGet、orElseThrow
 * - 链式调用：map、flatMap、filter
 * - 高级用法：ifPresent、or
 * - 实际应用场景
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsOptionalDemo {

    /**
     * 执行所有 Optional 演示
     */
    public void runAllDemos() {
        optionalCreation();
        optionalCheck();
        optionalGet();
        optionalTransform();
        optionalChaining();
        practicalExamples();
    }

    /**
     * 演示 Optional 创建
     *
     * 三种创建方式：
     * - empty()：创建空 Optional
     * - of()：创建非空 Optional（值不能为 null）
     * - ofNullable()：创建可能为空的 Optional
     */
    private void optionalCreation() {
        // 空 Optional
        Optional<String> empty = Optional.empty();

        // 非空 Optional（值不能为 null，否则抛出 NullPointerException）
        Optional<String> nonEmpty = Optional.of("Hello");

        // 可能为空的 Optional（推荐使用）
        String nullableValue = null;
        Optional<String> nullable = Optional.ofNullable(nullableValue);

        // 使用 ofNullable 处理可能为 null 的值
        String value = null;
        Optional<String> opt = Optional.ofNullable(value);
    }

    /**
     * 演示 Optional 判断
     *
     * isPresent()：值是否存在
     * isEmpty()：值是否不存在（Java 11+）
     */
    private void optionalCheck() {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        // 判断值是否存在
        if (present.isPresent()) {
            // 值存在
        }

        if (empty.isPresent()) {
            // 不会执行
        }

        // Java 11+：判断值是否不存在
        if (empty.isEmpty()) {
            // 空 Optional
        }

        // 不推荐：直接使用 isPresent
        if (present.isPresent()) {
            String value = present.get();
        }
    }

    /**
     * 演示 Optional 获取值
     *
     * get()：获取值（可能抛出 NoSuchElementException）
     * orElse()：值为空时返回默认值
     * orElseGet()：值为空时调用 supplier 获取值
     * orElseThrow()：值为空时抛出异常
     */
    private void optionalGet() {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();

        // get()：获取值
        String value1 = present.get();  // "Hello"
        // String value2 = empty.get();  // NoSuchElementException

        // orElse()：空时返回默认值
        String value3 = empty.orElse("default");  // "default"
        String value4 = present.orElse("default");  // "Hello"

        // orElseGet()：空时调用 supplier
        String value5 = empty.orElseGet(() -> "generated");  // "generated"
        String value6 = present.orElseGet(() -> "generated");  // "Hello"

        // orElseThrow()：空时抛出指定异常
        // String value7 = empty.orElseThrow(() -> new RuntimeException("值为空"));
    }

    /**
     * 演示 Optional 转换
     *
     * map()：转换值（返回新的 Optional）
     * flatMap()：扁平化转换
     * filter()：条件过滤
     */
    private void optionalTransform() {
        Optional<String> present = Optional.of("hello");

        // map()：转换值
        Optional<Integer> lengthOpt = present.map(String::length);  // Optional[5]
        Optional<String> upperOpt = present.map(String::toUpperCase);  // Optional[HELLO]

        // filter()：条件过滤
        Optional<String> filteredOpt = present.filter(s -> s.length() > 3);  // Optional["hello"]
        Optional<String> filteredEmpty = present.filter(s -> s.length() > 10);  // Optional.empty

        // 空 Optional 的转换返回空 Optional
        Optional<String> empty = Optional.empty();
        Optional<Integer> emptyMap = empty.map(String::length);  // Optional.empty
    }

    /**
     * 演示 Optional 链式调用
     *
     * 链式使用 map、flatMap、filter
     */
    private void optionalChaining() {
        // 模拟获取用户地址
        User user = new User("张三", new Address("北京"));

        // 链式调用获取城市
        String city = Optional.ofNullable(user)
                .map(User::getAddress)
                .map(Address::getCity)
                .orElse("未知");

        // 嵌套 Optional 使用 flatMap
        Optional<User> userOpt = Optional.ofNullable(user);
        Optional<String> nestedCity = userOpt.flatMap(u -> Optional.ofNullable(u.getAddress()))
                .flatMap(a -> Optional.ofNullable(a.getCity()));

        // 条件过滤
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        Optional<List<Integer>> numbersOpt = Optional.ofNullable(numbers);
        Optional<List<Integer>> filteredNumbers = numbersOpt.filter(list -> !list.isEmpty());

        // 复杂的链式调用
        Optional<String> result = Optional.of("  hello world  ")
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .map(String::toUpperCase);

        // 结果处理
        result.ifPresent(System.out::println);
    }

    /**
     * 演示实际应用场景
     */
    private void practicalExamples() {
        // 场景1：从 Map 中安全获取值
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        // String value = map.get("key2");  // 可能返回 null
        String safeValue = Optional.ofNullable(map.get("key2")).orElse("默认值");

        // 场景2：处理集合中的空元素
        List<String> list = Arrays.asList("a", null, "b", null, "c");
        List<String> nonNullList = new java.util.ArrayList<>();
        for (String item : list) {
            Optional.ofNullable(item).ifPresent(nonNullList::add);
        }

        // 场景3：方法返回值
        String result = findName().orElse("Unknown");

        // 场景4：避免 NullPointerException
        // 之前：name.toUpperCase() 可能抛出 NPE
        // 之后：
        String name = null;
        Optional.ofNullable(name).map(String::toUpperCase).orElse(null);

        // 场景5：配置默认值
        Properties props = new Properties();
        String host = Optional.ofNullable(props.getProperty("host"))
                .orElse("localhost");
        int port = Integer.parseInt(
                Optional.ofNullable(props.getProperty("port")).orElse("8080")
        );

        // 场景6：流式处理
        List<User> users = Arrays.asList(
                new User("张三", new Address("北京")),
                new User("李四", null),
                new User("王五", new Address("上海"))
        );

        List<String> cities = new java.util.ArrayList<>();
        for (User user : users) {
            Optional.ofNullable(user)
                    .map(User::getAddress)
                    .map(Address::getCity)
                    .ifPresent(cities::add);
        }
    }

    /**
     * 查找名称（演示 Optional 作为返回值）
     * @return 名称的 Optional
     */
    private Optional<String> findName() {
        // 从数据库或其他来源获取
        return Optional.ofNullable("张三");
    }

    // 演示用内部类
    static class User {
        private String name;
        private Address address;

        public User(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public Address getAddress() {
            return address;
        }
    }

    static class Address {
        private String city;

        public Address(String city) {
            this.city = city;
        }

        public String getCity() {
            return city;
        }
    }
}
