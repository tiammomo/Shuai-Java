package com.shuai.json;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON 处理测试类
 */
@DisplayName("JSON 测试")
class BasicsJsonTest {

    @Test
    @DisplayName("JSON 基本概念测试")
    void jsonConceptTest() {
        System.out.println("  JSON (JavaScript Object Notation) 是一种轻量级数据交换格式");
        System.out.println("");
        System.out.println("  JSON 特点:");
        System.out.println("    - 独立于语言");
        System.out.println("    - 易于阅读和编写");
        System.out.println("    - 易于机器解析和生成");
        System.out.println("    - 相比 XML 更紧凑");
        assertTrue(true);
    }

    @Test
    @DisplayName("JSON 数据结构测试")
    void jsonStructureTest() {
        System.out.println("  JSON 数据类型:");
        System.out.println("    - 对象: {\"key\": \"value\"}");
        System.out.println("    - 数组: [1, 2, 3]");
        System.out.println("    - 字符串: \"Hello\"");
        System.out.println("    - 数字: 123, 3.14");
        System.out.println("    - 布尔值: true, false");
        System.out.println("    - 空值: null");
        System.out.println("");
        System.out.println("  JSON 语法规则:");
        System.out.println("    - 键必须用双引号");
        System.out.println("    - 值可以是任意类型");
        System.out.println("    - 最后一项不能有逗号");
        assertTrue(true);
    }

    @Test
    @DisplayName("Jackson 库测试")
    void jacksonTest() {
        System.out.println("  Jackson 常用类:");
        System.out.println("    - ObjectMapper: 核心对象");
        System.out.println("    - JsonNode: JSON 节点");
        System.out.println("    - JsonParser: JSON 解析器");
        System.out.println("");
        System.out.println("  ObjectMapper 基本用法:");
        System.out.println("    // 对象转 JSON");
        System.out.println("    String json = mapper.writeValueAsString(user);");
        System.out.println("    // JSON 转对象");
        System.out.println("    User user = mapper.readValue(json, User.class);");
        System.out.println("    // JSON 转 List");
        System.out.println("    List<User> list = mapper.readValue(json, new TypeReference<List<User>>() {});");
        System.out.println("");
        System.out.println("  Jackson 常用注解:");
        System.out.println("    @JsonProperty: 指定属性名");
        System.out.println("    @JsonIgnore: 忽略字段");
        System.out.println("    @JsonFormat: 日期格式");
        System.out.println("    @JsonInclude: 包含策略");
        assertTrue(true);
    }

    @Test
    @DisplayName("Gson 库测试")
    void gsonTest() {
        System.out.println("  Gson 常用类:");
        System.out.println("    - Gson: 核心对象");
        System.out.println("    - GsonBuilder: 构建器");
        System.out.println("    - TypeToken: 泛型类型");
        System.out.println("");
        System.out.println("  Gson 基本用法:");
        System.out.println("    // 对象转 JSON");
        System.out.println("    String json = new Gson().toJson(user);");
        System.out.println("    // JSON 转对象");
        System.out.println("    User user = new Gson().fromJson(json, User.class);");
        System.out.println("    // JSON 转 List");
        System.out.println("    Type type = new TypeToken<List<User>>(){}.getType();");
        System.out.println("    List<User> list = new Gson().fromJson(json, type);");
        System.out.println("");
        System.out.println("  Gson 特点:");
        System.out.println("    - API 简洁");
        System.out.println("    - 对泛型支持好");
        System.out.println("    - 无需注解即可工作");
        assertTrue(true);
    }

    @Test
    @DisplayName("Fastjson 库测试")
    void fastjsonTest() {
        System.out.println("  Fastjson 常用类:");
        System.out.println("    - JSONObject: JSON 对象");
        System.out.println("    - JSONArray: JSON 数组");
        System.out.println("    - JSON: 静态工具类");
        System.out.println("");
        System.out.println("  Fastjson 基本用法:");
        System.out.println("    // 对象转 JSON");
        System.out.println("    String json = JSON.toJSONString(user);");
        System.out.println("    // JSON 转对象");
        System.out.println("    User user = JSON.parseObject(json, User.class);");
        System.out.println("    // JSON 转 List");
        System.out.println("    List<User> list = JSON.parseArray(json, User.class);");
        System.out.println("");
        System.out.println("  Fastjson 特点:");
        System.out.println("    - 国内使用广泛");
        System.out.println("    - 性能优异");
        System.out.println("    - 注意安全漏洞问题（建议使用 fastjson2）");
        assertTrue(true);
    }

    @Test
    @DisplayName("JSON 注解测试")
    void jsonAnnotationsTest() {
        System.out.println("  Jackson 注解:");
        System.out.println("    @JsonProperty(\"user_name\")  // 指定 JSON 属性名");
        System.out.println("    @JsonIgnore                  // 忽略该字段");
        System.out.println("    @JsonIgnoreProperties(ignoreUnknown = true)  // 类级别忽略");
        System.out.println("    @JsonFormat(pattern = \"yyyy-MM-dd\")  // 日期格式");
        System.out.println("    @JsonInclude(Include.NON_NULL)  // 排除 null 值");
        System.out.println("    @JsonPropertyOrder({\"name\", \"age\"})  // 指定顺序");
        System.out.println("");
        System.out.println("  自定义序列化器:");
        System.out.println("    实现 JsonSerializer 接口");
        System.out.println("    实现 JsonDeserializer 接口");
        assertTrue(true);
    }

    @Test
    @DisplayName("JSON 最佳实践测试")
    void jsonBestPracticesTest() {
        System.out.println("  JSON 处理最佳实践:");
        System.out.println("    1. 使用预编译的 ObjectMapper/Gson（单例）");
        System.out.println("    2. 配置正确的日期格式");
        System.out.println("    3. 处理循环引用（@JsonManagedReference, @JsonBackReference）");
        System.out.println("    4. 验证输入 JSON（防止反序列化攻击）");
        System.out.println("    5. 使用泛型类型处理复杂结构");
        System.out.println("");
        System.out.println("  常见问题:");
        System.out.println("    - 未知属性: 配置 FAIL_ON_UNKNOWN_PROPERTIES = false");
        System.out.println("    - 日期解析: 设置统一的日期格式");
        System.out.println("    - 枚举序列化: 配置 @JsonFormat 或自定义序列化器");
        System.out.println("    - 空值处理: 配置 @JsonInclude 策略");
        assertTrue(true);
    }
}
