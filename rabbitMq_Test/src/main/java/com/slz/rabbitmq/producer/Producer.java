package com.slz.rabbitmq.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/17
 */
public class Producer {
    //声明队列名称
    private final static String QUEUE_NAME = "hello";

    //入口函数
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
        //创建通道
        Channel channel = connection.createChannel();
        //
        /**
         * 声明队列
         * 参数1：队列名称
         * 参数2：是否需要持久化消息
         * 参数3：该队列是否只供一个消费者进行消费，是否进行消息共享，默认true共享
         * 参数4：是否自动删除 最后一个消费者断开连接以后，该队列是否自动删除，true为自动删除
         * 参数5：其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //消息内容
        String message = "hello world";
        //发布消息
        /**
         * 参数1：发送到那个交换机
         * 参数2：路由的key，可以用队列名称代替
         * 参数3：其他参数
         * 参数4：消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        //打印消息
        System.out.println("消息发送成功！！！");
    }
}
