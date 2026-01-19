# 消息队列模块

> 消息队列学习模块，包含 Kafka、RocketMQ、RabbitMQ 的核心概念和使用示例。

## 学习文档

本模块配套学习文档位于 [learn_docs](learn_docs/README.md) 目录：

### 消息队列

| 主题 | 文档链接 |
|------|----------|
| [Kafka](#kafka-知识点) | [learn_docs/01-kafka/](learn_docs/01-kafka/README.md) |
| [RocketMQ](#rocketmq-知识点) | [learn_docs/02-rocketmq/](learn_docs/02-rocketmq/README.md) |
| [RabbitMQ](#rabbitmq-知识点) | [learn_docs/03-rabbitmq/](learn_docs/03-rabbitmq/README.md) |

> **提示**: 点击主题名称可跳转到下方对应章节。

## 目录

- [简介](#简介)
- [文件结构](#文件结构)
- [测试文件](#测试文件)
- [公共模块](#公共模块)
- [领域模型](#领域模型)
- [Kafka 知识点](#kafka-知识点)
- [RocketMQ 知识点](#rocketmq-知识点)
- [RabbitMQ 知识点](#rabbitmq-知识点)
- [接口设计](#接口设计)
- [配置加载](#配置加载)
- [运行示例](#运行示例)

## 简介

本模块涵盖主流消息队列框架，包括 Kafka、RocketMQ、RabbitMQ 的核心概念和使用示例。

消息队列（Message Queue）是一种异步通信机制，用于不同系统或组件之间的解耦、异步和削峰填谷。

## 文件结构

```
mq/src/main/java/com/shuai/
├── MqDemo.java                      # 模块入口，编排各演示类
├── MqOverviewDemo.java              # 消息队列概述：概念、价值、模式
├── MqComparisonDemo.java            # MQ 对比：特性对比与选型建议
│
├── common/                          # 公共模块
│   ├── config/
│   │   └── MqConfigLoader.java      # 配置加载器
│   ├── constants/
│   │   └── MqConstants.java         # 常量定义
│   ├── exception/
│   │   └── MqException.java         # 自定义异常
│   └── interfaces/                  # 接口定义
│       ├── Callback.java            # 异步回调接口
│       ├── MessageListener.java     # 消息监听器接口
│       ├── MqConsumer.java          # 消费者接口
│       └── MqProducer.java          # 生产者接口
│
├── model/                           # 领域模型
│   ├── Message.java                 # 通用消息模型
│   └── MessageResult.java           # 消息发送结果
│
├── kafka/                           # Kafka 演示
│   ├── api/
│   │   ├── KafkaProducerDemo.java       # 生产者 API
│   │   └── KafkaConsumerDemo.java       # 消费者 API
│   ├── advanced/
│   │   ├── KafkaTransactionDemo.java    # 事务消息
│   │   └── KafkaIdempotentDemo.java     # 幂等与精确一次
│   ├── config/
│   │   └── KafkaProducerConfig.java     # 生产者配置
│   ├── consumer/
│   │   └── KafkaConsumerImpl.java       # 消费者接口实现
│   └── producer/
│       └── KafkaProducerImpl.java       # 生产者接口实现
│
├── rocketmq/                        # RocketMQ 演示
│   ├── api/
│   │   ├── RocketMqProducerDemo.java    # 生产者 API
│   │   └── RocketMqConsumerDemo.java    # 消费者 API
│   ├── advanced/
│   │   ├── RocketMqTransactionDemo.java # 事务消息
│   │   └── RocketMqDelayDemo.java       # 延迟消息
│   ├── config/
│   │   └── RocketMqProducerConfig.java  # 生产者/消费者配置
│   ├── consumer/
│   │   └── RocketMqConsumerImpl.java    # 消费者接口实现
│   └── producer/
│       └── RocketMqProducerImpl.java    # 生产者接口实现
│
└── rabbitmq/                        # RabbitMQ 演示
    ├── api/
    │   ├── RabbitMqProducerDemo.java    # 生产者 API
    │   └── RabbitMqConsumerDemo.java    # 消费者 API
    ├── advanced/
    │   ├── RabbitMqDelayDemo.java       # 延迟队列
    │   ├── RabbitMqPriorityDemo.java    # 优先级队列
    │   └── RabbitMqDLXDemo.java         # 死信队列
    ├── config/
    │   └── RabbitMqConnectionConfig.java # 连接配置
    ├── consumer/
    │   └── RabbitMqConsumerImpl.java    # 消费者接口实现
    ├── exchange/
    │   ├── DirectExchangeDemo.java      # Direct Exchange
    │   ├── FanoutExchangeDemo.java      # Fanout Exchange
    │   ├── TopicExchangeDemo.java       # Topic Exchange
    │   └── HeadersExchangeDemo.java     # Headers Exchange
    └── producer/
        └── RabbitMqProducerImpl.java    # 生产者接口实现
```

## 测试文件

**代码位置**: [src/test/java/com/shuai/](src/test/java/com/shuai/)

| 路径 | 说明 | 行号 |
|------|------|------|
| [MqConfigLoaderTest.java](src/test/java/com/shuai/common/MqConfigLoaderTest.java) | 配置加载器测试 | [#L18](src/test/java/com/shuai/common/MqConfigLoaderTest.java#L18) getString, [#L34](src/test/java/com/shuai/common/MqConfigLoaderTest.java#L34) getInt, [#L54](src/test/java/com/shuai/common/MqConfigLoaderTest.java#L54) getLong, [#L63](src/test/java/com/shuai/common/MqConfigLoaderTest.java#L63) getBoolean |
| [MessageModelTest.java](src/test/java/com/shuai/common/MessageModelTest.java) | 消息模型测试 | [#L17](src/test/java/com/shuai/common/MessageModelTest.java#L17) Message 构建, [#L32](src/test/java/com/shuai/common/MessageModelTest.java#L32) MessageResult 成功, [#L44](src/test/java/com/shuai/common/MessageModelTest.java#L44) MessageResult 失败 |
| [KafkaProducerTest.java](src/test/java/com/shuai/kafka/KafkaProducerTest.java) | Kafka 生产者测试 | [#L20](src/test/java/com/shuai/kafka/KafkaProducerTest.java#L20) 同步发送, [#L44](src/test/java/com/shuai/kafka/KafkaProducerTest.java#L44) 异步发送, [#L68](src/test/java/com/shuai/kafka/KafkaProducerTest.java#L68) 批量发送 |
| [RocketMqConsumerTest.java](src/test/java/com/shuai/rocketmq/RocketMqConsumerTest.java) | RocketMQ 消费者测试 | [#L17](src/test/java/com/shuai/rocketmq/RocketMqConsumerTest.java#L17) 订阅, [#L34](src/test/java/com/shuai/rocketmq/RocketMqConsumerTest.java#L34) 拉取消息 |
| [RabbitMqProducerTest.java](src/test/java/com/shuai/rabbitmq/RabbitMqProducerTest.java) | RabbitMQ 生产者测试 | [#L18](src/test/java/com/shuai/rabbitmq/RabbitMqProducerTest.java#L18) 发送消息, [#L44](src/test/java/com/shuai/rabbitmq/RabbitMqProducerTest.java#L44) 批量发送 |
| [ExchangeDemoTest.java](src/test/java/com/shuai/rabbitmq/exchange/ExchangeDemoTest.java) | Exchange 测试 | [#L17](src/test/java/com/shuai/rabbitmq/exchange/ExchangeDemoTest.java#L17) Direct, [#L30](src/test/java/com/shuai/rabbitmq/exchange/ExchangeDemoTest.java#L30) Fanout, [#L43](src/test/java/com/shuai/rabbitmq/exchange/ExchangeDemoTest.java#L43) Topic |

## 公共模块

**代码位置**: [common/](src/main/java/com/shuai/common/)

| 类别 | 文件 | 说明 |
|------|------|------|
| 配置加载 | [MqConfigLoader.java](src/main/java/com/shuai/common/config/MqConfigLoader.java) | 从 properties 文件加载配置 ([load#L26](src/main/java/com/shuai/common/config/MqConfigLoader.java#L26), [getString#L64](src/main/java/com/shuai/common/config/MqConfigLoader.java#L64)) |
| 常量定义 | [MqConstants.java](src/main/java/com/shuai/common/constants/MqConstants.java) | MQ 相关常量 ([KAFKA#L18](src/main/java/com/shuai/common/constants/MqConstants.java#L18), [ROCKETMQ#L28](src/main/java/com/shuai/common/constants/MqConstants.java#L28)) |
| 自定义异常 | [MqException.java](src/main/java/com/shuai/common/exception/MqException.java) | MQ 业务异常 ([构造#L22](src/main/java/com/shuai/common/exception/MqException.java#L22)) |
| 回调接口 | [Callback.java](src/main/java/com/shuai/common/interfaces/Callback.java) | 异步发送回调 ([onComplete#L12](src/main/java/com/shuai/common/interfaces/Callback.java#L12)) |
| 监听器接口 | [MessageListener.java](src/main/java/com/shuai/common/interfaces/MessageListener.java) | 消息监听器 ([onMessage#L12](src/main/java/com/shuai/common/interfaces/MessageListener.java#L12)) |
| 生产者接口 | [MqProducer.java](src/main/java/com/shuai/common/interfaces/MqProducer.java) | 生产者抽象接口 ([send#L17](src/main/java/com/shuai/common/interfaces/MqProducer.java#L17), [sendAsync#L23](src/main/java/com/shuai/common/interfaces/MqProducer.java#L23)) |
| 消费者接口 | [MqConsumer.java](src/main/java/com/shuai/common/interfaces/MqConsumer.java) | 消费者抽象接口 ([subscribe#L17](src/main/java/com/shuai/common/interfaces/MqConsumer.java#L17), [poll#L29](src/main/java/com/shuai/common/interfaces/MqConsumer.java#L29)) |

## 领域模型

**代码位置**: [model/](src/main/java/com/shuai/model/)

| 文件 | 说明 | 关键属性/方法 |
|------|------|---------------|
| [Message.java](src/main/java/com/shuai/model/Message.java) | 通用消息模型，包含 topic、tag、body、key 等属性 | [#L25](src/main/java/com/shuai/model/Message.java#L25) topic, [#L30](src/main/java/com/shuai/model/Message.java#L30) tag, [#L35](src/main/java/com/shuai/model/Message.java#L35) body, [#L40](src/main/java/com/shuai/model/Message.java#L40) key, [#L62](src/main/java/com/shuai/model/Message.java#L62) builder() |
| [MessageResult.java](src/main/java/com/shuai/model/MessageResult.java) | 消息发送结果，包含状态、消息ID、偏移量等信息 | [#L20](src/main/java/com/shuai/model/MessageResult.java#L20) success(), [#L32](src/main/java/com/shuai/model/MessageResult.java#L32) fail(), [#L44](src/main/java/com/shuai/model/MessageResult.java#L44) getMessageId(), [#L56](src/main/java/com/shuai/model/MessageResult.java#L56) getOffset() |

## Kafka 知识点

**代码位置**: [kafka/](src/main/java/com/shuai/kafka/)

### 接口实现

| 文件 | 说明 | 关键方法 |
|------|------|----------|
| [KafkaProducerImpl.java](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java) | `MqProducer` 接口的 Kafka 实现 | [#L42](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java#L42) send, [#L63](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java#L63) sendAsync, [#L87](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java#L87) start |
| [KafkaConsumerImpl.java](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java) | `MqConsumer` 接口的 Kafka 实现 | [#L28](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java#L28) subscribe, [#L46](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java#L46) poll, [#L67](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java#L67) start |

### API 层

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 生产者创建 | [KafkaProducerDemo.java#L53](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L53) | KafkaProducer 配置 |
| 同步发送 | [KafkaProducerDemo.java#L70](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L70) | producer.send(record) |
| 异步发送 | [KafkaProducerDemo.java#L101](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L101) | SendCallback 回调 |
| 分区策略 | [KafkaProducerDemo.java#L141](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L141) | Key 哈希/轮询分配 |
| 批量发送 | [KafkaProducerDemo.java#L183](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L183) | batch.size, linger.ms |
| 可靠发送 | [KafkaProducerDemo.java#L220](src/main/java/com/shuai/kafka/api/KafkaProducerDemo.java#L220) | acks=all, 重试配置 |
| 消费者创建 | [KafkaConsumerDemo.java#L47](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L47) | KafkaConsumer 配置 |
| 订阅主题 | [KafkaConsumerDemo.java#L156](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L156) | subscribe() 方法 |
| 拉取消息 | [KafkaConsumerDemo.java#L66](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L66) | poll() 方法 |
| 手动提交 | [KafkaConsumerDemo.java#L98](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L98) | commitSync |
| 自动提交 | 默认启用 | enable.auto.commit |
| 消费位点 | [KafkaConsumerDemo.java#L133](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L133) | commitSync() |
| 消费者组 | [KafkaConsumerDemo.java#L179](src/main/java/com/shuai/kafka/api/KafkaConsumerDemo.java#L179) | 分区分配策略 |

### 高级特性

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 事务消息 | [KafkaTransactionDemo.java](src/main/java/com/shuai/kafka/advanced/KafkaTransactionDemo.java) | initTransactions() ([初始化#L18](src/main/java/com/shuai/kafka/advanced/KafkaTransactionDemo.java#L18), [发送#L39](src/main/java/com/shuai/kafka/advanced/KafkaTransactionDemo.java#L39)) |
| 幂等配置 | [KafkaIdempotentDemo.java](src/main/java/com/shuai/kafka/advanced/KafkaIdempotentDemo.java) | enable.idempotence ([配置#L22](src/main/java/com/shuai/kafka/advanced/KafkaIdempotentDemo.java#L22), [精确一次#L50](src/main/java/com/shuai/kafka/advanced/KafkaIdempotentDemo.java#L50)) |
| 生产者配置 | [KafkaProducerConfig.java](src/main/java/com/shuai/kafka/config/KafkaProducerConfig.java) | 可靠/高性能配置 ([基础配置#L19](src/main/java/com/shuai/kafka/config/KafkaProducerConfig.java#L19), [可靠性#L42](src/main/java/com/shuai/kafka/config/KafkaProducerConfig.java#L42)) |

## RocketMQ 知识点

**代码位置**: [rocketmq/](src/main/java/com/shuai/rocketmq/)

### 接口实现

| 文件 | 说明 | 关键方法 |
|------|------|----------|
| [RocketMqProducerImpl.java](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java) | `MqProducer` 接口的 RocketMQ 实现 | [#L45](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java#L45) send, [#L74](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java#L74) sendAsync, [#L98](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java#L98) start |
| [RocketMqConsumerImpl.java](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java) | `MqConsumer` 接口的 RocketMQ 实现 | [#L27](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java#L27) subscribe, [#L47](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java#L47) poll, [#L68](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java#L68) start |

### API 层

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 生产者创建 | [RocketMqProducerDemo.java#L53](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L53) | DefaultMQProducer 配置 |
| 同步发送 | [RocketMqProducerDemo.java#L72](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L72) | producer.send(msg) |
| 异步发送 | [RocketMqProducerDemo.java#L103](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L103) | SendCallback |
| 单向发送 | [RocketMqProducerDemo.java#L148](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L148) | producer.sendOneway() |
| 延迟消息 | [RocketMqProducerDemo.java#L176](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L176) | setDelayTimeLevel() |
| 顺序消息 | [RocketMqProducerDemo.java#L206](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L206) | MessageQueueSelector |
| 事务消息 | [RocketMqTransactionDemo.java](src/main/java/com/shuai/rocketmq/advanced/RocketMqTransactionDemo.java) | TransactionListener |
| 消息标签 | [RocketMqProducerDemo.java#L240](src/main/java/com/shuai/rocketmq/api/RocketMqProducerDemo.java#L240) | Tag 二级分类 |
| Push 消费 | [RocketMqConsumerDemo.java#L48](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java#L48) | MessageListenerConcurrently |
| Pull 消费 | [RocketMqConsumerDemo.java#L136](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java#L136) | DefaultMQPushConsumer |
| 消息过滤 | [RocketMqConsumerDemo.java#L156](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java#L156) | Tag/SQL 过滤 |
| 顺序消费 | [RocketMqConsumerDemo.java#L102](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java#L102) | MessageListenerOrderly |
| 消费模式 | [RocketMqConsumerDemo.java#L187](src/main/java/com/shuai/rocketmq/api/RocketMqConsumerDemo.java#L187) | CLUSTERING/BROADCASTING |

### 高级特性

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 事务消息 | [RocketMqTransactionDemo.java](src/main/java/com/shuai/rocketmq/advanced/RocketMqTransactionDemo.java) | 半消息 + 回查 ([执行本地事务#L25](src/main/java/com/shuai/rocketmq/advanced/RocketMqTransactionDemo.java#L25), [回查#L44](src/main/java/com/shuai/rocketmq/advanced/RocketMqTransactionDemo.java#L44)) |
| 延迟消息 | [RocketMqDelayDemo.java](src/main/java/com/shuai/rocketmq/advanced/RocketMqDelayDemo.java) | setDelayTimeLevel ([延迟级别#L20](src/main/java/com/shuai/rocketmq/advanced/RocketMqDelayDemo.java#L20), [消费#L40](src/main/java/com/shuai/rocketmq/advanced/RocketMqDelayDemo.java#L40)) |
| 配置类 | [RocketMqProducerConfig.java](src/main/java/com/shuai/rocketmq/config/RocketMqProducerConfig.java) | 生产者/消费者配置 ([生产者#L19](src/main/java/com/shuai/rocketmq/config/RocketMqProducerConfig.java#L19), [消费者#L50](src/main/java/com/shuai/rocketmq/config/RocketMqProducerConfig.java#L50)) |

## RabbitMQ 知识点

**代码位置**: [rabbitmq/](src/main/java/com/shuai/rabbitmq/)

### 接口实现

| 文件 | 说明 | 关键方法 |
|------|------|----------|
| [RabbitMqProducerImpl.java](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java) | `MqProducer` 接口的 RabbitMQ 实现 | [#L52](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java#L52) send, [#L77](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java#L77) sendAsync, [#L98](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java#L98) start |
| [RabbitMqConsumerImpl.java](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java) | `MqConsumer` 接口的 RabbitMQ 实现 | [#L28](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java#L28) subscribe, [#L47](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java#L47) poll, [#L66](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java#L66) start |

### RabbitMQ API 层

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 连接创建 | [RabbitMqProducerDemo.java#L61](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L61) | ConnectionFactory 配置 |
| Channel 操作 | [RabbitMqProducerDemo.java#L81](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L81) | confirmSelect() 开启确认 |
| Exchange 声明 | [RabbitMqProducerDemo.java#L98](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L98) | exchangeDeclare() 声明交换机 |
| Queue 声明 | [RabbitMqProducerDemo.java#L127](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L127) | queueDeclare() 声明队列 |
| Binding 创建 | [RabbitMqProducerDemo.java#L155](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L155) | queueBind() 绑定队列到交换机 |
| 发布确认 | [RabbitMqProducerDemo.java#L212](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L212) | waitForConfirms() 等待确认 |
| 事务消息 | [RabbitMqProducerDemo.java#L248](src/main/java/com/shuai/rabbitmq/api/RabbitMqProducerDemo.java#L248) | txSelect/txCommit/txRollback |
| 消费者创建 | [RabbitMqConsumerDemo.java#L58](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L58) | basicConsume() 创建消费者 |
| 推模式消费 | [RabbitMqConsumerDemo.java#L91](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L91) | DeliverCallback 回调处理 |
| 拉模式消费 | [RabbitMqConsumerDemo.java#L132](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L132) | basicGet() 主动拉取 |
| 消息确认 | [RabbitMqConsumerDemo.java#L209](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L209) | basicAck() 确认消息 |
| QoS 设置 | [RabbitMqConsumerDemo.java#L166](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L166) | basicQos() 预取控制 |
| 拒绝消息 | [RabbitMqConsumerDemo.java#L256](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L256) | basicReject() 拒绝消息 |
| 消费者标签 | [RabbitMqConsumerDemo.java#L294](src/main/java/com/shuai/rabbitmq/api/RabbitMqConsumerDemo.java#L294) | 自定义消费者标签 |

### Exchange 类型

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| Direct Exchange | [DirectExchangeDemo.java](src/main/java/com/shuai/rabbitmq/exchange/DirectExchangeDemo.java) | 精确匹配 routingKey |
| Fanout Exchange | [FanoutExchangeDemo.java](src/main/java/com/shuai/rabbitmq/exchange/FanoutExchangeDemo.java) | 广播到所有队列 |
| Topic Exchange | [TopicExchangeDemo.java](src/main/java/com/shuai/rabbitmq/exchange/TopicExchangeDemo.java) | 通配符匹配 (*, #) |
| Headers Exchange | [HeadersExchangeDemo.java](src/main/java/com/shuai/rabbitmq/exchange/HeadersExchangeDemo.java) | 基于消息头匹配 (x-match=all/any) |

### 高级特性

| 知识点 | 文件位置 | 说明 |
|--------|----------|------|
| 延迟队列 | [RabbitMqDelayDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDelayDemo.java) | TTL + DLX ([创建#L26](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDelayDemo.java#L26), [测试#L52](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDelayDemo.java#L52)) |
| 优先级队列 | [RabbitMqPriorityDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqPriorityDemo.java) | x-max-priority ([声明#L22](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqPriorityDemo.java#L22), [发送#L45](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqPriorityDemo.java#L45)) |
| 死信队列 | [RabbitMqDLXDemo.java](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDLXDemo.java) | x-dead-letter-exchange ([配置#L24](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDLXDemo.java#L24), [消息路由#L50](src/main/java/com/shuai/rabbitmq/advanced/RabbitMqDLXDemo.java#L50)) |
| 连接配置 | [RabbitMqConnectionConfig.java](src/main/java/com/shuai/rabbitmq/config/RabbitMqConnectionConfig.java) | 连接工厂配置 ([连接#L19](src/main/java/com/shuai/rabbitmq/config/RabbitMqConnectionConfig.java#L19), [信道#L45](src/main/java/com/shuai/rabbitmq/config/RabbitMqConnectionConfig.java#L45)) |

## 模块入口

**代码位置**: [src/main/java/com/shuai/](src/main/java/com/shuai/)

| 文件 | 说明 | 关键行号 |
|------|------|----------|
| [MqDemo.java](src/main/java/com/shuai/MqDemo.java) | 模块入口，编排各演示类 | [#L12](src/main/java/com/shuai/MqDemo.java#L12) runAllDemos, [#L26](src/main/java/com/shuai/MqDemo.java#L26) kafkaDemo, [#L50](src/main/java/com/shuai/MqDemo.java#L50) rocketMqDemo, [#L74](src/main/java/com/shuai/MqDemo.java#L74) rabbitMqDemo |
| [MqOverviewDemo.java](src/main/java/com/shuai/MqOverviewDemo.java) | 消息队列概述：概念、价值、模式 | [#L19](src/main/java/com/shuai/MqOverviewDemo.java#L19) 消息模型, [#L48](src/main/java/com/shuai/MqOverviewDemo.java#L48) pointToPoint, [#L71](src/main/java/com/shuai/MqOverviewDemo.java#L71) publishSubscribe |
| [MqComparisonDemo.java](src/main/java/com/shuai/MqComparisonDemo.java) | MQ 对比：特性对比与选型建议 | [#L92](src/main/java/com/shuai/MqComparisonDemo.java#L92) mqComparison, [#L181](src/main/java/com/shuai/MqComparisonDemo.java#L181) performanceComparison, [#L273](src/main/java/com/shuai/MqComparisonDemo.java#L273) reliabilityGuarantee, [#L383](src/main/java/com/shuai/MqComparisonDemo.java#L383) selectionGuide, [#L455](src/main/java/com/shuai/MqComparisonDemo.java#L455) usageNotes |

## 运行示例

```bash
# 编译项目
mvn compile

# 运行所有演示
mvn exec:java -Dexec.mainClass=com.shuai.MqDemo

# 运行测试
mvn test

# 生成测试报告
mvn test jacoco:report
```

## 接口设计

本模块定义了统一的 MQ 操作接口，便于在不同消息队列之间切换。

### MqProducer - 生产者接口

```java
public interface MqProducer {
    // 发送同步消息
    MessageResult send(Message message);

    // 发送异步消息（带回调）
    void sendAsync(Message message, Callback callback);

    // 发送单向消息
    void sendOneWay(Message message);

    // 批量发送
    MessageResult[] sendBatch(Message[] messages);

    // 启动生产者
    void start();

    // 关闭生产者
    void shutdown();
}
```

**实现类**:
- [KafkaProducerImpl](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java) - Kafka 生产者实现 ([同步发送#L42](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java#L42), [异步发送#L63](src/main/java/com/shuai/kafka/producer/KafkaProducerImpl.java#L63))
- [RocketMqProducerImpl](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java) - RocketMQ 生产者实现 ([同步发送#L45](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java#L45), [异步发送#L74](src/main/java/com/shuai/rocketmq/producer/RocketMqProducerImpl.java#L74))
- [RabbitMqProducerImpl](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java) - RabbitMQ 生产者实现 ([发送消息#L52](src/main/java/com/shuai/rabbitmq/producer/RabbitMqProducerImpl.java#L52))

### MqConsumer - 消费者接口

```java
public interface MqConsumer {
    // 订阅主题
    void subscribe(String topic);
    void subscribe(String topic, String tags);

    // 拉取单条消息
    Message poll(long timeoutMs);

    // 批量拉取消息
    Message[] pollBatch(long timeoutMs, int maxCount);

    // 提交消费进度
    void commit(Message message);

    // 启动消费者
    void start();

    // 关闭消费者
    void shutdown();
}
```

**实现类**:
- [KafkaConsumerImpl](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java) - Kafka 消费者实现 ([订阅#L28](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java#L28), [拉取#L46](src/main/java/com/shuai/kafka/consumer/KafkaConsumerImpl.java#L46))
- [RocketMqConsumerImpl](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java) - RocketMQ 消费者实现 ([订阅#L27](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java#L27), [拉取#L47](src/main/java/com/shuai/rocketmq/consumer/RocketMqConsumerImpl.java#L47))
- [RabbitMqConsumerImpl](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java) - RabbitMQ 消费者实现 ([订阅#L28](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java#L28), [拉取#L47](src/main/java/com/shuai/rabbitmq/consumer/RabbitMqConsumerImpl.java#L47))

### 配置加载

使用 [MqConfigLoader](src/main/java/com/shuai/common/config/MqConfigLoader.java) 从 properties 文件加载配置：

| 方法 | 说明 | 行号 |
|------|------|------|
| [load(String)](src/main/java/com/shuai/common/config/MqConfigLoader.java#L26) | 加载配置文件 |#L26|
| [load(String, Properties)](src/main/java/com/shuai/common/config/MqConfigLoader.java#L46) | 加载并合并默认值 |#L46|
| [getString](src/main/java/com/shuai/common/config/MqConfigLoader.java#L64) | 获取字符串配置 |#L64|
| [getInt](src/main/java/com/shuai/common/config/MqConfigLoader.java#L76) | 获取整型配置 |#L76|
| [getLong](src/main/java/com/shuai/common/config/MqConfigLoader.java#L96) | 获取长整型配置 |#L96|
| [getBoolean](src/main/java/com/shuai/common/config/MqConfigLoader.java#L116) | 获取布尔型配置 |#L116|

使用示例：

```java
// 加载配置文件
Properties props = MqConfigLoader.load("kafka.properties");

// 加载并合并默认值
Properties defaults = new Properties();
defaults.setProperty("bootstrap.servers", "localhost:9092");
Properties merged = MqConfigLoader.load("kafka.properties", defaults);

// 获取配置值（带默认值）
String servers = MqConfigLoader.getString(props, "bootstrap.servers", "localhost:9092");
int port = MqConfigLoader.getInt(props, "port", 9092);
long timeout = MqConfigLoader.getLong(props, "timeout", 30000);
boolean enabled = MqConfigLoader.getBoolean(props, "enabled", true);
```

### 配置文件位置

| 文件 | 用途 |
|------|------|
| [kafka.properties](src/main/resources/kafka.properties) | Kafka 连接配置 |
| [rocketmq.properties](src/main/resources/rocketmq.properties) | RocketMQ 连接配置 |
| [rabbitmq.properties](src/main/resources/rabbitmq.properties) | RabbitMQ 连接配置 |

## 核心概念速查

### Kafka
- **Topic**: 消息主题
- **Partition**: 分区，消息存储和并行消费的基本单位
- **Replica**: 副本，保证消息可靠性
- **Consumer Group**: 消费者组，同组消费者均分消息
- **Offset**: 消息偏移量，记录消费位置

### RocketMQ
- **Topic**: 消息主题
- **Tag**: 消息标签，二级分类
- **MessageQueue**: 消息队列
- **Broker**: 消息服务器
- **NameServer**: 路由协调服务

### RabbitMQ
- **Exchange**: 交换机，消息路由中心
- **Queue**: 队列，存储消息
- **Binding**: 绑定，连接 Exchange 和 Queue
- **Virtual Host**: 虚拟主机，隔离不同环境
- **Channel**: 通道，在连接上建立的逻辑通道

---

**作者**: Shuai
**创建时间**: 2026-01-15
