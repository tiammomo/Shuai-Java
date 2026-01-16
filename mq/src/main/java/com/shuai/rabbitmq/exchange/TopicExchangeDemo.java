package com.shuai.rabbitmq.exchange;

/**
 * Topic Exchange 演示
 *
 * 【特性】
 *   - 通配符匹配 routingKey
 *   - * 匹配一个单词
 *   - # 匹配零个或多个单词
 *
 * 【代码示例】
 *   channel.exchangeDeclare("topic-exchange", BuiltinExchangeType.TOPIC, true);
 *   channel.queueBind("all-logs", "topic-exchange", "#");
 *   channel.queueBind("user-logs", "topic-exchange", "user.*");
 *   channel.queueBind("order-logs", "topic-exchange", "order.#");
 *
 *   // 路由规则
 *   // user.* 匹配: user.login, user.logout
 *   // order.# 匹配: order.created, order.paid, order.paid.shipped
 *   // # 匹配所有
 *
 * 【使用场景】
 *   - 日志分类收集
 *   - 灵活路由
 *   - 复杂消息分发
 *
 * @author Shuai
 */
public class TopicExchangeDemo {

    public void demo() {
        MockChannel channel = new MockChannel();

        // 声明 Topic Exchange
        channel.exchangeDeclare("topic-logs", "TOPIC", true);

        // 绑定队列（通配符匹配）
        channel.queueBind("all-logs", "topic-logs", "#");              // 所有日志
        channel.queueBind("user-logs", "topic-logs", "user.*");        // 用户相关
        channel.queueBind("order-logs", "topic-logs", "order.#");       // 订单相关
        channel.queueBind("error-logs", "topic-logs", "*.error");       // 错误日志

        // 发送消息
        channel.basicPublish("topic-logs", "user.login", "用户登录");
        channel.basicPublish("topic-logs", "order.created", "订单创建");
        channel.basicPublish("topic-logs", "payment.error", "支付错误");

        channel.close();
    }

    static class MockChannel {
        public void exchangeDeclare(String name, String type, boolean durable) {
        }

        public void queueBind(String queue, String exchange, String routingKey) {
        }

        public void basicPublish(String exchange, String routingKey, String body) {
        }

        public void close() {
        }
    }
}
