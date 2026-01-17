# Java 基础语法模块

> Java 语言核心基础模块，系统性演示数据类型、控制流程、面向对象、泛型、反射、SPI 等核心概念。

## 目录

- [模块概述](#模块概述)
- [核心主题速查](#核心主题速查)
- [数据类型详解](#数据类型详解)
- [运算符详解](#运算符详解)
- [字符串与数组详解](#字符串与数组详解)
- [实用工具类](#实用工具类)
- [流程控制详解](#流程控制详解)
- [异常处理详解](#异常处理详解)
- [面向对象详解](#面向对象详解)
- [值传递机制](#值传递机制)
- [泛型详解](#泛型详解)
- [反射机制详解](#反射机制详解)
- [BigDecimal 精确计算](#bigdecimal-精确计算)
- [SPI 机制详解](#spi-机制详解)
- [语法糖特性](#语法糖特性)
- [集合框架详解](#集合框架详解)
- [IO 流详解](#io-流详解)
- [日期时间 API](#日期时间-api)
- [线程与并发](#线程与并发)
- [JSON 处理](#json-处理)
- [日志框架](#日志框架)
- [Optional 详解](#optional-详解)
- [注解详解](#注解详解)
- [网络编程](#网络编程)
- [Netty 框架](#netty-框架)
- [JDBC 数据库](#jdbc-数据库)
- [JVM 内存模型](#jvm-内存模型)
- [Guava 工具](#guava-工具)
- [设计模式](#设计模式)
- [配置管理](#配置管理)
- [单元测试](#单元测试)
- [运行指南](#运行指南)

---

## 模块概述

本模块系统性地演示 Java 语言的核心基础语法知识。代码文件专注于展示方法和类的使用模式，详细的概念说明和原理解释请查阅本文档。

**设计原则：**
- 代码以具体方法使用样例为主（见各 `Basics*Demo.java` 文件）
- 描述性文字集中在 README.md
- 避免过多 System.out.println 污染代码
- 使用 Java 注释简洁说明 API 用法

---

## 核心主题速查

| 主题 | 文件 | 核心内容 |
|-----|------|---------|
| 数据类型 | [BasicsDataTypesDemo.java](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java) | 基本类型、包装类、自动装箱拆箱、类型转换 |
| 运算符 | [BasicsOperatorsDemo.java](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java) | 算术、比较、逻辑、位运算、赋值运算符 |
| 字符串与数组 | [BasicsStringArrayDemo.java](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java) | String、StringBuilder、数组操作、Arrays 工具类 |
| 流程控制 | [BasicsControlFlowDemo.java](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java) | 条件语句、循环、跳转、switch 表达式 |
| 面向对象 | [BasicsOOPDemo.java](src/main/java/com/shuai/oop/BasicsOOPDemo.java) | 类与对象、封装、继承、多态、抽象类与接口 |
| 值传递 | [BasicsPassByValueDemo.java](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java) | 基本类型值传递、引用类型值传递机制 |
| 泛型 | [BasicsGenericsDemo.java](src/main/java/com/shuai/advanced/BasicsGenericsDemo.java) | 泛型类、泛型方法、通配符、边界限制 |
| 反射 | [BasicsReflectionDemo.java](src/main/java/com/shuai/advanced/BasicsReflectionDemo.java) | Class 对象、动态创建对象、访问字段和私有方法 |
| BigDecimal | [BasicsBigDecimalDemo.java](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java) | 精确计算、舍入模式、比较运算 |
| SPI | [BasicsSPIDemo.java](src/main/java/com/shuai/advanced/BasicsSPIDemo.java) | ServiceLoader、服务发现机制、JDBC 驱动加载 |
| 语法糖 | [BasicsSyntaxSugarDemo.java](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java) | Lambda、Stream、Record、Sealed Class |
| 集合框架 | [BasicsCollectionDemo.java](src/main/java/com/shuai/collections/BasicsCollectionDemo.java) | List、Set、Map、Queue、Collections 工具类 |
| IO 流 | [BasicsIoDemo.java](src/main/java/com/shuai/io/BasicsIoDemo.java) | 字节流、字符流、缓冲流、序列化、NIO |
| 日期时间 | [BasicsDateTimeDemo.java](src/main/java/com/shuai/datetime/BasicsDateTimeDemo.java) | LocalDateTime、ZonedDateTime、DateTimeFormatter |
| 线程与并发 | [BasicsThreadsDemo.java](src/main/java/com/shuai/threads/BasicsThreadsDemo.java) | 线程创建、同步、线程池、并发工具 |
| JSON 处理 | [BasicsJsonDemo.java](src/main/java/com/shuai/json/BasicsJsonDemo.java) | Jackson、Gson、序列化/反序列化 |
| 日志框架 | [BasicsLoggingDemo.java](src/main/java/com/shuai/logging/BasicsLoggingDemo.java) | SLF4J、Logback、日志级别、配置 |
| Optional | [BasicsOptionalDemo.java](src/main/java/com/shuai/optional/BasicsOptionalDemo.java) | 避免空指针、链式操作 |
| 注解 | [BasicsAnnotationsDemo.java](src/main/java/com/shuai/annotations/BasicsAnnotationsDemo.java) | 自定义注解、元注解、注解处理器 |
| 网络编程 | [BasicsTcpSocketDemo.java](src/main/java/com/shuai/network/BasicsTcpSocketDemo.java) | TCP Socket、UDP Socket |
| Netty | [BasicsNettyDemo.java](src/main/java/com/shuai/netty/BasicsNettyDemo.java) | 异步网络编程、事件处理 |
| JDBC | [BasicsJdbcDemo.java](src/main/java/com/shuai/database/BasicsJdbcDemo.java) | 数据库连接、CRUD 操作、事务管理 |
| JVM 内存 | [BasicsMemoryDemo.java](src/main/java/com/shuai/jvm/BasicsMemoryDemo.java) | 内存区域、类加载、垃圾回收 |
| Guava | [BasicsGuavaCacheDemo.java](src/main/java/com/shuai/guava/BasicsGuavaCacheDemo.java) | 集合工具、缓存、并发 |
| 设计模式 | [BasicsDesignPatternsDemo.java](src/main/java/com/shuai/patterns/BasicsDesignPatternsDemo.java) | 创建型、结构型、行为型模式 |
| 配置管理 | [BasicsPropertiesDemo.java](src/main/java/com/shuai/config/BasicsPropertiesDemo.java) | Properties 文件、环境变量 |
| 单元测试 | [BasicsTestingDemo.java](src/main/java/com/shuai/testing/BasicsTestingDemo.java) | JUnit 5、断言、测试生命周期 |

---

## 数据类型详解

### BasicsDataTypesDemo.java

Java 数据类型分为两大类：基本数据类型和引用数据类型。

### 基本数据类型（8种）

Java 基本数据类型直接存储值，不涉及对象引用。

**整数型**

| 类型 | 字节 | 范围 | 说明 |
|-----|------|-----|------|
| `byte` | 1 | -128 ~ 127 | 最小的整数类型 |
| `short` | 2 | -32,768 ~ 32,767 | 短整型 |
| `int` | 4 | -21亿 ~ 21亿 | 默认的整数类型 |
| `long` | 8 | 很大 | 需要 `L` 后缀 |

> 代码示例：见 [BasicsDataTypesDemo.java:14-15](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L14-L15)

**浮点型**

| 类型 | 字节 | 精度 | 说明 |
|-----|------|-----|------|
| `float` | 4 | 单精度 | 需要 `f` 后缀 |
| `double` | 8 | 双精度 | 默认的浮点类型 |

> 代码示例：见 [BasicsDataTypesDemo.java:18-19](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L18-L19)

**字符型**

- `char`：2 字节，存储单个 Unicode 字符

> 代码示例：见 [BasicsDataTypesDemo.java:22](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L22)

**布尔型**

- `boolean`：只有 `true` 和 `false` 两个值

> 代码示例：见 [BasicsDataTypesDemo.java:25](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L25)

### 引用数据类型

引用数据类型存储对象的内存地址（引用）。

**类类型**
- `String`、`Integer`、自定义类

> 代码示例：见 [BasicsDataTypesDemo.java:30](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L30)

**接口类型**
- `List`、`Runnable`、自定义接口

> 代码示例：见 [BasicsDataTypesDemo.java:33](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L33)

**数组类型**
- `int[]`、`String[]`

> 代码示例：见 [BasicsDataTypesDemo.java:36-37](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L36-L37)

**枚举类型**
- `enum Season { SPRING, SUMMER, AUTUMN, WINTER }`

### 类型转换

**自动转换（隐式）**

小类型自动转换为大类型，不会丢失精度：
```
byte → short → int → long → float → double
```

> 代码示例：见 [BasicsDataTypesDemo.java:45-46](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L45-L46)

**强制转换（显式）**

大类型转换为小类型，可能丢失精度：

> 代码示例：见 [BasicsDataTypesDemo.java:49-50](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L49-L50)

**溢出示例**

> 代码示例：见 [BasicsDataTypesDemo.java:53-54](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L53-L54)

**字符串转换**

> 代码示例：见 [BasicsDataTypesDemo.java:57-58](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L57-L58)

### 变量作用域

| 类型 | 修饰符 | 生命周期 | 说明 |
|-----|-------|---------|------|
| 成员变量 | 无 | 对象创建到销毁 | 有默认值 |
| 静态变量 | `static` | 类加载到卸载 | 所有对象共享 |
| 局部变量 | 无 | 方法/代码块执行期间 | 必须初始化 |

> 代码示例：见 [BasicsDataTypesDemo.java:61-70](src/main/java/com/shuai/datatype/BasicsDataTypesDemo.java#L61-L70)

---

## 运算符详解

### BasicsOperatorsDemo.java

### 算术运算符

| 运算符 | 说明 | 示例 | 结果 |
|-------|------|-----|------|
| `+` | 加法/字符串拼接 | `10 + 3` | `13` |
| `-` | 减法 | `10 - 3` | `7` |
| `*` | 乘法 | `10 * 3` | `30` |
| `/` | 除法（整数除法截断小数） | `10 / 3` | `3` |
| `%` | 取余 | `10 % 3` | `1` |

> 代码示例：见 [BasicsOperatorsDemo.java:14-40](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L14-L40)

### 关系运算符

| 运算符 | 说明 | 示例 | 结果 |
|-------|------|-----|------|
| `==` | 等于 | `5 == 5` | `true` |
| `!=` | 不等于 | `5 != 3` | `true` |
| `>` | 大于 | `5 > 3` | `true` |
| `<` | 小于 | `5 < 3` | `false` |
| `>=` | 大于等于 | `5 >= 5` | `true` |
| `<=` | 小于等于 | `5 <= 3` | `false` |

> 代码示例：见 [BasicsOperatorsDemo.java:42-47](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L42-L47)

### 逻辑运算符

| 运算符 | 说明 | 短路特性 |
|-------|------|---------|
| `&&` | 逻辑与 | 左边 `false` 时右边不执行 |
| `||` | 逻辑或 | 左边 `true` 时右边不执行 |
| `!` | 逻辑非 | - |
| `&` | 与（非短路） | 两边都执行 |
| `|` | 或（非短路） | 两边都执行 |

> 代码示例：见 [BasicsOperatorsDemo.java:49-58](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L49-L58)

### 位运算符

| 运算符 | 说明 | 示例 | 结果（32位） |
|-------|------|-----|-------------|
| `&` | 按位与 | `5 & 3` | `1` |
| `|` | 按位或 | `5 \| 3` | `7` |
| `^` | 按位异或 | `5 ^ 3` | `6` |
| `~` | 按位取反 | `~5` | `-6` |
| `<<` | 左移 | `5 << 1` | `10` |
| `>>` | 右移 | `8 >> 1` | `4` |

> 代码示例：见 [BasicsOperatorsDemo.java:60-70](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L60-L70)

### 赋值运算符

| 运算符 | 等价于 | 示例 |
|-------|-------|-----|
| `+=` | `x = x + y` | `x += 3` |
| `-=` | `x = x - y` | `x -= 3` |
| `*=` | `x = x * y` | `x *= 3` |
| `/=` | `x = x / y` | `x /= 3` |
| `%=` | `x = x % y` | `x %= 3` |

> 代码示例：见 [BasicsOperatorsDemo.java:72-81](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L72-L81)

### 运算符优先级

从高到低：

1. `()` `[]` `.`（括号、数组、成员访问）
2. `++` `--` `!` `~`（一元运算符）
3. `*` `/` `%`（乘除取余）
4. `+` `-`（加减）
5. `<<` `>>` `>>>`（移位）
6. `<` `<=` `>` `>=` `instanceof`（比较）
7. `==` `!=`（等于判断）
8. `&`（按位与）
9. `^`（按位异或）
10. `|`（按位或）
11. `&&`（逻辑与）
12. `||`（逻辑或）
13. `?:`（三元运算符）
14. `=` `+=` `-=` 等（赋值）

> 代码示例：见 [BasicsOperatorsDemo.java:83-99](src/main/java/com/shuai/datatype/BasicsOperatorsDemo.java#L83-L99)

---

## 字符串与数组详解

### BasicsStringArrayDemo.java

### String 特性

**不可变性**

String 对象一旦创建不可修改。所有看似"修改"的操作都会创建新的 String 对象。

**字符串常量池**

JVM 维护一个字符串常量池，相同内容的字符串字面量共享同一对象。

**字符串比较**

- `==` 比较引用地址
- `equals()` 比较内容

> 代码示例：见 [BasicsStringArrayDemo.java:18-23](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L18-L23)

### StringBuilder 与 StringBuffer

用于高效拼接字符串，避免创建过多中间 String 对象。

| 类 | 线程安全 | 性能 | 适用场景 |
|---|---------|------|---------|
| `StringBuilder` | 非线程安全 | 高 | 单线程环境 |
| `StringBuffer` | 线程安全 | 较低 | 多线程环境 |

**常用方法**：`append()`、`insert()`、`delete()`、`reverse()`、`replace()`

> 代码示例：见 [BasicsStringArrayDemo.java:30-34](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L30-L34)

### 数组

**数组特性**

- 长度固定：创建后不可改变
- 元素类型相同
- 索引从 0 开始，访问越界会抛出 `ArrayIndexOutOfBoundsException`
- 多维数组：`int[][] matrix = {{1,2,3}, {4,5,6}}`

> 代码示例：见 [BasicsStringArrayDemo.java:41-49](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L41-L49)

### Arrays 工具类

| 方法 | 说明 |
|-----|------|
| `Arrays.sort(arr)` | 排序 |
| `Arrays.binarySearch(arr, key)` | 二分查找 |
| `Arrays.equals(arr1, arr2)` | 比较数组内容 |
| `Arrays.fill(arr, value)` | 填充元素 |
| `Arrays.toString(arr)` | 转字符串 |
| `Arrays.copyOf(arr, newLength)` | 复制数组 |
| `Arrays.asList(arr)` | 转为 List |

> 代码示例：见 [BasicsStringArrayDemo.java:56-64](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L56-L64)

---

## 实用工具类

### Objects 工具类（Java 7+）

| 方法 | 说明 |
|-----|------|
| `Objects.isNull(obj)` | 判断是否为 null |
| `Objects.nonNull(obj)` | 判断是否非 null |
| `Objects.equals(a, b)` | 安全比较（避免 NPE） |
| `Objects.hashCode(obj)` | 计算哈希值 |
| `Objects.toString(obj, defaultStr)` | 安全转字符串 |

> 代码示例：见 [BasicsStringArrayDemo.java:85-93](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L85-L93)

### Collections 工具类

**只读/同步集合**

| 方法 | 说明 |
|-----|------|
| `unmodifiableList(list)` | 返回只读 List |
| `synchronizedList(list)` | 返回同步 List |
| `emptyList()` | 返回空只读 List |

**常用操作**

| 方法 | 说明 |
|-----|------|
| `sort(list)` | 排序 |
| `reverse(list)` | 反转 |
| `shuffle(list)` | 随机打乱 |
| `max/min(collection)` | 最大/最小值 |
| `frequency(collection, obj)` | 出现次数 |
| `binarySearch(list, key)` | 二分查找 |

> 代码示例：见 [BasicsStringArrayDemo.java:95-120](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L95-L120)

### Math 工具类

| 方法 | 说明 |
|-----|------|
| `abs(x)` | 绝对值 |
| `sqrt(x)` | 平方根 |
| `pow(a, b)` | 幂运算 |
| `max/min(a, b)` | 最大/最小值 |
| `random()` | 随机数 [0, 1) |
| `round(x)` | 四舍五入 |

> 代码示例：见 [BasicsStringArrayDemo.java:122-135](src/main/java/com/shuai/collections/BasicsStringArrayDemo.java#L122-L135)

---

## 流程控制详解

### BasicsControlFlowDemo.java

### 条件语句 if-else

```java
if (condition) { }
else if (condition) { }
else { }
```

> 代码示例：见 [BasicsControlFlowDemo.java:16-40](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java#L16-L40)

### switch 语句和表达式

**传统 switch 语句（Java 14 前）**

```java
switch(var) {
    case value: break;
    default: break;
}
```

**switch 表达式（Java 14+）**

```java
var result = switch(var) {
    case value -> result;
    default -> defaultResult;
};
```

**多 case 合并**：`case 9, 10 -> "A"`

> 代码示例：见 [BasicsControlFlowDemo.java:42-67](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java#L42-L67)

### 循环语句

| 循环类型 | 说明 |
|---------|------|
| for 循环 | 适合已知循环次数的场景 |
| 增强 for 循环 | 适合遍历数组和集合 |
| while 循环 | 先判断后执行 |
| do-while 循环 | 先执行后判断 |
| forEach 方法 | `list.forEach(item -> { })` |

> 代码示例：见 [BasicsControlFlowDemo.java:69-102](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java#L69-L102)

### 跳转语句

| 语句 | 作用 |
|-----|------|
| `break` | 跳出当前循环或 switch |
| `continue` | 跳过本次循环，继续下一次 |
| `return` | 返回方法结果或退出方法 |
| 带标签的 break | 可跳出多层嵌套循环 |

> 代码示例：见 [BasicsControlFlowDemo.java:104-136](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java#L104-L136)

### 函数式接口

| 接口 | 说明 | 方法 |
|-----|------|-----|
| `Predicate<T>` | 条件判断 | `boolean test(T t)` |
| `Function<T,R>` | 转换 | `R apply(T t)` |
| `Consumer<T>` | 消费 | `void accept(T t)` |
| `Supplier<T>` | 供给 | `T get()` |
| `Comparator<T>` | 比较 | `int compare(T o1, T o2)` |

> 代码示例：见 [BasicsControlFlowDemo.java:138-159](src/main/java/com/shuai/datatype/BasicsControlFlowDemo.java#L138-L159)

---

## 面向对象详解

### BasicsOOPDemo.java

### 类与对象

- **类（Class）**：对象的模板/蓝图，定义属性和方法
- **对象（Object）**：类的实例，通过 `new` 关键字创建

> 代码示例：见 [BasicsOOPDemo.java:13-21](src/main/java/com/shuai/oop/BasicsOOPDemo.java#L13-L21)

### 封装

- 将属性私有化（private）
- 提供公共的 getter/setter 方法
- 隐藏内部实现细节，保护数据安全

> 代码示例：见 [BasicsOOPDemo.java:21-28](src/main/java/com/shuai/oop/BasicsOOPDemo.java#L21-L28)

### 继承

- 子类继承父类的属性和方法（private 除外）
- 使用 `extends` 关键字
- Java 只支持单继承
- `super()` 调用父类构造方法，`super.` 访问父类成员

> 代码示例：见 [BasicsOOPDemo.java:30-44](src/main/java/com/shuai/oop/BasicsOOPDemo.java#L30-L44)

### 多态

**编译时多态（方法重载）**
- 同一类中方法名相同，参数不同

**运行时多态（方法重写）**
- 子类重写父类方法
- 向上转型：`Animal dog = new Dog()`（自动转换）
- 向下转型：`Dog dog2 = (Dog) animal`（需要强制转换）
- 使用 `instanceof` 判断对象类型

> 代码示例：见 [BasicsOOPDemo.java:47-58](src/main/java/com/shuai/oop/BasicsOOPDemo.java#L47-L58)

### 抽象类 vs 接口

| 特性 | 抽象类 | 接口（Java 8+） |
|-----|-------|----------------|
| 方法 | 抽象方法和具体方法 | 抽象方法、默认方法、静态方法 |
| 构造方法 | 有 | 无 |
| 继承 | 单继承 | 多实现 |
| 字段 | 各种类型 | 默认为 public static final |

> 代码示例：见 [BasicsOOPDemo.java:61-71](src/main/java/com/shuai/oop/BasicsOOPDemo.java#L61-L71)

---

## 异常处理详解

### 异常层次结构

```
Throwable
├── Error（系统错误，程序无法处理）
│   ├── OutOfMemoryError
│   ├── StackOverflowError
└── Exception（可处理的异常）
    ├── RuntimeException（运行时异常）
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   └── ArrayIndexOutOfBoundsException
    └── IOException（受检异常）
        ├── FileNotFoundException
        └── SocketException
```

### try-catch-finally

```java
try {
    // 可能抛出异常的代码
} catch (ArithmeticException e) {
    // 处理特定异常
} catch (Exception e) {
    // 处理其他异常
} finally {
    // 无论是否异常都执行
}
```

> 代码示例：见 [BasicsExceptionsDemo.java:20-35](src/main/java/com/shuai/exceptions/BasicsExceptionsDemo.java#L20-L35)

### try-with-resources

自动关闭实现 `AutoCloseable` 的资源。

```java
try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
    String line = reader.readLine();
} catch (IOException e) {
    // 处理异常
}
```

> 代码示例：见 [BasicsExceptionsDemo.java:50-65](src/main/java/com/shuai/exceptions/BasicsExceptionsDemo.java#L50-L65)

### throws 与 throw

| 关键字 | 位置 | 作用 |
|-------|------|------|
| `throws` | 方法声明 | 声明可能抛出的异常 |
| `throw` | 方法体 | 手动抛出异常 |

```java
void readFile(String path) throws IOException {
    if (path == null) {
        throw new IllegalArgumentException("路径不能为空");
    }
    // 可能抛出 IOException
}
```

> 代码示例：见 [BasicsExceptionsDemo.java:80-95](src/main/java/com/shuai/exceptions/BasicsExceptionsDemo.java#L80-L95)

### 自定义异常

```java
class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

---

## 值传递机制

### BasicsPassByValueDemo.java

Java 始终按值传递（pass by value），没有按引用传递。

### 基本类型值传递

传递基本类型时，方法接收的是值的副本。在方法内修改参数值，只影响副本，不影响原变量。

> 代码示例：见 [BasicsPassByValueDemo.java:15-24](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L15-L24)

### 引用类型值传递

传递引用类型时，方法接收的是引用地址的副本：
- 通过引用副本可以访问和修改原对象的属性
- 但将引用副本重新指向新对象，不会影响原引用

> 代码示例：见 [BasicsPassByValueDemo.java:30-53](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L30-L53)

### 集合框架操作

- `list.add()`、`list.remove()`：修改集合内容
- `Collections.unmodifiableList()`：创建不可修改集合
- Stream API：处理数据，保持原集合不可变

> 代码示例：见 [BasicsPassByValueDemo.java:55-77](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L55-L77)

### 函数式接口在值传递中的应用

使用 `Supplier`、`Consumer`、`Function` 等函数式接口实现回调。

> 代码示例：见 [BasicsPassByValueDemo.java:26-28](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L26-L28)
> 代码示例：见 [BasicsPassByValueDemo.java:51-53](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L51-L53)

### 不可变性

- String 不可变性：所有"修改"操作都创建新对象
- Record（Java 14+）：自动生成不可变数据类
- Collections.unmodifiableList()：创建只读集合

> 代码示例：见 [BasicsPassByValueDemo.java:78-97](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L78-L97)
> 代码示例：见 [BasicsPassByValueDemo.java:113](src/main/java/com/shuai/oop/BasicsPassByValueDemo.java#L113)

---

## 泛型详解

### BasicsGenericsDemo.java

泛型在编译阶段提供类型检查，避免强制类型转换。

### 泛型类

在类定义时使用类型参数，在创建对象时指定具体类型。

```java
class Box<T> { private T content; }
class Pair<K, V> { private K key; private V value; }
```

> 代码示例：见 [BasicsGenericsDemo.java:12-26](src/main/java/com/shuai/advanced/BasicsGenericsDemo.java#L12-L26)

### 泛型方法

在方法定义时使用类型参数，可独立于类的泛型参数。

```java
public <T> void printArray(T[] array) { }
```

> 代码示例：见 [BasicsGenericsDemo.java:28-38](src/main/java/com/shuai/advanced/BasicsGenericsDemo.java#L28-L38)

### 通配符

| 通配符 | 说明 | 限制 |
|-------|------|-----|
| `?` | 无界通配符 | 接受任何类型 |
| `? extends T` | 上界通配符 | T 或其子类，只读 |
| `? super T` | 下界通配符 | T 或其父类，可添加 |

> 代码示例：见 [BasicsGenericsDemo.java:40-52](src/main/java/com/shuai/advanced/BasicsGenericsDemo.java#L40-L52)

### 泛型边界

- 上界限制：`<T extends Number>` T 只能是 Number 或其子类
- 可同时限制接口：`<T extends Comparable<T>>`

> 代码示例：见 [BasicsGenericsDemo.java:54-66](src/main/java/com/shuai/advanced/BasicsGenericsDemo.java#L54-L66)

---

## 反射机制详解

### BasicsReflectionDemo.java

反射允许在运行时动态获取类的信息和操作对象。

### 获取 Class 对象

| 方式 | 代码 |
|-----|------|
| 方式1 | `ClassName.class` |
| 方式2 | `instance.getClass()` |
| 方式3 | `Class.forName("fully.qualified.ClassName")` |
| 方式4 | `classLoader.loadClass("ClassName")` |

> 代码示例：见 [BasicsReflectionDemo.java:18-31](src/main/java/com/shuai/advanced/BasicsReflectionDemo.java#L18-L31)

### 检查类信息

| 方法 | 说明 |
|-----|------|
| `getSimpleName()` | 获取简单类名 |
| `getPackage().getName()` | 获取包名 |
| `getSuperclass()` | 获取父类 |
| `getModifiers()` | 获取修饰符 |
| `getInterfaces()` | 获取实现的接口 |
| `getConstructors()` | 获取构造器 |
| `getDeclaredFields()` | 获取字段 |
| `getDeclaredMethods()` | 获取方法 |

> 代码示例：见 [BasicsReflectionDemo.java:33-51](src/main/java/com/shuai/advanced/BasicsReflectionDemo.java#L33-L51)

### 动态操作

| 操作 | 代码 |
|-----|------|
| 创建对象 | `class.getDeclaredConstructor().newInstance()` |
| 访问字段 | `field.get(instance)`、`field.set(instance, value)` |
| 调用方法 | `method.invoke(instance, args)` |
| 访问私有成员 | 先调用 `setAccessible(true)` |

> 代码示例：见 [BasicsReflectionDemo.java:53-139](src/main/java/com/shuai/advanced/BasicsReflectionDemo.java#L53-L139)

---

## BigDecimal 精确计算

### BasicsBigDecimalDemo.java

### 浮点数精度问题

double 的二进制表示无法精确表示某些十进制小数：
```
0.1 + 0.2 = 0.30000000000000004 (double)
0.1 + 0.2 = 0.3 (BigDecimal)
```

> 代码示例：见 [BasicsBigDecimalDemo.java:18-28](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L18-L28)

### BigDecimal 构造

| 方式 | 推荐程度 | 说明 |
|-----|---------|------|
| `new BigDecimal("0.1")` | 推荐 | 使用 String 构造 |
| `BigDecimal.valueOf(0.1)` | 推荐 | 内部使用 String |
| `new BigDecimal(0.1)` | 避免 | double 构造有精度问题 |

> 代码示例：见 [BasicsBigDecimalDemo.java:30-45](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L30-L45)

### 算术运算

| 方法 | 说明 |
|-----|------|
| `add()` | 加法 |
| `subtract()` | 减法 |
| `multiply()` | 乘法 |
| `divide()` | 除法，需要指定舍入模式 |
| `pow(n)` | 幂运算 |
| `negate()` | 取反 |
| `setScale(n, mode)` | 设置精度和舍入 |

> 代码示例：见 [BasicsBigDecimalDemo.java:47-63](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L47-L63)

### 舍入模式

| 模式 | 说明 | 示例 2.5 |
|-----|------|---------|
| `HALF_UP` | 四舍五入 | 3 |
| `HALF_DOWN` | 五舍六入 | 2 |
| `HALF_EVEN` | 银行家舍入 | 2 |
| `UP` | 向远离零方向 | 3 |
| `DOWN` | 向零方向 | 2 |
| `CEILING` | 向上取整 | 3 |
| `FLOOR` | 向下取整 | 2 |

> 代码示例：见 [BasicsBigDecimalDemo.java:65-81](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L65-L81)

### 比较运算

- `equals()`：比较值和精度（`1.0` 不等于 `1.00`）
- `compareTo()`：比较数值（`1.0` 等于 `1.00`）

> 代码示例：见 [BasicsBigDecimalDemo.java:83-106](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L83-L106)

### 实际使用场景

价格计算、税费计算、多个金额求和。

> 代码示例：见 [BasicsBigDecimalDemo.java:108-130](src/main/java/com/shuai/advanced/BasicsBigDecimalDemo.java#L108-L130)

---

## SPI 机制详解

### BasicsSPIDemo.java

SPI（Service Provider Interface）是一种服务发现机制，允许模块化设计中的服务提供者实现接口。

### SPI 工作原理

```
接口定义 → 第三方实现 → META-INF/services 配置 → ServiceLoader 加载 → 获取服务实例
```

### SPI vs API

| 概念 | 说明 | 示例 |
|-----|------|-----|
| API | 应用程序调用接口 | `List.add()` |
| SPI | 框架提供接口，第三方实现 | `java.sql.Driver` |

### ServiceLoader 使用

```java
ServiceLoader<LogService> loader = ServiceLoader.load(LogService.class);
for (LogService service : loader) {
    service.log("message");
}
```

**遍历方式**

| 方式 | 说明 |
|-----|------|
| for-each | `for (LogService service : loader) { }` |
| Stream | `loader.stream().forEach(...)` |

> 代码示例：见 [BasicsSPIDemo.java:34-51](src/main/java/com/shuai/advanced/BasicsSPIDemo.java#L34-L51)

### META-INF/services 配置

在 `src/main/resources/META-INF/services/` 目录下创建文件：
- 文件名：接口全限定名（如 `com.shuai.spi.LogService`）
- 内容：实现类的全限定名（每行一个）

> 代码示例：见 [BasicsSPIDemo.java:53-68](src/main/java/com/shuai/advanced/BasicsSPIDemo.java#L53-L68)

### SPI 接口定义

> SPI 接口定义：见 [LogService.java](src/main/java/com/shuai/spi/LogService.java)
> SPI 实现类：见 [ConsoleLogService.java](src/main/java/com/shuai/spi/ConsoleLogService.java)、[FileLogService.java](src/main/java/com/shuai/spi/FileLogService.java)
> 配置文件：见 [com.shuai.spi.LogService](../src/main/resources/META-INF/services/com.shuai.spi.LogService)

### 典型应用

- **JDBC 驱动加载**：`java.sql.Driver`
- **日志框架**：SLF4J、Log4j
- **插件式架构**
- **Spring Boot 自动配置**

---

## 语法糖特性

### BasicsSyntaxSugarDemo.java

语法糖是语言提供的便捷语法，让代码更简洁易写。

### 类型擦除

泛型信息只在编译阶段存在，运行时被擦除：
```
List<String> 和 List<Integer> 在运行时的 class 相同
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:19-26](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L19-L26)

### 自动装箱/拆箱

- **装箱**：`Integer i = 100`（自动调用 `Integer.valueOf(100)`）
- **拆箱**：`int n = i`（自动调用 `i.intValue()`）
- Integer 缓存：[-128, 127] 范围内的值会被缓存复用

> 代码示例：见 [BasicsSyntaxSugarDemo.java:28-48](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L28-L48)

### 增强 for 循环

```java
for (int num : numbers) { }
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:50-70](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L50-L70)

### Lambda 表达式

```java
names.forEach(name -> System.out.println(name));
names.forEach(System.out::println);
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:72-93](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L72-L93)

### 方法引用

| 类型 | 语法 | 示例 |
|-----|------|-----|
| 类静态方法 | `ClassName::staticMethod` | `Math::abs` |
| 对象实例方法 | `instance::instanceMethod` | `str::length` |
| 类实例方法 | `ClassName::instanceMethod` | `String::toUpperCase` |
| 构造方法 | `ClassName::new` | `ArrayList::new` |

### Stream API（Java 8+）

Stream 提供声明式的数据处理方式。

**中间操作**

| 方法 | 说明 |
|-----|------|
| `filter(Predicate)` | 过滤元素 |
| `map(Function)` | 转换元素 |
| `flatMap(Function)` | 展开并转换 |
| `distinct()` | 去重 |
| `sorted()` | 排序 |
| `limit(n)` | 截取前 n 个 |
| `skip(n)` | 跳过前 n 个 |

**终端操作**

| 方法 | 说明 |
|-----|------|
| `forEach(Consumer)` | 遍历 |
| `toList()` | 收集到 List |
| `collect(Collector)` | 收集到指定集合 |
| `count()` | 计数 |
| `anyMatch/allMatch/noneMatch` | 匹配判断 |
| `findFirst/findAny` | 查找元素 |
| `reduce(BinaryOperator)` | 归约 |
| `mapToInt/Long/Double` | 转换为基本类型 Stream |
| `summaryStatistics()` | 统计信息 |

> 代码示例：见 [BasicsSyntaxSugarDemo.java:96-193](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L96-L193)

### Switch 表达式（Java 14+）

```java
String grade = switch (score / 10) {
    case 9, 10 -> "A";
    case 8 -> "B";
    default -> "D";
};
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:95-120](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L95-L120)

### Record（Java 14+）

自动生成构造方法、getter、equals、hashCode、toString。

```java
record Point(int x, int y) { }
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:122-136](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L122-L136)
> 代码示例：见 [BasicsSyntaxSugarDemo.java:185](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L185)

### Sealed Class（Java 17+）

限制接口或类的实现类，只能由指定的类实现。

```java
sealed interface GeoShape permits Circle, Rectangle {}
record Circle(double radius) implements GeoShape {}
record Rectangle(double width, double height) implements GeoShape {}
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:187-189](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L187-L189)
> 代码示例：见 [BasicsSyntaxSugarDemo.java:138-143](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L138-L143)

### var 关键字（Java 10+）

局部变量类型推断。

```java
var message = "Hello World";
var numbers = List.of(1, 2, 3);
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:145-155](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L145-L155)

### 文本块（Java 13+）

多行字符串字面量。

```java
String json = """
    {
        "name": "张三"
    }
    """;
```

> 代码示例：见 [BasicsSyntaxSugarDemo.java:157-174](src/main/java/com/shuai/modern/BasicsSyntaxSugarDemo.java#L157-L174)

---

## 集合框架详解

### BasicsCollectionDemo.java

Java 集合框架提供了用于存储和操作对象组的数据结构。

### Collection 接口层次

```
Iterable<T>                    - 可迭代接口
    ├── Collection<E>          - 集合接口
    │   ├── List<E>            - 有序列表
    │   ├── Set<E>             - 无重复集合
    │   └── Queue<E>           - 队列
    └── Map<K,V>               - 键值对
```

### List 接口

| 实现类 | 特点 | 时间复杂度 |
|-------|------|-----------|
| ArrayList | 随机访问 O(1)，尾部插入 O(1) | 查找快，插入删除慢 |
| LinkedList | 头部/尾部插入 O(1)，随机访问 O(n) | 插入删除快，查找慢 |
| Vector | 线程安全，性能较差 | - |

> 代码示例：见 [BasicsCollectionDemo.java:57-98](src/main/java/com/shuai/collections/BasicsCollectionDemo.java#L57-L98)

### Set 接口

| 实现类 | 特点 |
|-------|------|
| HashSet | 基于哈希表，无序，O(1) 查找 |
| LinkedHashSet | 保持插入顺序 |
| TreeSet | 基于红黑树，有序，O(log n) |

> 代码示例：见 [BasicsCollectionDemo.java:102-135](src/main/java/com/shuai/collections/BasicsCollectionDemo.java#L102-L135)

### Map 接口

| 实现类 | 特点 |
|-------|------|
| HashMap | 键值对，O(1) 查找 |
| LinkedHashMap | 保持插入顺序 |
| TreeMap | 基于红黑树，有序 |

> 代码示例：见 [BasicsCollectionDemo.java:137-170](src/main/java/com/shuai/collections/BasicsCollectionDemo.java#L137-L170)

### Queue 接口

| 实现类 | 特点 |
|-------|------|
| LinkedList | 可作为队列使用 |
| PriorityQueue | 优先级队列 |
| ArrayDeque | 循环数组实现 |

### Collections 工具类

```java
// 只读集合
Collections.unmodifiableList(list);

// 同步集合
Collections.synchronizedList(list);

// 排序
Collections.sort(list);

// 二分查找
Collections.binarySearch(list, key);
```

> 代码示例：见 [BasicsCollectionDemo.java:172-210](src/main/java/com/shuai/collections/BasicsCollectionDemo.java#L172-L210)

---

## IO 流详解

### BasicsIoDemo.java

Java IO 用于处理输入输出操作，包括文件、网络等。

### IO 流分类

**按方向分：**
- 输入流（InputStream/Reader）：从来源读取数据
- 输出流（OutputStream/Writer）：向目标写入数据

**按数据类型分：**
- 字节流：处理二进制数据（Image、Audio 等）
- 字符流：处理文本数据

### 字节流

| 类 | 说明 |
|----|------|
| FileInputStream/FileOutputStream | 文件字节流 |
| ByteArrayInputStream/ByteArrayOutputStream | 内存字节流 |
| BufferedInputStream/BufferedOutputStream | 缓冲字节流 |

> 代码示例：见 [BasicsIoDemo.java:57-85](src/main/java/com/shuai/io/BasicsIoDemo.java#L57-L85)

### 字符流

| 类 | 说明 |
|----|------|
| FileReader/FileWriter | 文件字符流 |
| BufferedReader/BufferedWriter | 缓冲字符流 |
| InputStreamReader/OutputStreamWriter | 字节转字符 |

> 代码示例：见 [BasicsIoDemo.java:87-120](src/main/java/com/shuai/io/BasicsIoDemo.java#L87-L120)

### 对象流（序列化）

```java
// 序列化
try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("data.obj"))) {
    out.writeObject(object);
}

// 反序列化
try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("data.obj"))) {
    Object obj = in.readObject();
}
```

> 代码示例：见 [BasicsIoDemo.java:150-175](src/main/java/com/shuai/io/BasicsIoDemo.java#L150-L175)

### NIO（新 IO）

| 类 | 说明 |
|----|------|
| Path | 文件路径抽象 |
| Files | 文件操作工具类 |
| Channel | 通道 |
| Buffer | 缓冲区 |

```java
Path path = Paths.get("file.txt");
byte[] content = Files.readAllBytes(path);
Files.write(path, content);
```

> 代码示例：见 [BasicsIoDemo.java:177-250](src/main/java/com/shuai/io/BasicsIoDemo.java#L177-L250)

---

## 日期时间 API

### BasicsDateTimeDemo.java

Java 8 引入了全新的日期时间 API，位于 `java.time` 包下。

### LocalDate（日期）

```java
LocalDate today = LocalDate.now();
LocalDate date = LocalDate.of(2024, 1, 15);

// 获取分量
int year = today.getYear();
int month = today.getMonthValue();
int day = today.getDayOfMonth();

// 日期计算
LocalDate tomorrow = today.plusDays(1);
LocalDate nextMonth = today.plusMonths(1);
```

> 代码示例：见 [BasicsDateTimeDemo.java:47-85](src/main/java/com/shuai/datetime/BasicsDateTimeDemo.java#L47-L85)

### LocalTime（时间）

```java
LocalTime now = LocalTime.now();
LocalTime time = LocalTime.of(14, 30, 0);

// 时间计算
LocalTime later = now.plusHours(1);
```

### LocalDateTime（日期时间）

```java
LocalDateTime now = LocalDateTime.now();
LocalDateTime dt = LocalDateTime.of(2024, 1, 15, 14, 30);
```

> 代码示例：见 [BasicsDateTimeDemo.java:90-120](src/main/java/com/shuai/datetime/BasicsDateTimeDemo.java#L90-L120)

### ZonedDateTime（带时区）

```java
ZonedDateTime now = ZonedDateTime.now();
ZonedDateTime tokyo = now.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
```

> 代码示例：见 [BasicsDateTimeDemo.java:125-160](src/main/java/com/shuai/datetime/BasicsDateTimeDemo.java#L125-L160)

### Duration 与 Period

```java
// 基于时间的 Duration
Duration duration = Duration.between(LocalTime.now(), LocalTime.MAX);

// 基于日期的 Period
Period period = Period.between(birthday, today);
```

### DateTimeFormatter（格式化）

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String str = now.format(formatter);
LocalDateTime parsed = LocalDateTime.parse(str, formatter);
```

> 代码示例：见 [BasicsDateTimeDemo.java:190-210](src/main/java/com/shuai/datetime/BasicsDateTimeDemo.java#L190-L210)

---

## 线程与并发

### BasicsThreadsDemo.java、BasicsThreadDemo.java

### 线程创建

**方式1：继承 Thread**

```java
class MyThread extends Thread {
    @Override
    public void run() {
        // 线程执行逻辑
    }
}
new MyThread().start();
```

**方式2：实现 Runnable**

```java
Runnable task = () -> {
    // 线程执行逻辑
};
new Thread(task).start();
```

**方式3：实现 Callable**

```java
Callable<String> task = () -> "result";
Future<String> future = executor.submit(task);
```

> 代码示例：见 [BasicsThreadsDemo.java:20-80](src/main/java/com/shuai/threads/BasicsThreadsDemo.java#L20-L80)

### 线程生命周期

```
NEW → RUNNABLE → BLOCKED/WAITING → TIMED_WAITING → TERMINATED
```

### 线程同步

**synchronized 关键字**

```java
// 同步方法
public synchronized void syncMethod() { }

// 同步代码块
synchronized (lock) {
    // 临界区
}
```

**ReentrantLock**

```java
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // 临界区
} finally {
    lock.unlock();
}
```

> 代码示例：见 [BasicsThreadDemo.java:50-120](src/main/java/com/shuai/concurrent/BasicsThreadDemo.java#L50-L120)

### 线程池

```java
ExecutorService executor = Executors.newFixedThreadPool(4);
executor.submit(() -> { /* 任务逻辑 */ });
executor.shutdown();
```

| 线程池类型 | 说明 |
|-----------|------|
| newFixedThreadPool | 固定大小线程池 |
| newCachedThreadPool | 缓存线程池 |
| newSingleThreadExecutor | 单线程线程池 |
| newScheduledThreadPool | 定时任务线程池 |

> 代码示例：见 [BasicsThreadDemo.java:122-180](src/main/java/com/shuai/concurrent/BasicsThreadDemo.java#L122-L180)

### 并发工具类

| 类 | 说明 |
|----|------|
| CountDownLatch | 倒计时门闩 |
| CyclicBarrier | 循环屏障 |
| Semaphore | 信号量 |
| CompletableFuture | 异步计算 |

---

## JSON 处理

### BasicsJsonDemo.java、BasicsGsonDemo.java

### JSON 序列化/反序列化

**Jackson**

```java
ObjectMapper mapper = new ObjectMapper();

// 对象转 JSON
String json = mapper.writeValueAsString(user);

// JSON 转对象
User user = mapper.readValue(json, User.class);
```

> 代码示例：见 [BasicsJsonDemo.java:30-80](src/main/java/com/shuai/json/BasicsJsonDemo.java#L30-L80)

**Gson**

```java
Gson gson = new Gson();

// 对象转 JSON
String json = gson.toJson(user);

// JSON 转对象
User user = gson.fromJson(json, User.class);

// 泛型处理
Type type = new TypeToken<List<User>>(){}.getType();
List<User> users = gson.fromJson(json, type);
```

> 代码示例：见 [BasicsGsonDemo.java:30-100](src/main/java/com/shuai/json/BasicsGsonDemo.java#L30-L100)

### 注解

```java
// 忽略字段
@JsonIgnore
private String password;

// 自定义字段名
@JsonProperty("user_name")
private String userName;
```

### 复杂 JSON 处理

```java
// 嵌套对象
JsonNode root = mapper.readTree(json);
String name = root.get("user").get("name").asText();

// 数组处理
JsonNode items = root.get("items");
for (JsonNode item : items) {
    // 处理每个元素
}
```

---

## 日志框架

### BasicsLoggingDemo.java

### 日志框架体系

```
    应用层
       |
   SLF4J (门面/抽象层)
       |
   Logback / Log4j2 (实现层)
```

### SLF4J 日志级别

| 级别 | 说明 | 顺序 |
|-----|------|-----|
| TRACE | 最详细 | 1 |
| DEBUG | 调试信息 | 2 |
| INFO | 正常信息 | 3 |
| WARN | 警告 | 4 |
| ERROR | 错误 | 5 |
| OFF | 关闭 | 6 |

### 基本使用

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyClass {
    private static final Logger logger = LoggerFactory.getLogger(MyClass.class);

    public void process() {
        logger.debug("Debug message: {}", variable);
        logger.info("Info message");
        logger.warn("Warning: {}", value);
        logger.error("Error: {}", exception.getMessage(), exception);
    }
}
```

> 代码示例：见 [BasicsLoggingDemo.java:30-60](src/main/java/com/shuai/logging/BasicsLoggingDemo.java#L30-L60)

### Logback 配置

```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

### 占位符与参数

```java
// 避免字符串拼接，性能更好
logger.debug("User {} logged in at {}", userId, loginTime);
```

### 日志文件输出

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>logs/app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
    </rollingPolicy>
</appender>
```

> 代码示例：见 [BasicsLoggingDemo.java:62-120](src/main/java/com/shuai/logging/BasicsLoggingDemo.java#L62-L120)

---

## Optional 详解

### BasicsOptionalDemo.java

Optional 是 Java 8 引入的类，用于优雅地处理 null 值，避免空指针异常。

### Optional 创建

```java
// 创建空的 Optional
Optional<String> empty = Optional.empty();

// 创建非空的 Optional
Optional<String> present = Optional.of("value");

// 创建可能为空的 Optional
Optional<String> nullable = Optional.ofNullable(maybeNull);
```

### 常用方法

| 方法 | 说明 |
|-----|------|
| `isPresent()` | 判断值是否存在 |
| `ifPresent(Consumer)` | 值存在时执行操作 |
| `get()` | 获取值（可能抛出异常） |
| `orElse(T)` | 值不存在时返回默认值 |
| `orElseGet(Supplier)` | 值不存在时返回供应值 |
| `orElseThrow()` | 值不存在时抛出异常 |

```java
Optional<String> optional = Optional.ofNullable(name);
String result = optional.orElse("default");
```

> 代码示例：见 [BasicsOptionalDemo.java:20-80](src/main/java/com/shuai/optional/BasicsOptionalDemo.java#L20-L80)

### 链式操作

```java
// 过滤
optional.filter(s -> s.length() > 0)

// 映射
optional.map(String::toUpperCase)

// 扁平映射
optional.flatMap(Optional::ofNullable)
```

### 实际使用场景

```java
// 避免嵌套 null 检查
return Optional.ofNullable(user)
    .map(User::getAddress)
    .map(Address::getCity)
    .orElse("Unknown");
```

---

## 注解详解

### BasicsAnnotationsDemo.java

注解是 Java 5 引入的元数据机制，用于为代码元素提供附加信息。

### 内置注解

| 注解 | 说明 |
|-----|------|
| `@Override` | 标记重写父类方法 |
| `@Deprecated` | 标记已废弃 |
| `@SuppressWarnings` | 抑制编译器警告 |
| `@FunctionalInterface` | 标记函数式接口 |

### 元注解

| 元注解 | 说明 |
|-------|------|
| `@Retention` | 注解保留阶段（SOURCE/CLASS/RUNTIME） |
| `@Target` | 注解可应用的目标元素 |
| `@Documented` | 注解包含在 Javadoc 中 |
| `@Inherited` | 注解可被继承 |

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value() default "";
}
```

> 代码示例：见 [BasicsAnnotationsDemo.java:30-100](src/main/java/com/shuai/annotations/BasicsAnnotationsDemo.java#L30-L100)

### 注解处理器

```java
// 使用反射获取注解信息
Method method = MyClass.class.getMethod("myMethod");
MyAnnotation annotation = method.getAnnotation(MyAnnotation.class);
String value = annotation.value();
```

### 常见应用

- Lombok：@Data、@Builder、@Getter
- Spring：@Component、@Autowired、@RequestMapping
- JUnit：@Test、@BeforeEach、@ParameterizedTest

---

## 网络编程

### BasicsNetworkDemo.java、BasicsTcpSocketDemo.java、BasicsUdpSocketDemo.java

### InetAddress

```java
InetAddress address = InetAddress.getByName("www.baidu.com");
System.out.println("IP: " + address.getHostAddress());
System.out.println("Name: " + address.getHostName());
```

### URL 处理

```java
URL url = new URL("https://www.example.com/path?query=value");
String protocol = url.getProtocol();  // https
String host = url.getHost();          // www.example.com
int port = url.getPort();             // 443
String path = url.getPath();          // /path
String query = url.getQuery();        // query=value
```

### HTTP 客户端（Java 11+）

```java
HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .build();

HttpResponse<String> response = client.send(request,
    HttpResponse.BodyHandlers.ofString());
String body = response.body();
```

> 代码示例：见 [BasicsNetworkDemo.java:30-100](src/main/java/com/shuai/network/BasicsNetworkDemo.java#L30-L100)

### TCP Socket

**服务器端**

```java
try (ServerSocket server = new ServerSocket(8080)) {
    Socket client = server.accept();
    BufferedReader in = new BufferedReader(
        new InputStreamReader(client.getInputStream()));
    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
    // 处理客户端请求
}
```

**客户端**

```java
try (Socket socket = new Socket("localhost", 8080)) {
    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));
    out.println("Hello Server");
    String response = in.readLine();
}
```

> 代码示例：见 [BasicsTcpSocketDemo.java:30-120](src/main/java/com/shuai/network/BasicsTcpSocketDemo.java#L30-L120)

### UDP Socket

```java
// 发送端
DatagramSocket socket = new DatagramSocket();
byte[] data = "Hello".getBytes();
DatagramPacket packet = new DatagramPacket(data, data.length,
    InetAddress.getByName("localhost"), 8080);
socket.send(packet);

// 接收端
DatagramSocket socket = new DatagramSocket(8080);
byte[] buffer = new byte[1024];
DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
socket.receive(packet);
String message = new String(packet.getData(), 0, packet.getLength());
```

> 代码示例：见 [BasicsUdpSocketDemo.java:30-80](src/main/java/com/shuai/network/BasicsUdpSocketDemo.java#L30-L80)

---

## Netty 框架

### BasicsNettyDemo.java

Netty 是一个高性能的网络通信框架，简化了 TCP/UDP 服务器开发。

### 核心概念

| 概念 | 说明 |
|-----|------|
| Channel | 网络套接字 |
| EventLoop | 处理 I/O 事件 |
| ChannelPipeline | 处理器链 |
| ByteBuf | 字节容器 |

### 简单 TCP 服务器

```java
EventLoopGroup boss = new NioEventLoopGroup();
EventLoopGroup worker = new NioEventLoopGroup();

ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(boss, worker)
    .channel(NioServerSocketChannel.class)
    .childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        protected void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new HelloServerHandler());
        }
    });

ChannelFuture future = bootstrap.bind(8080).sync();
future.channel().closeFuture().sync();
```

### ChannelHandler

```java
public class HelloServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Received: " + in.toString(StandardCharsets.UTF_8));
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello Client", StandardCharsets.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
```

> 代码示例：见 [BasicsNettyDemo.java:30-150](src/main/java/com/shuai/netty/BasicsNettyDemo.java#L30-L150)

---

## JDBC 数据库

### BasicsJdbcDemo.java

JDBC（Java Database Connectivity）是 Java 连接数据库的标准 API。

### JDBC 步骤

```
1. 加载驱动 → 2. 获取连接 → 3. 创建语句 → 4. 执行 SQL → 5. 处理结果 → 6. 释放资源
```

### 基本操作

```java
// 1. 加载驱动（Java 6+ 自动加载）
Class.forName("com.mysql.cj.jdbc.Driver");

// 2. 获取连接
String url = "jdbc:mysql://localhost:3306/mydb";
String user = "root";
String password = "password";
Connection conn = DriverManager.getConnection(url, user, password);

// 3. 创建语句
Statement stmt = conn.createStatement();

// 4. 执行查询
ResultSet rs = stmt.executeQuery("SELECT * FROM users");

// 5. 处理结果
while (rs.next()) {
    int id = rs.getInt("id");
    String name = rs.getString("name");
}

// 6. 释放资源
rs.close();
stmt.close();
conn.close();
```

> 代码示例：见 [BasicsJdbcDemo.java:30-100](src/main/java/com/shuai/database/BasicsJdbcDemo.java#L30-L100)

### PreparedStatement（防止 SQL 注入）

```java
String sql = "SELECT * FROM users WHERE name = ?";
PreparedStatement pstmt = conn.prepareStatement(sql);
pstmt.setString(1, "张三");
ResultSet rs = pstmt.executeQuery();
```

### 事务管理

```java
conn.setAutoCommit(false);  // 关闭自动提交
try {
    executeUpdate("INSERT INTO orders ...");
    executeUpdate("UPDATE account ...");
    conn.commit();  // 提交事务
} catch (SQLException e) {
    conn.rollback();  // 回滚事务
}
```

### 连接池（推荐 HikariCP）

```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://localhost:3306/mydb");
config.setUsername("root");
config.setPassword("password");
config.setMaximumPoolSize(10);

HikariDataSource ds = new HikariDataSource(config);
Connection conn = ds.getConnection();
```

---

## JVM 内存模型

### BasicsMemoryDemo.java

### 内存区域

```
┌─────────────────────────────────────────────────────────┐
│                      JVM 进程内存                        │
│  ┌───────────────────────────────────────────────────┐  │
│  │                   方法区 (MetaSpace)               │  │
│  │  - 类信息、运行时常量池、静态变量、JIT 编译代码    │  │
│  └───────────────────────────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────┐  │
│  │                    堆 (Heap)                       │  │
│  │  - 对象实例、数组 (年轻代 + 老年代)                │  │
│  └───────────────────────────────────────────────────┘  │
│  ┌───────────────────┐  ┌───────────────────────────┐  │
│  │   程序计数器      │  │         虚拟机栈           │  │
│  │  (线程私有)       │  │     (每个线程一个栈)       │  │
│  │  - 当前线程执行   │  │  - 栈帧: 局部变量表、操作  │  │
│  │    的字节码行号   │  │    数栈、方法返回地址      │  │
│  └───────────────────┘  └───────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### 类加载过程

```
加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载
```

### 垃圾回收（GC）

**回收算法**

| 算法 | 说明 | 适用场景 |
|-----|------|---------|
| 标记-清除 | 标记存活对象，清除未标记 | 老年代 |
| 标记-整理 | 标记后整理内存碎片 | 老年代 |
| 复制 | 将存活对象复制到新区域 | 年轻代 |

**垃圾收集器**

| 收集器 | 特点 |
|-------|------|
| Serial | 单线程，简单高效 |
| Parallel | 多线程，吞吐量优先 |
| CMS | 并发标记清除，低延迟 |
| G1 | 可控停顿时间，可预测 |
| ZGC / Shenandoah | 超低停顿，TB 级内存 |

### JVM 参数示例

```bash
# 堆大小
-Xms256m -Xmx512m

# 年轻代大小
-XX:NewSize=128m -XX:MaxNewSize=256m

# GC 日志
-Xlog:gc*:file=gc.log:time
```

> 代码示例：见 [BasicsMemoryDemo.java:30-100](src/main/java/com/shuai/jvm/BasicsMemoryDemo.java#L30-L100)

---

## Guava 工具

### BasicsGuavaCollectionDemo.java、BasicsGuavaCacheDemo.java

### 集合工具

```java
// List 分区
Lists.partition(list, 10)

// 列表逆序
Lists.reverse(list)

// 集合操作
Sets.union(set1, set2);      // 并集
Sets.intersection(set1, set2); // 交集
Sets.difference(set1, set2);   // 差集

// MultiMap（一个键对应多个值）
ArrayListMultimap<String, String> multimap = ArrayListMultimap.create();
multimap.put("language", "Java");
multimap.put("language", "Python");

// BiMap（双向映射）
HashBiMap<String, Integer> biMap = HashBiMap.create();
biMap.put("one", 1);
String key = biMap.inverse().get(1);  // "one"

// Table（双键映射）
Table<String, String, Integer> table = HashBasedTable.create();
table.put("row1", "col1", 1);
```

> 代码示例：见 [BasicsGuavaCollectionDemo.java:30-120](src/main/java/com/shuai/guava/BasicsGuavaCollectionDemo.java#L30-L120)

### Guava 缓存

```java
// LoadingCache（自动加载缓存）
LoadingCache<String, String> cache = CacheBuilder.newBuilder()
    .maximumSize(100)           // 最大条目数
    .expireAfterWrite(10, TimeUnit.MINUTES)  // 写入后过期
    .refreshAfterWrite(5, TimeUnit.MINUTES)  // 定期刷新
    .build(new CacheLoader<String, String>() {
        @Override
        public String load(String key) {
            return fetchFromDatabase(key);  // 缓存未命中时加载
        }
    });

String value = cache.get("key");  // 自动加载

// 手动缓存
Cache<String, String> cache = CacheBuilder.newBuilder()
    .maximumSize(1000)
    .expireAfterAccess(30, TimeUnit.MINUTES)
    .build();

cache.put("key", "value");
String value = cache.getIfPresent("key");
```

> 代码示例：见 [BasicsGuavaCacheDemo.java:30-100](src/main/java/com/shuai/guava/BasicsGuavaCacheDemo.java#L30-L100)

### 并发工具

```java
// ListenableFuture
ListenableFuture<String> future = service.submit(task);
Futures.addCallback(future, new FutureCallback<String>() {
    @Override
    public void onSuccess(String result) { }
    @Override
    public void onFailure(Throwable t) { }
});

// RateLimiter（限流）
RateLimiter limiter = RateLimiter.create(10.0);  // 每秒 10 个许可
limiter.acquire();  // 获取许可
```

---

## 设计模式

### BasicsDesignPatternsDemo.java

### 创建型模式

| 模式 | 说明 | 示例 |
|-----|------|-----|
| 单例 | 全局唯一实例 | Runtime.getRuntime() |
| 工厂方法 | 子类决定创建对象 | LoggerFactory |
| 抽象工厂 | 创建相关对象族 | DocumentBuilderFactory |
| 建造者 | 分步构建复杂对象 | StringBuilder |
| 原型 | 克隆已有对象 | clone() |

**单例模式**

```java
public class Singleton {
    private static volatile Singleton instance;
    private Singleton() {}

    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### 结构型模式

| 模式 | 说明 | 示例 |
|-----|------|-----|
| 适配器 | 接口转换 | Arrays.asList() |
| 装饰器 | 动态添加功能 | IO 流 |
| 外观 | 统一接口 | Facade |
| 代理 | 控制访问 | RMI |
| 组合 | 树形结构 | Component |

**装饰器模式**

```java
InputStream is = new BufferedInputStream(
    new FileInputStream("file.txt"));
```

### 行为型模式

| 模式 | 说明 | 示例 |
|-----|------|-----|
| 策略 | 算法族可替换 | Comparator |
| 观察者 | 事件通知 | Observable |
| 责任链 | 请求沿链传递 | FilterChain |
| 模板方法 | 算法骨架 | HttpServlet |
| 状态 | 状态改变行为 | StateMachine |
| 命令 | 请求封装为对象 | Runnable |
| 迭代器 | 统一遍历方式 | Iterator |

**策略模式**

```java
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardStrategy implements PaymentStrategy {
    public void pay(double amount) { /* 信用卡支付 */ }
}

class PayPalStrategy implements PaymentStrategy {
    public void pay(double amount) { /* PayPal 支付 */ }
}

class ShoppingCart {
    private PaymentStrategy strategy;
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }
    public void checkout(double amount) {
        strategy.pay(amount);
    }
}
```

> 代码示例：见 [BasicsDesignPatternsDemo.java:30-200](src/main/java/com/shuai/patterns/BasicsDesignPatternsDemo.java#L30-L200)

---

## 配置管理

### BasicsPropertiesDemo.java

Java 配置管理主要涉及 Properties 文件和系统属性的读取。

### Properties 文件操作

```java
// 加载 Properties 文件
Properties props = new Properties();
try (InputStream in = new FileInputStream("config.properties")) {
    props.load(in);
}

// 获取配置值
String url = props.getProperty("database.url");
String port = props.getProperty("database.port", "3306");  // 默认值

// 设置配置值
props.setProperty("app.name", "MyApp");
props.store(new FileOutputStream("config.properties"), "Comments");
```

> 代码示例：见 [BasicsPropertiesDemo.java:30-80](src/main/java/com/shuai/config/BasicsPropertiesDemo.java#L30-L80)

### 环境变量

```java
// 获取环境变量
String javaHome = System.getenv("JAVA_HOME");
String path = System.getenv("PATH");

// 获取所有环境变量
Map<String, String> env = System.getenv();
```

### 系统属性

```java
// 获取系统属性
String osName = System.getProperty("os.name");
String javaVersion = System.getProperty("java.version");
String userDir = System.getProperty("user.dir");

// 设置系统属性
System.setProperty("custom.property", "value");

// 获取带默认值的属性
String timeout = System.getProperty("request.timeout", "30");
```

### ResourceBundle（资源绑定）

```java
// 加载资源文件（国际化支持）
ResourceBundle bundle = ResourceBundle.getBundle("messages");
String greeting = bundle.getString("greeting");
```

---

## 单元测试

### BasicsTestingDemo.java

单元测试是保证代码质量的重要手段，Java 常用 JUnit 5 进行测试。

### JUnit 5 基础

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @Test
    void testAdd() {
        Calculator calc = new Calculator();
        assertEquals(4, calc.add(2, 2));
    }

    @Test
    void testDivide() {
        Calculator calc = new Calculator();
        assertThrows(ArithmeticException.class, () -> calc.divide(1, 0));
    }
}
```

> 代码示例：见 [BasicsTestingDemo.java:30-80](src/main/java/com/shuai/testing/BasicsTestingDemo.java#L30-L80)

### 断言方法

| 断言 | 说明 |
|-----|------|
| `assertEquals(expected, actual)` | 相等断言 |
| `assertNotEquals(expected, actual)` | 不等断言 |
| `assertTrue(condition)` | 条件为真 |
| `assertFalse(condition)` | 条件为假 |
| `assertNull(object)` | 对象为空 |
| `assertNotNull(object)` | 对象非空 |
| `assertThrows(exception, executable)` | 抛出异常 |
| `assertTimeout(duration, executable)` | 超时测试 |
| `assertAll(executables)` | 分组断言 |

```java
// 分组断言
assertAll("person",
    () -> assertEquals("John", person.getFirstName()),
    () -> assertEquals("Doe", person.getLastName())
);
```

### 测试生命周期

```java
class LifecycleTest {
    @BeforeAll
    static void setupAll() { }  // 所有测试前执行一次

    @BeforeEach
    void setup() { }  // 每个测试前执行

    @Test
    void test1() { }

    @Test
    void test2() { }

    @AfterEach
    void teardown() { }  // 每个测试后执行

    @AfterAll
    static void teardownAll() { }  // 所有测试后执行一次
}
```

### 参数化测试

```java
@ParameterizedTest
@ValueSource(ints = {1, 2, 3, 4, 5})
void testIsPrime(int number) {
    assertTrue(NumberUtils.isPrime(number));
}

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5",
    "10, 20, 30"
})
void testAdd(int a, int b, int expected) {
    assertEquals(expected, a + b);
}
```

### Mockito 模拟

```java
import org.mockito.Mockito;

class UserServiceTest {

    @Test
    void testGetUser() {
        // 创建模拟对象
        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        UserService service = new UserService(mockRepo);

        // 设置模拟行为
        Mockito.when(mockRepo.findById(1L)).thenReturn(new User("John"));

        // 执行测试
        User user = service.getUser(1L);

        // 验证结果
        assertNotNull(user);
        assertEquals("John", user.getName());

        // 验证交互
        Mockito.verify(mockRepo).findById(1L);
    }
}
```

### 常用测试注解

| 注解 | 说明 |
|-----|------|
| `@Test` | 标记测试方法 |
| `@ParameterizedTest` | 参数化测试 |
| `@RepeatedTest(n)` | 重复测试 |
| `@Disabled` | 禁用测试 |
| `@DisplayName("名称")` | 显示名称 |
| `@Tag("tag")` | 测试分组 |
| `@Timeout(value, unit)` | 超时限制 |

---

## 运行指南

### Maven 运行

```bash
# 运行所有演示
mvn -pl basics exec:java -Dexec.mainClass=com.shuai.BasicsDemo

# 运行单个演示
mvn -pl basics exec:java -Dexec.mainClass=com.shuai.BasicsDataTypesDemo

# 编译项目
mvn -pl basics compile
```

### 直接运行

```bash
cd basics
mvn compile
java -cp target/classes com.shuai.BasicsDemo
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest=BasicsDemoTest

# 查看测试报告
cat target/surefire-reports/com.shuai.BasicsDemoTest.txt
```

---

## Skills 速查

```yaml
basics:
  types:
    - Primitive Types (byte, short, int, long, float, double, char, boolean)
    - Wrapper Classes (Integer, Long, Double, Boolean, Character)
    - Type Conversion (implicit, explicit)
    - Variable Scope (instance, local, static)
  operators:
    - Arithmetic (+, -, *, /, %, ++, --)
    - Comparison (>, <, ==, !=, >=, <=)
    - Logical (&&, ||, !)
    - Bitwise (&, |, ^, ~, <<, >>, >>>)
    - Ternary (condition ? a : b)
  string:
    - String (immutability, string pool)
    - StringBuilder (mutable, efficient)
    - StringBuffer (thread-safe)
    - String Methods (length, charAt, substring, split, etc.)
  utility:
    - Objects (isNull, equals, toString, hashCode)
    - Collections (sort, reverse, unmodifiable, synchronized)
    - Math (abs, sqrt, pow, random)
  array:
    - Array Creation (static, dynamic)
    - Multi-dimensional Arrays
    - Arrays Utility Class
  control:
    - Conditional (if-else, switch, switch expression)
    - Loop (for, foreach, while, do-while)
    - Jump (break, continue, return)
    - Exception Handling (try-catch, throws, custom exceptions)
  oop:
    - Class and Object
    - Encapsulation (private, getter/setter)
    - Inheritance (extends, super)
    - Polymorphism (override, overload)
    - Abstract Class vs Interface
  generics:
    - Generic Class (<T>)
    - Generic Method (<T>)
    - Wildcard (? extends, ? super)
    - Type Erasure
  reflection:
    - Class Object (class.class, getClass(), forName())
    - Constructor (getConstructors, newInstance())
    - Field (getFields, getDeclaredFields, set, get)
    - Method (getMethods, getDeclaredMethods, invoke)
  bigdecimal:
    - Precision Problem
    - BigDecimal Operations (add, subtract, multiply, divide)
    - Rounding Mode (HALF_UP, HALF_EVEN, etc.)
    - compareTo vs equals
  spi:
    - ServiceLoader
    - META-INF/services Configuration
    - JDBC Driver Loading
  syntax_sugar:
    - Auto-boxing/unboxing
    - Enhanced for loop
    - Lambda expressions
    - Method reference
    - Stream API (filter, map, reduce, collect)
    - Switch expression
    - Record (Java 14+)
    - Sealed class (Java 17+)
    - Pattern matching (Java 16+)
    - var keyword (Java 10+)
    - Text blocks (Java 13+)
  collections:
    - List (ArrayList, LinkedList, Vector)
    - Set (HashSet, TreeSet, LinkedHashSet)
    - Map (HashMap, TreeMap, LinkedHashMap)
    - Queue (LinkedList, PriorityQueue, ArrayDeque)
    - Collections Utility Methods
  io:
    - InputStream/OutputStream (byte stream)
    - Reader/Writer (character stream)
    - Buffered Stream (buffered IO)
    - Object Stream (serialization)
    - NIO (Path, Files, Channel, Buffer)
  datetime:
    - LocalDate (date only)
    - LocalTime (time only)
    - LocalDateTime (date and time)
    - ZonedDateTime (with timezone)
    - Duration (time-based)
    - Period (date-based)
    - DateTimeFormatter (formatting)
  threads:
    - Thread Creation (extends Thread, implements Runnable)
    - Thread Lifecycle (NEW, RUNNABLE, BLOCKED, WAITING, TERMINATED)
    - Thread Synchronization (synchronized, ReentrantLock)
    - Thread Pool (ExecutorService)
    - Concurrent Utilities (CountDownLatch, CompletableFuture)
  json:
    - Jackson (ObjectMapper, JsonNode)
    - Gson (toJson, fromJson, TypeToken)
    - JSON Serialization/Deserialization
    - Annotations (@JsonProperty, @JsonIgnore)
  logging:
    - SLF4J (Simple Logging Facade)
    - Logback (implementation)
    - Log Levels (TRACE, DEBUG, INFO, WARN, ERROR)
    - Logger Configuration
    - Placeholder substitution
  optional:
    - Optional Creation (empty, of, ofNullable)
    - isPresent() and ifPresent()
    - orElse() and orElseGet()
    - map() and flatMap()
    - filter()
  annotations:
    - Built-in Annotations (@Override, @Deprecated)
    - Meta-Annotations (@Retention, @Target)
    - Custom Annotations
    - Annotation Processors (Reflection)
  network:
    - InetAddress (DNS lookup)
    - URL and URLConnection
    - HttpClient (Java 11+)
    - TCP Socket (ServerSocket, Socket)
    - UDP Socket (DatagramSocket)
  netty:
    - EventLoopGroup (boss, worker)
    - Channel and ChannelPipeline
    - ChannelHandler and ChannelInboundHandlerAdapter
    - ByteBuf (dynamic buffer)
    - Bootstrap (ServerBootstrap)
  jdbc:
    - DriverManager and Connection
    - Statement and PreparedStatement
    - ResultSet (query results)
    - Transaction Management (commit, rollback)
    - Connection Pool (HikariCP)
  jvm:
    - Memory Areas (Heap, MetaSpace, Stack)
    - Class Loading (Loading, Linking, Initialization)
    - Garbage Collection (Mark-Sweep, Copy, Mark-Compact)
    - GC Collectors (Serial, Parallel, CMS, G1)
    - JVM Parameters (heap size, GC logging)
  guava:
    - Collections (Lists, Sets, Maps)
    - MultiMap and BiMap
    - Table (row-column value)
    - Cache (LoadingCache, Cache)
    - RateLimiter (rate limiting)
  design_patterns:
    - Creational (Singleton, Factory, Builder, Prototype)
    - Structural (Adapter, Decorator, Proxy, Facade)
    - Behavioral (Strategy, Observer, Chain, Template)
  properties:
    - Properties (load, store, getProperty)
    - System Properties (getProperty, setProperty)
    - Environment Variables (getenv)
    - ResourceBundle (i18n support)
  testing:
    - JUnit 5 (@Test, @BeforeEach, @AfterEach)
    - Assertions (assertEquals, assertThrows, assertAll)
    - Parameterized Tests (@ParameterizedTest)
    - Lifecycle (@BeforeAll, @AfterAll)
    - Mockito (mock, when, verify)
```
