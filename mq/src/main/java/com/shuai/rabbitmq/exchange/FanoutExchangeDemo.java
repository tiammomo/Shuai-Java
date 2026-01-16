package com.shuai.rabbitmq.exchange;

/**
 * Fanout Exchange 演示
 *
 * 【特性】
 *   - 忽略 routingKey，广播到所有绑定的队列
 *   - 最简单的发布/订阅模式
 *
 * 【代码示例】
 *   channel.exchangeDeclare("fanout-exchange", BuiltinExchangeType.FANOUT, true);
 *   channel.queueBind("queue-1", "fanout-exchange", "");
 *   channel.queueBind("queue-2", "fanout-exchange", "");
 *   channel.queueBind("queue-3", "fanout-exchange", "");
 *
 *   // 发送
 *   channel.basicPublish("fanout-exchange", "", null, broadcastMsg);
 *
 * 【使用场景】
 *   - 广播通知
 *   - 实时消息推送
 *   - 多终端同步
 *
 * @author Shuai
 */
public class FanoutExchangeDemo {

    public void demo() {
        MockChannel channel = new MockChannel();

        // 声明 Fanout Exchange
        channel.exchangeDeclare("broadcast", "FANOUT", true);

        // 绑定多个队列
        channel.queueBind("subscriber-1", "broadcast", "");
        channel.queueBind("subscriber-2", "broadcast", "");
        channel.queueBind("subscriber-3", "broadcast", "");

        // 广播消息
        channel.basicPublish("broadcast", "", "广播消息");

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
