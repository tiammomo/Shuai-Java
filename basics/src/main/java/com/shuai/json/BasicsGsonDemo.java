package com.shuai.json;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

/**
 * Gson JSON 序列化演示
 *
 * @author Shuai
 * @version 1.0
 */
public class BasicsGsonDemo {

    public void runAllDemos() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       Gson JSON 序列化");
        System.out.println("=".repeat(50));

        basicSerialization();
        deserialization();
        typeToken();
        annotations();
        customConfiguration();
    }

    /**
     * 基本序列化
     */
    private void basicSerialization() {
        System.out.println("\n--- 基本序列化 ---");

        System.out.println("\n  对象转 JSON:");
        System.out.println("    Gson gson = new Gson();");
        System.out.println("    String json = gson.toJson(user);");

        User user = new User("zhangsan", 25, "zhangsan@example.com");

        System.out.println("    User user = new User(\"zhangsan\", 25);");
        System.out.println("    结果: " + user);

        System.out.println("\n  集合转 JSON:");
        List<String> list = Lists.newArrayList("a", "b", "c");
        System.out.println("    List: " + list);

        System.out.println("\n  Map 转 JSON:");
        Map<String, Integer> map = Maps.newHashMap();
        map.put("Java", 1);
        map.put("Go", 2);
        System.out.println("    Map: " + map);
    }

    /**
     * 反序列化
     */
    private void deserialization() {
        System.out.println("\n--- 反序列化 ---");

        System.out.println("\n  JSON 转对象:");
        System.out.println("    String json = \"{\\\"name\\\":\\\"zhangsan\\\",\\\"age\\\":25}\";");
        System.out.println("    User user = gson.fromJson(json, User.class);");

        String json = "{\"name\":\"zhangsan\",\"age\":25}";
        System.out.println("    输入: " + json);

        System.out.println("\n  JSON 转集合:");
        System.out.println("    Type listType = new TypeToken<List<String>>(){}.getType();");
        System.out.println("    List<String> list = gson.fromJson(json, listType);");

        System.out.println("\n  JSON 转数组:");
        System.out.println("    String arrayJson = \"[\\\"a\\\",\\\"b\\\",\\\"c\\\"]\";");
        System.out.println("    String[] array = gson.fromJson(arrayJson, String[].class);");
    }

    /**
     * TypeToken
     */
    private void typeToken() {
        System.out.println("\n--- TypeToken (泛型反序列化) ---");

        System.out.println("\n  泛型类型获取:");
        System.out.println("    Type type = new TypeToken<List<String>>(){}.getType();");
        System.out.println("    Type mapType = new TypeToken<Map<String, User>>(){}.getType();");

        System.out.println("\n  常用类型:");
        System.out.println("    List<String>     -> TypeToken<List<String>>{}.getType()");
        System.out.println("    Set<User>        -> TypeToken<Set<User>>{}.getType()");
        System.out.println("    Map<String,T>    -> TypeToken<Map<String,T>>{}.getType()");
        System.out.println("    List<List<T>>    -> TypeToken<List<List<T>>>{}.getType()");

        System.out.println("\n(");
        System.out.println("    // 自定义泛型类");
        System.out.println("    class ApiResponse<T> { T data; int code; }");
        System.out.println("    Type type = new TypeToken<ApiResponse<List<User>>>(){}.getType();");
        System.out.println("    ApiResponse<List<User>> response = gson.fromJson(json, type);");
    }

    /**
     * 注解
     */
    private void annotations() {
        System.out.println("\n--- Gson 注解 ---");

        System.out.println("\n  @SerializedName:");
        System.out.println("    @SerializedName(\"user_name\")");
        System.out.println("    private String name;");
        System.out.println("    // JSON 中使用 user_name 而非 name");

        System.out.println("\n  @Expose:");
        System.out.println("    @Expose");
        System.out.println("    private String name;");
        System.out.println("    // 需要配合 GsonBuilder.excludeFieldsWithoutExposeAnnotation()");

        System.out.println("\n  @Since / @Until:");
        System.out.println("    @Since(1.0)  private String field1;");
        System.out.println("    @Until(2.0)  private String field2;");
        System.out.println("    // 配合 GsonBuilder.setVersion() 使用");
    }

    /**
     * 自定义配置
     */
    private void customConfiguration() {
        System.out.println("\n--- GsonBuilder 配置 ---");

        System.out.println("\n  常用配置:");
        System.out.println("    Gson gson = new GsonBuilder()");
        System.out.println("        .setDateFormat(\"yyyy-MM-dd HH:mm:ss\")  // 日期格式");
        System.out.println("        .setPrettyPrinting()                     // 格式化输出");
        System.out.println("        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)");
        System.out.println("        .serializeNulls()                         // 序列化 null");
        System.out.println("        .excludeFieldsWithoutExposeAnnotation()   // 排除无 @Expose");
        System.out.println("        .setVersion(1.0)                          // 版本控制");
        System.out.println("        .create();");

        System.out.println("\n  FieldNamingPolicy:");
        System.out.println("    DEFAULT           - 默认");
        System.out.println("    LOWER_CASE_WITH_UNDERSCORES   - snake_case");
        System.out.println("    LOWER_CAMEL_CASE              - lowerCamelCase");
        System.out.println("    UPPER_CAMEL_CASE              - UpperCamelCase");
        System.out.println("    UPPER_CASE_WITH_UNDERSCORES   - UPPER_SNAKE_CASE");

        System.out.println("\n  自定义序列化器:");
        System.out.println("    Gson gson = new GsonBuilder()");
        System.out.println("        .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {");
        System.out.println("            public JsonElement serialize(LocalDateTime src, Type type, JsonSerializationContext context) {");
        System.out.println("                return new JsonPrimitive(src.toString());");
        System.out.println("            }");
        System.out.println("        })");
        System.out.println("        .create();");
    }

    // 内部类用于演示
    static class User {
        String name;
        int age;
        String email;

        User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }
}
