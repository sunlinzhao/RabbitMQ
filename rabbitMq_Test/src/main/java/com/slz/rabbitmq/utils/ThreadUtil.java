package com.slz.rabbitmq.utils;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/19
 */
public class ThreadUtil {
    public static void sleep(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
