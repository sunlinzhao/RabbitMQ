package com.slz.rabbitmq.autorep;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.slz.rabbitmq.utils.RabbitMqUtil;
import com.slz.rabbitmq.utils.ThreadUtil;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/19
 */
public class Consumer1 {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            ThreadUtil.sleep(2);
            System.out.println("消费者1接收到消息为：" + new String(message.getBody()));
            /**手动应答
             *  参数1：消息标记 tag（应答是哪个消息）
             *  参数2：是否批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = (String consumerTag) -> {
            System.out.println("消费者1取消消费消息");
        };
        System.out.println("消费者1等待消费消息");
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
