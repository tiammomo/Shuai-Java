package com.shuai.json;

/**
 * Java JSON 处理演示类
 *
 * 涵盖内容：
 * - JSON 基本概念和语法
 * - JSON 数据结构
 * - 手动 JSON 解析
 * - 常见 JSON 库介绍
 * - JSON 与 Java 对象转换
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsJsonDemo {

    /**
     * 执行所有 JSON 演示
     */
    public void runAllDemos() {
        jsonBasicConcepts();
        jsonStructure();
        manualJsonParsing();
        jsonLibraries();
        jsonWithObjects();
    }

    /**
     * 演示 JSON 基本概念
     */
    private void jsonBasicConcepts() {
        System.out.println("\n--- JSON 基本概念 ---");

        System.out.println("\n  JSON (JavaScript Object Notation) 是一种轻量级数据交换格式。");
        System.out.println("  它易于阅读和编写，同时也易于机器解析和生成。");

        System.out.println("\n  JSON 的特点:");
        System.out.println("    - 独立于语言：几乎所有语言都有 JSON 解析库");
        System.out.println("    - 易于理解：基于文本，结构清晰");
        System.out.println("    - 体积小：相比 XML 更紧凑");
        System.out.println("    - 类型安全：支持基本数据类型");

        System.out.println("\n  JSON 的应用场景:");
        System.out.println("    - Web API 数据交换");
        System.out.println("    - 配置文件（如 package.json）");
        System.out.println("    - 前后端数据通信");
        System.out.println("    - 数据存储和缓存");
    }

    /**
     * 演示 JSON 数据结构
     */
    private void jsonStructure() {
        System.out.println("\n--- JSON 数据结构 ---");

        System.out.println("\n  1. 对象（Object）:");
        System.out.println("    使用大括号 {} 表示");
        System.out.println("    由键值对（key: value）组成");
        System.out.println("    键必须是字符串，必须用双引号");
        System.out.println("    ");
        System.out.println("    {");
        System.out.println("      \"name\": \"张三\",");
        System.out.println("      \"age\": 25,");
        System.out.println("      \"city\": \"北京\"");
        System.out.println("    }");

        System.out.println("\n  2. 数组（Array）:");
        System.out.println("    使用方括号 [] 表示");
        System.out.println("    可以包含任意类型的值");
        System.out.println("    ");
        System.out.println("    [");
        System.out.println("      \"苹果\",");
        System.out.println("      \"香蕉\",");
        System.out.println("      \"橙子\"");
        System.out.println("    ]");

        System.out.println("\n  3. 值（Value）:");
        System.out.println("    字符串（必须用双引号）: \"Hello\"");
        System.out.println("    数字: 123, 3.14, -10");
        System.out.println("    布尔值: true, false");
        System.out.println("    空值: null");
        System.out.println("    对象: {\"key\": \"value\"}");
        System.out.println("    数组: [1, 2, 3]");

        System.out.println("\n  4. 复杂示例:");
        System.out.println("    {");
        System.out.println("      \"users\": [");
        System.out.println("        {");
        System.out.println("          \"id\": 1,");
        System.out.println("          \"name\": \"张三\",");
        System.out.println("          \"scores\": [85, 90, 78],");
        System.out.println("          \"active\": true");
        System.out.println("        },");
        System.out.println("        {");
        System.out.println("          \"id\": 2,");
        System.out.println("          \"name\": \"李四\",");
        System.out.println("          \"scores\": [92, 88, 95],");
        System.out.println("          \"active\": false");
        System.out.println("        }");
        System.out.println("      ],");
        System.out.println("      \"total\": 2");
        System.out.println("    }");
    }

    /**
     * 演示手动 JSON 解析
     */
    private void manualJsonParsing() {
        System.out.println("\n--- 手动 JSON 解析 ---");

        System.out.println("\n  简单 JSON 字符串解析:");

        // 示例 JSON
        String json = "{\"name\":\"张三\",\"age\":25,\"city\":\"北京\"}";
        System.out.println("  原字符串: " + json);

        // 解析键值对
        System.out.println("\n  解析方法:");
        System.out.println("    // 找到 key 的位置");
        System.out.println("    int keyStart = json.indexOf(\"\\\"name\\\":\") + 7;");
        System.out.println("    int keyEnd = json.indexOf(\"\\\"\", keyStart);");
        System.out.println("    String value = json.substring(keyStart, keyEnd);");

        System.out.println("\n  解析数字:");

        String ageJson = "{\"age\":25}";
        System.out.println("  原字符串: " + ageJson);
        System.out.println("  解析步骤:");
        System.out.println("    1. 找到 \"age\": 的位置");
        System.out.println("    2. 从冒号后开始解析数字");
        System.out.println("    3. 遇到非数字字符停止");

        System.out.println("\n  解析布尔值:");

        String boolJson = "{\"active\":true,\"deleted\":false}";
        System.out.println("  原字符串: " + boolJson);
        System.out.println("  判断是否包含 \"active\":true");

        System.out.println("\n  解析数组:");

        String arrayJson = "{\"scores\":[85,90,78]}";
        System.out.println("  原字符串: " + arrayJson);
        System.out.println("  解析步骤:");
        System.out.println("    1. 找到 \"[\" 和 \"]\" 的位置");
        System.out.println("    2. 提取中间内容");
        System.out.println("    3. 按逗号分割");

        System.out.println("\n  解析嵌套对象:");

        String nestedJson = "{\"user\":{\"name\":\"张三\",\"age\":25}}";
        System.out.println("  原字符串: " + nestedJson);
        System.out.println("  解析步骤:");
        System.out.println("    1. 先找到外层对象内容");
        System.out.println("    2. 再解析内部对象");
        System.out.println("    System.out.println(\"外部: \" + outerContent);");
        System.out.println("    System.out.println(\"内部: \" + innerContent);");

        System.out.println("\n  注意事项:");
        System.out.println("    - 注意转义字符（\\n, \\t, \\\\\" 等）");
        System.out.println("    - 处理嵌套层级");
        System.out.println("    - 处理数组中的对象");
        System.out.println("    - 实际开发中建议使用专业 JSON 库");
    }

    /**
     * 演示常见 JSON 库
     */
    private void jsonLibraries() {
        System.out.println("\n--- 常见 JSON 库 ---");

        System.out.println("\n  1. Jackson（最流行）:");
        System.out.println("    https://github.com/FasterXML/jackson");
        System.out.println("    功能强大，性能好");
        System.out.println("    Spring Boot 默认使用");
        System.out.println("    ");
        System.out.println("    Maven 依赖:");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>com.fasterxml.jackson.core</groupId>");
        System.out.println("        <artifactId>jackson-databind</artifactId>");
        System.out.println("        <version>2.15.0</version>");
        System.out.println("    </dependency>");

        System.out.println("\n  2. Gson（Google）:");
        System.out.println("    https://github.com/google/gson");
        System.out.println("    API 简洁，易于使用");
        System.out.println("    对泛型支持好");
        System.out.println("    ");
        System.out.println("    Maven 依赖:");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>com.google.code.gson</groupId>");
        System.out.println("        <artifactId>gson</artifactId>");
        System.out.println("        <version>2.10.1</version>");
        System.out.println("    </dependency>");

        System.out.println("\n  3. Fastjson（阿里）:");
        System.out.println("    https://github.com/alibaba/fastjson");
        System.out.println("    国内使用广泛");
        System.out.println("    性能优异");
        System.out.println("    注意安全漏洞问题");
        System.out.println("    ");
        System.out.println("    Maven 依赖:");
        System.out.println("    <dependency>");
        System.out.println("        <groupId>com.alibaba.fastjson2</groupId>");
        System.out.println("        <artifactId>fastjson2</artifactId>");
        System.out.println("        <version>2.0.28</version>");
        System.out.println("    </dependency>");

        System.out.println("\n  4. JSON-B（JAX-B 标准）:");
        System.out.println("    Java EE 8+ 标准");
        System.out.println("    统一的 API");
        System.out.println("    实现包括: Eclipse Yasson, Oracle JSON-B");

        System.out.println("\n  库对比:");
        System.out.println("    | 特性      | Jackson | Gson  | Fastjson |");
        System.out.println("    |-----------|---------|-------|----------|");
        System.out.println("    | 性能       | 高      | 中    | 高       |");
        System.out.println("    | API 友好度 | 中      | 高    | 高       |");
        System.out.println("    | 社区活跃度 | 高      | 中    | 中       |");
        System.out.println("    | Spring 集成 | 原生   | 需配置 | 需配置   |");
    }

    /**
     * 演示 JSON 与 Java 对象转换
     */
    private void jsonWithObjects() {
        System.out.println("\n--- JSON 与 Java 对象转换 ---");

        System.out.println("\n  Jackson 基本用法:");

        System.out.println("    // 创建 ObjectMapper");
        System.out.println("    ObjectMapper mapper = new ObjectMapper();");
        System.out.println("");
        System.out.println("    // Java 对象转 JSON 字符串");
        System.out.println("    User user = new User(\"张三\", 25);");
        System.out.println("    String json = mapper.writeValueAsString(user);");
        System.out.println("    // {\"name\":\"张三\",\"age\":25}");
        System.out.println("");
        System.out.println("    // JSON 字符串转 Java 对象");
        System.out.println("    User result = mapper.readValue(json, User.class);");
        System.out.println("");
        System.out.println("    // 忽略未知属性");
        System.out.println("    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);");
        System.out.println("");
        System.out.println("    // 处理日期格式");
        System.out.println("    mapper.setDateFormat(new SimpleDateFormat(\"yyyy-MM-dd\"));");

        System.out.println("\n  Gson 基本用法:");
        System.out.println("    // Gson 详细用法请参考 BasicsGsonDemo.java");
        System.out.println("    // 包含：TypeToken、注解、自定义序列化器等高级功能");

        System.out.println("\n  JSON 数组处理:");

        System.out.println("    // JSON 数组转 List");
        System.out.println("    String arrayJson = \"[\\\"苹果\\\",\\\"香蕉\\\",\\\"橙子\\\"]\";");
        System.out.println("    List<String> list = Arrays.asList(mapper.readValue(arrayJson, String[].class));");
        System.out.println("");
        System.out.println("    // JSON 数组转 List<User>");
        System.out.println("    String usersJson = \"[{\\\"name\\\":\\\"张三\\\"},{\\\"name\\\":\\\"李四\\\"}]\";");
        System.out.println("    List<User> users = mapper.readValue(usersJson, new TypeReference<List<User>>() {});");

        System.out.println("\n  常用注解:");

        System.out.println("    @JsonProperty(\"user_name\")  // 指定 JSON 属性名");
        System.out.println("    @JsonIgnore                 // 忽略该字段");
        System.out.println("    @JsonIgnoreProperties(ignoreUnknown = true)  // 忽略未知属性");
        System.out.println("    @JsonFormat(pattern = \"yyyy-MM-dd\")  // 日期格式");
        System.out.println("    @JsonInclude(Include.NON_NULL)  // 排除 null 值");
    }
}
