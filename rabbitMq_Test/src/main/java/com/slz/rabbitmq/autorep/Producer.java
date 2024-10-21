package com.slz.rabbitmq.autorep;

import com.rabbitmq.client.Channel;
import com.slz.rabbitmq.utils.RabbitMqUtil;

import java.util.Scanner;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/19
 */
public class Producer {
    private static final String QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完成：" + message);
        }
    }
}
