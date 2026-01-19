# Observability 可观测性模块

> 整合 Prometheus + Sentinel 两大核心可观测性组件，提供完整的指标监控和流量控制能力。

## 学习文档

本模块配套学习文档位于 [learn_docs](learn_docs/README.md) 目录：

### 可观测性

| 主题 | 文档链接 |
|------|----------|
| [Prometheus](#prometheus-指标监控) | [learn_docs/prometheus/](learn_docs/prometheus/README.md) |
| [Sentinel](#sentinel-流量控制) | [learn_docs/sentinel/](learn_docs/sentinel/README.md) |
| [Micrometer](#micrometer-指标抽象) | [learn_docs/micrometer/](learn_docs/micrometer/README.md) |
| [Grafana](#grafana-可视化) | [learn_docs/grafana/](learn_docs/grafana/README.md) |

> **提示**: 点击主题名称可跳转到下方对应章节。

## 模块结构

```
observability/
├── pom.xml                                    # Maven 依赖配置
├── README.md                                  # 本文档
├── learn_docs/                                # 学习文档目录
│   ├── README.md                              # 学习文档入口
│   ├── prometheus/                            # Prometheus 学习文档
│   ├── sentinel/                              # Sentinel 学习文档
│   ├── micrometer/                            # Micrometer 学习文档
│   └── grafana/                               # Grafana 学习文档
├── src/main/java/com/shuai/observability/
│   ├── ObservabilityDemo.java                 # 模块入口类
│   ├── prometheus/
│   │   ├── PrometheusDemo.java                # Prometheus 指标监控演示
│   │   └── PrometheusMetrics.java             # 自定义指标工具类
│   ├── sentinel/
│   │   ├── SentinelDemo.java                  # Sentinel 流量控制演示
│   │   ├── SentinelDynamicRulesDemo.java      # Sentinel 动态规则演示
│   │   ├── FlowRuleConfig.java                # 流控规则配置
│   │   └── DegradeRuleConfig.java             # 熔断规则配置
│   └── controller/
│       └── PrometheusController.java          # Spring Boot REST 端点
└── src/main/resources/
    └── application.yml                        # Spring Boot 配置文件
```

## 技术栈

| 组件 | 版本 | 用途 |
|------|------|------|
| Micrometer | 1.12.2 | 指标抽象层 |
| Prometheus | 2.51.0 | 指标存储与查询 |
| Sentinel | 1.8.6 | 流量控制与熔断 |
| Spring Boot Actuator | 3.5.3 | 健康检查与指标暴露 |

## 快速开始

### 1. 编译运行

```bash
cd /home/ubuntu/learn_projects/Shuai-Java/observability

# 编译
mvn clean compile

# 运行演示
mvn exec:java -Dexec.mainClass="com.shuai.observability.ObservabilityDemo"
```

### 2. Docker 服务启动

启动完整的可观测性基础设施：

```bash
cd /home/ubuntu/learn_projects/Shuai-Java/observability/docker

# 启动所有服务
docker-compose up -d

# 或只启动可观测性相关服务
docker-compose up -d prometheus grafana sentinel-dashboard
```

### 3. 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| Prometheus | http://localhost:9090 | 指标查询 |
| Grafana | http://localhost:3000 | 可视化面板 |
| Sentinel Dashboard | http://localhost:8081 | 流量控制台 |

默认账号：`sentinel / sentinel`

---

## Prometheus 指标监控

### 核心指标类型

| 类型 | 说明 | 适用场景 |
|------|------|----------|
| **Counter** | 递增计数器 | 请求总数、错误计数 |
| **Gauge** | 当前值仪表盘 | 活跃连接数、内存使用 |
| **Histogram** | 直方图分布 | 响应时间分布、请求大小 |
| **Summary** | 摘要分位数 | 自定义分位数统计 |

### 常用指标示例

```java
// Counter: 计数器
Counter requests = Counter.builder("http_requests_total")
    .description("HTTP 请求总数")
    .tag("method", "GET")
    .register(registry);
requests.increment();

// Gauge: 仪表盘
Gauge.builder("active_users")
    .description("当前活跃用户数")
    .register(registry)
    .set(userCount.get());

// Timer: 计时器
Timer timer = Timer.builder("http_request_duration")
    .description("HTTP 请求延迟")
    .register(registry);
timer.record(duration, TimeUnit.MILLISECONDS);

// Histogram: 直方图
DistributionSummary responseSize = DistributionSummary.builder("response_size")
    .description("响应大小分布")
    .register(registry);
responseSize.record(size);
```

### 暴露指标端点

```bash
# 获取 Prometheus 格式指标
curl http://localhost:8080/actuator/prometheus

# 查看健康状态
curl http://localhost:8080/actuator/health
```

---

## Sentinel 流量控制

### 流控策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| **直接拒绝** | 超出阈值直接拒绝 | 保护核心接口 |
| **预热启动** | 逐步提升阈值 | 防止冷启动冲击 |
| **匀速排队** | 平滑请求速率 | 削峰填谷 |

### 熔断策略

| 策略 | 说明 | 适用场景 |
|------|------|----------|
| **慢调用比例** | 响应时间过长触发熔断 | 后端服务变慢 |
| **异常比例** | 错误率过高触发熔断 | 服务不稳定 |
| **异常数** | 异常次数触发熔断 | 瞬时故障 |

### 代码示例

```java
// 定义流控规则
FlowRule rule = new FlowRule("userResource");
rule.setCount(10);                    // QPS 阈值
rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
rule.setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_WARM_UP);
FlowRuleManager.loadRules(Collections.singletonList(rule));

// 使用 @SentinelResource 注解
@SentinelResource(
    value = "getUser",
    blockHandler = "handleBlock",
    fallback = "handleFallback"
)
public User getUser(Long id) {
    return userService.getById(id);
}

// 熔断降级
CircuitBreakerRule breaker = CircuitBreakerRule.builder()
    .resource("userService")
    .grade(CircuitBreakerRule.SLOW_REQUEST_RATIO)
    .count(0.5)           // 50% 慢调用比例
    .timeWindow(10)       // 10秒熔断窗口
    .build();
```

---

## Docker 服务配置

### docker-compose.yml

```yaml
services:
  prometheus:
    image: prom/prometheus:v2.51.0
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml

  grafana:
    image: grafana/grafana:11.0.0
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin

  sentinel-dashboard:
    image: sentinel-docker/sentinel-dashboard:1.8.6
    ports:
      - "8081:8081"
    environment:
      - SPRING_APPLICATION_NAME=sentinel-dashboard
```

### Prometheus 配置

```yaml
# prometheus/prometheus.yml
scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']

  - job_name: 'spring-boot'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

---

## 最佳实践

### 1. 指标命名规范

```yaml
# 推荐命名方式
http_requests_total          # 使用 _total 后缀表示计数器
http_request_duration_seconds  # 使用 base_unit (seconds)
jvm_memory_heap_used_bytes    # 包含单位

# 不推荐
http_request_count
requestTimer
mem
```

### 2. 标签使用

```java
// 按业务维度打标签
Counter.builder("api_requests")
    .tag("endpoint", "/api/users")
    .tag("method", "GET")
    .tag("status", "200")
    .register(registry);

// 避免高基数标签
// 不推荐：user_id、ip_address
// 推荐：user_type、region
```

### 3. 熔断配置建议

```java
// 慢调用比例
CircuitBreakerRule.builder()
    .grade(SLOW_REQUEST_RATIO)
    .count(0.5)              // 50% 慢调用阈值
    .timeWindow(10)          // 10秒熔断窗口
    .minRequestAmount(5)     // 最小请求数
    .build();

// 异常比例
CircuitBreakerRule.builder()
    .grade(EXCEPTION_RATIO)
    .count(0.3)              // 30% 异常阈值
    .timeWindow(10)
    .build();
```

---

## 常见问题

### Q: 指标不生效？

检查 `application.yml` 中的 `management.endpoints.web.exposure.include` 是否包含 `prometheus`。

### Q: 熔断器不触发？

确保 `minRequestAmount` 设置合理，通常设置为最小统计请求数。

### Q: Grafana 无法连接 Prometheus？

检查 Prometheus 的 `scrape_configs` 配置，确保 `targets` 地址正确。

---

## 相关资源

- [Micrometer 文档](https://micrometer.io/docs/ref/prometheus)
- [Sentinel 官方文档](https://sentinelguard.io/zh-cn/docs/)
- [Prometheus 查询语言](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [Grafana 仪表盘](https://grafana.com/docs/grafana/latest/dashboards/)
