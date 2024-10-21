package com.slz.rabbitmq.workqueues;

import com.rabbitmq.client.Channel;
import com.slz.rabbitmq.utils.RabbitMqUtil;

import java.util.Scanner;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/18
 */
public class Producer {
    private static final String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception {
        //获取通道
        Channel channel = RabbitMqUtil.getChannel();
        //生成队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        //从控制台发送消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String message = scanner.next();
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完成：" + message);
        }
    }
}
