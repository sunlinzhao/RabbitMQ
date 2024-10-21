package com.slz.rabbitmq.workqueues;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.slz.rabbitmq.utils.RabbitMqUtil;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/18
 */
public class Consumer1 {
    private final static String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception {
        // 获取信道
        Channel channel = RabbitMqUtil.getChannel();
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println("消费者1已消费信息，消息内容为:" + new String(message.getBody()));
        };
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消费者1取消消费");
        };
        System.out.println("消费者1等待接收消息。。。。。。");
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
