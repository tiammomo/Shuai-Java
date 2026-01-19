# ORM 模块 - Spring + MyBatis 持久层框架

基于 Spring Framework 与 MyBatis/MyBatis-Plus 的 ORM 持久层学习模块。

## 学习文档

本模块配套学习文档位于 [learn_docs](learn_docs/README.md) 目录：

### Spring + MyBatis 基础

| 主题 | 文档链接 |
|------|----------|
| [快速开始](#快速开始) | [learn_docs/01-快速开始.md](learn_docs/01-快速开始.md) |
| [Spring 核心概念](#spring-核心概念) | [learn_docs/02-Spring核心概念.md](learn_docs/02-Spring核心概念.md) |
| [MyBatis 基础](#mybatis-基础) | [learn_docs/03-MyBatis基础.md](learn_docs/03-MyBatis基础.md) |
| [MyBatis 进阶](#mybatis-进阶) | [learn_docs/04-MyBatis进阶.md](learn_docs/04-MyBatis进阶.md) |

### Spring Boot 集成

| 主题 | 文档链接 |
|------|----------|
| [Spring Boot 集成](#springboot-集成) | [learn_docs/05-SpringBoot集成.md](learn_docs/05-SpringBoot集成.md) |
| [项目实践](#项目实践) | [learn_docs/06-项目实践.md](learn_docs/06-项目实践.md) |
| [最佳实践](#最佳实践) | [learn_docs/07-最佳实践.md](learn_docs/07-最佳实践.md) |

### MyBatis-Plus 增强

| 主题 | 文档链接 |
|------|----------|
| [MyBatis-Plus](#mybatis-plus) | [learn_docs/08-MyBatisPlus.md](learn_docs/08-MyBatisPlus.md) |

> **提示**: 点击主题名称可跳转到下方对应章节。

---

## 技术栈

| 技术 | 版本 | 说明 |
|-----|------|------|
| Spring Framework | 6.x | 企业级应用框架 |
| Spring Boot | 3.x | 简化 Spring 配置 |
| MyBatis | 3.x | 数据持久化框架 |
| MyBatis-Plus | 3.5.x | MyBatis 增强工具 |
| H2 Database | - | 内存数据库（演示用） |
| HikariCP | - | 数据库连接池 |

---

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+

### 编译运行

```bash
# 编译项目
mvn clean compile

# 运行单元测试
mvn test

# 运行 ORM 演示
mvn exec:java -Dexec.mainClass="com.shuai.orm.OrmDemo"

# 运行 MyBatis-Plus 演示
mvn exec:java -Dexec.mainClass="com.shuai.orm.MyBatisPlusDemo"

# 启动 Spring Boot 应用
mvn spring-boot:run
```

### API 端点

启动后访问 `http://localhost:8080/api`:

| 端点 | 方法 | 说明 |
|-----|------|-----|
| `/api/users` | GET | 用户列表 |
| `/api/users/{id}` | GET | 用户详情 |
| `/api/orders` | GET | 订单列表 |
| `/api/orders/{id}` | GET | 订单详情 |
| `/api/configs` | GET | 配置列表 |

---

## 项目结构

```
orm/
├── src/main/java/com/shuai/orm/
│   ├── aspect/              # AOP 切面 (日志、性能监控)
│   ├── config/              # 配置类 (MyBatis-Plus、Redis)
│   ├── controller/          # REST 控制器
│   ├── dto/                 # 数据传输对象
│   ├── entity/              # 实体类 (@TableName 注解)
│   ├── enums/               # 枚举类 (订单状态等)
│   ├── mapper/              # MyBatis Mapper 接口 (继承 BaseMapper)
│   ├── result/              # 统一返回结果
│   └── service/             # 业务逻辑层 (QueryWrapper)
│
├── src/main/resources/
│   ├── application.yml      # 应用配置
│   ├── mybatis-config.xml   # MyBatis 全局配置
│   ├── db/
│   │   ├── schema.sql       # 数据库表结构
│   │   └── data.sql         # 初始化数据
│   └── mapper/              # SQL 映射文件 (XML)
│
├── src/test/
│   ├── java/                # 单元测试
│   └── resources/
│       └── application-test.yml  # 测试配置
│
└── learn_docs/              # 学习文档
```

---

## Spring 核心概念

### IoC 容器与依赖注入

Spring 通过 IoC (控制反转) 容器管理 Bean 的生命周期和依赖关系：

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;  // 依赖注入
}
```

### AOP 面向切面编程

本模块包含两个 AOP 切面示例：

- **LoggingAspect** - 操作日志记录
- **PerformanceAspect** - 性能监控

---

## MyBatis 基础

### Mapper 接口

```java
public interface UserMapper extends BaseMapper<User> {
    // 通用 CRUD 已由 BaseMapper 提供

    // 自定义查询方法
    User selectByUsername(String username);
    List<User> selectByCondition(User condition);
}
```

### XML 映射文件

```xml
<mapper namespace="com.shuai.orm.mapper.UserMapper">
    <select id="selectByUsername" resultType="User">
        SELECT * FROM t_user WHERE username = #{username}
    </select>
</mapper>
```

---

## MyBatis 进阶

### 关联查询

- 一对一: `<association>` 标签
- 一对多: `<collection>` 标签

### 动态 SQL

```xml
<select id="selectByCondition" resultType="User">
    SELECT * FROM t_user
    <where>
        <if test="username != null">AND username LIKE #{username}</if>
        <if test="status != null">AND status = #{status}</if>
    </where>
</select>
```

---

## SpringBoot 集成

### 自动配置

Spring Boot 自动配置 MyBatis-Plus，只需添加依赖：

```xml
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.5</version>
</dependency>
```

### 配置属性

```yaml
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.shuai.orm.entity
  configuration:
    map-underscore-to-camel-case: true
```

---

## 项目实践

### REST API 示例

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }
}
```

---

## 最佳实践

### 事务管理

使用 `@Transactional` 注解管理事务：

```java
@Override
@Transactional
public boolean save(User user) {
    return userMapper.insert(user) > 0;
}
```

### 性能优化

- 使用分页查询避免大结果集
- 合理使用缓存
- 批量操作优化

---

## MyBatis-Plus

### 通用 Mapper

继承 `BaseMapper<T>` 获得通用 CRUD 方法：

```java
public interface UserMapper extends BaseMapper<User> {
    // selectById, insert, updateById, deleteById 等已自动提供
}
```

### 条件构造器

```java
// LambdaQueryWrapper - 类型安全的查询构造器
LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
wrapper.eq(User::getStatus, 1)
       .like(User::getUsername, "admin")
       .orderByDesc(User::getCreatedAt);
List<User> users = userMapper.selectList(wrapper);

// LambdaUpdateWrapper - 类型安全的更新构造器
LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
updateWrapper.eq(User::getId, id)
             .set(User::getStatus, 0);
userMapper.update(null, updateWrapper);
```

### 分页查询

```java
Page<User> page = new Page<>(1, 10);  // 第1页，每页10条
IPage<User> result = userMapper.selectPage(page, null);
```

### 自动填充

通过 `MetaObjectHandler` 实现创建时间、更新时间自动填充：

```java
@Component
public class CustomMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

---

## 参考资源

- [Spring Framework 官方文档](https://spring.io/projects/spring-framework)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [MyBatis 官方文档](https://mybatis.org/mybatis-3/)
- [MyBatis-Plus 官方文档](https://baomidou.com/)
