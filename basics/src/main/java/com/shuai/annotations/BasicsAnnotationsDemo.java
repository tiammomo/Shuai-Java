package com.shuai.annotations;

import java.lang.annotation.*;
import java.lang.reflect.*;

/**
 * Java 注解演示类
 *
 * 涵盖内容：
 * - 注解概念：元数据标记
 * - 内置注解：@Override、@Deprecated、@SuppressWarnings 等
 * - 元注解：@Target、@Retention、@Documented、@Inherited
 * - 自定义注解：创建和使用自定义注解
 * - 注解处理器：反射获取注解信息
 * - 注解应用场景
 *
 * @author Java Basics Demo
 * @version 1.0.0
 * @since 1.0
 */
public class BasicsAnnotationsDemo {

    /**
     * 执行所有注解演示
     */
    public void runAllDemos() {
        builtInAnnotations();
        metaAnnotations();
        customAnnotations();
        annotationProcessor();
        practicalUsage();
    }

    /**
     * 演示内置注解
     *
     * Java 内置常用注解：
     * - @Override：方法重写标记
     * - @Deprecated：标记已废弃
     * - @SuppressWarnings：抑制警告
     * - @FunctionalInterface：函数式接口标记
     * - @SafeVarargs：安全可变参数（Java 7+）
     * - @Repeatable：可重复注解（Java 8+）
     */
    private void builtInAnnotations() {
        // @Override：方法重写标记
        // 编译器会检查是否真的重写了父类方法

        // @Deprecated：标记已废弃
        // 使用 @Deprecated 标记的方法/类会产生编译警告

        // @SuppressWarnings：抑制警告
        // @SuppressWarnings("unchecked") 抑制未检查警告
        // @SuppressWarnings("deprecation") 抑制废弃警告
        // @SuppressWarnings("unused") 抑制未使用警告
        @SuppressWarnings("unused")
        int unusedVar = 0;

        // @FunctionalInterface：函数式接口标记
        // 编译器会检查是否是有效的函数式接口
    }

    /**
     * 演示元注解
     *
     * 用于修饰注解的注解：
     * - @Target：注解可以应用的目标元素类型
     * - @Retention：注解的保留策略
     * - @Documented：注解是否包含在 Javadoc 中
     * - @Inherited：注解是否可继承
     */
    private void metaAnnotations() {
        // ElementType 取值：
        // - TYPE：类、接口、枚举
        // - FIELD：字段
        // - METHOD：方法
        // - PARAMETER：参数
        // - CONSTRUCTOR：构造器
        // - LOCAL_VARIABLE：局部变量
        // - ANNOTATION_TYPE：注解类型
        // - PACKAGE：包
        // - TYPE_PARAMETER：类型参数（Java 8+）
        // - TYPE_USE：类型使用（Java 8+）

        // RetentionPolicy 取值：
        // - SOURCE：仅在源码阶段保留
        // - CLASS：编译时保留
        // - RUNTIME：运行时保留（可通过反射获取）
    }

    /**
     * 演示自定义注解
     */
    private void customAnnotations() {
        // 使用自定义注解
        @MyAnnotation(name = "测试", value = "值")
        class AnnotatedClass {}

        // 使用带默认值的注解
        @AnnotationWithDefault
        class DefaultAnnotatedClass {}

        // 使用必需属性的注解
        @RequiredField(required = true)
        class RequiredFieldClass {}
    }

    /**
     * 演示注解处理器
     *
     * 通过反射获取注解信息
     */
    private void annotationProcessor() {
        // 使用 UserEntity 演示注解处理器
        Class<?> clazz = UserEntity.class;

        // 获取类的注解
        if (clazz.isAnnotationPresent(MyAnnotation.class)) {
            MyAnnotation annotation = clazz.getAnnotation(MyAnnotation.class);
            String name = annotation.name();
            String value = annotation.value();
        }

        // 获取字段的注解
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(FieldInfo.class)) {
                FieldInfo info = field.getAnnotation(FieldInfo.class);
                String description = info.description();
                int order = info.order();
            }
        }

        // 获取方法的注解
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MethodInfo.class)) {
                MethodInfo info = method.getAnnotation(MethodInfo.class);
                String author = info.author();
                String date = info.date();
                String description = info.description();
            }
        }

        // 获取构造器的注解
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(ConstructorInfo.class)) {
                ConstructorInfo info = constructor.getAnnotation(ConstructorInfo.class);
            }
        }
    }

    /**
     * 演示注解实际应用场景
     */
    private void practicalUsage() {
        // 场景1：数据验证
        // @NotNull、@Size、@Min、@Max 等

        // 场景2：JSON 序列化
        // @JsonProperty、@JsonIgnore 等

        // 场景3：数据库映射
        // @Entity、@Table、@Column 等

        // 场景4：API 文档
        // @ApiModel、@ApiModelProperty 等

        // 场景5：依赖注入
        // @Autowired、@Inject、@Resource 等

        // 场景6：路由配置
        // @RequestMapping、@GetMapping、@PostMapping 等
    }

    // ==================== 自定义注解定义 ====================

    /**
     * 自定义注解示例
     *
     * @Target(ElementType.TYPE)：可以应用于类、接口
     * @Retention(RetentionPolicy.RUNTIME)：运行时可获取
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MyAnnotation {
        String name() default "";  // 默认值为空字符串
        String value() default "";
    }

    /**
     * 带默认值的注解
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AnnotationWithDefault {
        String name() default "默认名称";
        int count() default 0;
        boolean enabled() default true;
    }

    /**
     * 必需字段注解
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface RequiredField {
        boolean required() default false;
    }

    /**
     * 字段信息注解
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface FieldInfo {
        String description() default "";
        int order() default 0;
    }

    /**
     * 方法信息注解
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MethodInfo {
        String author() default "";
        String date() default "";
        String description() default "";
    }

    /**
     * 构造器信息注解
     */
    @Target(ElementType.CONSTRUCTOR)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ConstructorInfo {
        String description() default "";
    }

    // ==================== 注解使用示例类 ====================

    /**
     * 演示带注解的类
     */
    @MyAnnotation(name = "用户类", value = "UserEntity")
    @AnnotationWithDefault(name = "用户")
    @RequiredField(required = true)
    static class UserEntity {

        @FieldInfo(description = "用户ID", order = 1)
        private Long id;

        @FieldInfo(description = "用户名", order = 2)
        private String username;

        @FieldInfo(description = "用户邮箱", order = 3)
        private String email;

        @MethodInfo(author = "张三", date = "2024-01-15", description = "获取用户ID")
        public Long getId() {
            return id;
        }

        @ConstructorInfo(description = "无参构造")
        public UserEntity() {
        }

        @ConstructorInfo(description = "带参构造")
        public UserEntity(Long id, String username) {
            this.id = id;
            this.username = username;
        }
    }

    /**
     * 函数式接口示例（使用 @FunctionalInterface）
     */
    @FunctionalInterface
    interface Calculator {
        int calculate(int a, int b);

        // 可以有默认方法
        default int square(int x) {
            return x * x;
        }
    }

    /**
     * 已废弃的方法示例（使用 @Deprecated）
     */
    @Deprecated
    static class OldClass {
        @Deprecated
        public void oldMethod() {
            // 旧方法
        }
    }

    /**
     * 抑制警告示例
     */
    static class WarningDemo {
        @SuppressWarnings("unused")
        private String unusedField;

        @SuppressWarnings("deprecation")
        public void useDeprecated() {
            OldClass old = new OldClass();
            old.oldMethod();
        }
    }
}
