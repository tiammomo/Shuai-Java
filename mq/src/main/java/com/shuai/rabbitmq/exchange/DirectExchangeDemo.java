package com.shuai.rabbitmq.exchange;

/**
 * Direct Exchange 演示
 *
 * 【特性】
 *   - 精确匹配 routingKey 和 bindingKey
 *   - routingKey 完全相等才路由到队列
 *
 * 【代码示例】
 *   channel.exchangeDeclare("direct-exchange", BuiltinExchangeType.DIRECT, true);
 *   channel.queueBind("error-queue", "direct-exchange", "error");
 *   channel.queueBind("warning-queue", "direct-exchange", "warning");
 *   channel.queueBind("info-queue", "direct-exchange", "info");
 *
 *   // 发送
 *   channel.basicPublish("direct-exchange", "error", null, errorMsg);
 *
 * 【使用场景】
 *   - 日志分级处理（error/warning/info）
 *   - 任务类型路由
 *   - 精确消息分发
 *
 * @author Shuai
 */
public class DirectExchangeDemo {

    public void demo() {
        MockChannel channel = new MockChannel();

        // 声明 Direct Exchange
        channel.exchangeDeclare("direct-logs", "DIRECT", true);

        // 绑定队列
        channel.queueBind("error-queue", "direct-logs", "error");
        channel.queueBind("warning-queue", "direct-logs", "warning");
        channel.queueBind("info-queue", "direct-logs", "info");

        // 发送消息
        channel.basicPublish("direct-logs", "error", "错误日志");

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
