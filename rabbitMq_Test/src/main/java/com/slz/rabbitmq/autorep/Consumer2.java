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
public class Consumer2 {
    private static final String  QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();

        DeliverCallback deliverCallback = (consumerTag, message) -> {
            ThreadUtil.sleep(20);
            System.out.println("消费者2接收到消息为：" + new String(message.getBody()));
            //手动应答
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        };
        CancelCallback cancelCallback = (String consumerTag) -> {
            System.out.println("消费者2取消消费消息");
        };
        System.out.println("消费者2等待消费消息");
        boolean autoAck = false;
        channel.basicConsume(QUEUE_NAME, autoAck, deliverCallback, cancelCallback);
    }
}
