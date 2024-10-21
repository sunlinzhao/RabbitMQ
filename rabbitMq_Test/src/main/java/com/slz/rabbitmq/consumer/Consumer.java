package com.slz.rabbitmq.consumer;

import com.rabbitmq.client.*;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/17
 */
public class Consumer {
    //声明队列名称
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //连接到RabbitMQ主机
        factory.setHost("172.27.20.54");
        //设置用户名
        factory.setUsername("admin");
        //设置密码
        factory.setPassword("admin");
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();

        //消费回调
        DeliverCallback deliverCallback = (consumerTag, message) -> {
            System.out.println(new String(message.getBody()));
            System.out.println(message);
        };
        //取消消费回调
        CancelCallback cancelCallback = (consumerTag) -> {
            System.out.println("消费者取消消费");
        };
        /**
         * 消费者消费消息
         * 参数1：消费那个队列
         * 参数2：消费成功之后是否要自动应答，true代表自动应答，false手动应答
         * 参数3：消费送达的回调
         * 参数4：消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, cancelCallback);
    }
}
