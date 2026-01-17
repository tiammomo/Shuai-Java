# Java 学习项目

> 基于 JDK 21 的 Java 学习项目，从入门到进阶的完整学习路径。

## 核心模块

| 模块 | 路径 | 描述 |
|-----|------|------|
| **basics** | [basics/](basics/) | Java 基础语法模块，基于 JDK 21 的系统性学习 |
| **mq** | [mq/](mq/) | 消息队列模块，涵盖 Kafka、RocketMQ、RabbitMQ |

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd Shuai-Java
```

### 2. 环境配置

请参考 [环境配置指南](learn_docs/环境配置指南.md) 完成开发环境搭建。

### 3. 运行示例

```bash
# 运行 basics 模块
mvn -pl basics exec:java -Dexec.mainClass=com.shuai.BasicsDemo

# 运行 mq 模块
mvn -pl mq exec:java -Dexec.mainClass=com.shuai.MqDemo
```

## 环境要求

| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 21 LTS | 必须使用 JDK 21 或更高版本 |
| Maven | 3.8+ | 项目构建工具 |

## 学习文档

项目配套学习文档位于 [learn_docs](learn_docs/) 目录：

| 文档 | 描述 |
|-----|------|
| [学习路线图](learn_docs/学习路线图.md) | 完整的学习路径规划 |
| [环境配置指南](learn_docs/环境配置指南.md) | JDK 21 和 Maven 环境配置 |
| [项目结构说明](learn_docs/项目结构说明.md) | 项目目录结构和规范 |
| [代码规范](learn_docs/代码规范.md) | Java 编码规范和最佳实践 |
| [JDK21新特性](learn_docs/JDK21新特性.md) | JDK 21 新特性详解 |

## 参考资料

### 官方文档

- [Java SE 21 文档](https://docs.oracle.com/en/java/javase/21/)
- [Java 语言规范](https://docs.oracle.com/javase/specs/)
- [JDK 21 Release Notes](https://www.oracle.com/java/technologies/javase/21-relnote-issues.html)

### 学习资源

- [Java 教程 - 廖雪峰](https://www.liaoxuefeng.com/wiki/1252599548343744)
- [Java 核心技术卷](https://www.amazon.com/Core-Java-Volume-I-Fundamentals/dp/0137673629)
- [Effective Java](https://www.amazon.com/Effective-Java-Joshua-Bloch/dp/0134685997)

---

**作者**: tianmomo
**创建时间**: 2026-01-15
**最后更新**: 2026-01-17
