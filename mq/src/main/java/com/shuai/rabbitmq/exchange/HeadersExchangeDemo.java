package com.shuai.rabbitmq.exchange;

/**
 * Headers Exchange 演示
 *
 * 【特性】
 *   - 基于消息头属性匹配
 *   - x-match=all (AND) 或 x-match=any (OR)
 *
 * 【代码示例】
 *   channel.exchangeDeclare("headers-exchange", BuiltinExchangeType.HEADERS, true);
 *
 *   Map<String, Object> args = new HashMap<>();
 *   args.put("x-match", "all");  // 所有条件匹配
 *   args.put("content-type", "application/json");
 *   args.put("priority", 5);
 *   channel.queueBind("json-high-queue", "headers-exchange", "", args);
 *
 *   // 发送消息（消息头包含属性）
 *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
 *       .headers(Map.of("content-type", "application/json", "priority", 5))
 *       .build();
 *   channel.basicPublish("headers-exchange", "", props, body);
 *
 * 【使用场景】
 *   - 复杂条件匹配
 *   - 多属性路由
 *   - 内容类型路由
 *
 * @author Shuai
 */
public class HeadersExchangeDemo {

    public void demo() {
        MockChannel channel = new MockChannel();

        // 声明 Headers Exchange
        channel.exchangeDeclare("headers-exchange", "HEADERS", true);

        // AND 匹配
        channel.queueBindWithHeaders("json-queue", "headers-exchange", "all", "application/json");
        channel.queueBindWithHeaders("priority-queue", "headers-exchange", "all", "priority:5");

        // OR 匹配
        channel.queueBindWithHeaders("any-queue", "headers-exchange", "any", "type:urgent");

        // 发送消息
        channel.basicPublishWithHeaders("application/json", "{\"name\":\"test\"}");

        channel.close();
    }

    static class MockChannel {
        public void exchangeDeclare(String name, String type, boolean durable) {
        }

        public void queueBind(String queue, String exchange, String routingKey) {
        }

        public void queueBindWithHeaders(String queue, String exchange, String matchType, String headers) {
            /*
             * [RabbitMQ] Headers 绑定
             *   Map<String, Object> args = new HashMap<>();
             *   args.put("x-match", "all");  // 或 "any"
             *   args.put("content-type", "application/json");
             *   channel.queueBind(queue, exchange, "", args);
             */
        }

        public void basicPublishWithHeaders(String header, String body) {
            /*
             * [RabbitMQ] 发送 Headers 消息
             *   AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
             *       .headers(Map.of("content-type", header))
             *       .build();
             *   channel.basicPublish("headers-exchange", "", props, body.getBytes());
             */
        }

        public void close() {
        }
    }
}
