package com.slz.rabbitmq.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ResourceBundle;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/18
 */
public class RabbitMqUtil {
    private static final String HOST ;
    private static final String USER_NAME;
    private static final String PASSWORD;
    static  {
        ResourceBundle rb = ResourceBundle.getBundle("props");
        HOST = rb.getString("vm_host_ip");
        USER_NAME = rb.getString("user_name");
        PASSWORD = rb.getString("password");
    }
    /**
     * 获取RabbitMQ的连接的Channel
     *
     * @return
     * @throws Exception
     */
    public static Channel getChannel() throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //连接到RabbitMQ主机
        factory.setHost(HOST);
        //设置用户名
        factory.setUsername(USER_NAME);
        //设置密码
        factory.setPassword(PASSWORD);
        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        return connection.createChannel();
    }
}
