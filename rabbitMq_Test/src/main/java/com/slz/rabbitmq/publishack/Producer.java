package com.slz.rabbitmq.publishack;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.slz.rabbitmq.utils.RabbitMqUtil;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author : SunLZ
 * @project : RabbitMQ
 * @date : 2024/10/19
 */
public class Producer {
    //批量发送消息的数量
    private static final int MESSAGE_COUNT = 1000;
    private static final String INDIVIDUAL_CONFIRM_QUEUE = "individual_confirm_queue";
    private static final String BATCH_CONFIRM_QUEUE = "batch_confirm_queue";
    private static final String ASYNC_CONFIRM_QUEUE = "async_confirm_queue";
    private static final String ASYNC_CONCURRENT_CONFIRM_QUEUE = "async_concurrent_confirm_queue";

    public static void main(String[] args) throws Exception {
        confirmMessageIndividually();
    }

    public static void confirmMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明队列
        channel.queueDeclare(INDIVIDUAL_CONFIRM_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", INDIVIDUAL_CONFIRM_QUEUE, null, message.getBytes());
            //
            boolean flag = channel.waitForConfirms();
            if (flag) {
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) +
                "ms");
    }
    public static void batchConfirmMessage() throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明队列
        channel.queueDeclare(BATCH_CONFIRM_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();

        //批量确认数量大小
        int batchSize = 100;
        //未确认消息数量
        int waitConfirmCount = 0;
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", BATCH_CONFIRM_QUEUE, null, message.getBytes());
            waitConfirmCount++;
            if (waitConfirmCount == batchSize) {
                //确认消息
                channel.waitForConfirms();
                waitConfirmCount = 0;
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - begin) +
                "ms");
    }
    public static void asyncConfirmMessage() throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明队列
        channel.queueDeclare(ASYNC_CONFIRM_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();
        //成功的监听器
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            System.out.println("确认的消息：" + deliveryTag);
        };
        //失败的监听器
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            System.out.println("未确认的消息：" + deliveryTag);
        };
        /**
         * 确认发布监听器
         * 参数1：监听成功的消息
         * 参数2：监听失败的消息
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            channel.basicPublish("", ASYNC_CONFIRM_QUEUE, null, message.getBytes());
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - begin) +
                "ms");
    }
    /**
     * 异步并发问题
     *
     * @throws Exception
     */
    public static void asyncConcurrentConfirmMessage() throws Exception {
        Channel channel = RabbitMqUtil.getChannel();
        //声明队列
        channel.queueDeclare(ASYNC_CONCURRENT_CONFIRM_QUEUE, false, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        /**
         * 1. 安全有序的Hash表，适用于高并发场景
         * 优点：关联序号和消息
         *      支持批量操作
         *      支持高并发
         */
        ConcurrentSkipListMap<Long, String> concurrentSkipListMap = new ConcurrentSkipListMap();

        //成功的监听器
        ConfirmCallback ackCallback = (deliveryTag, multiple) -> {
            //批量消息处理
            //3. 删除已确认的消息
            if (multiple) {
                //返回消息序列号小于等于当前序列号的消息
                ConcurrentNavigableMap<Long, String> confirmed =
                        concurrentSkipListMap.headMap(deliveryTag, true);
                confirmed.clear();
            } else {
                //只删除当前消息
                concurrentSkipListMap.remove(deliveryTag);
            }
            System.out.println("确认的消息：" + deliveryTag);
        };
        //失败的监听器
        ConfirmCallback nackCallback = (deliveryTag, multiple) -> {
            //4. 保留未确认的消息并打印
            String message = concurrentSkipListMap.get(deliveryTag);
            System.out.println("未确认的消息tag：" + deliveryTag + ",消息内容为：" + message);
        };
        /**
         * 确认发布监听器
         * 参数1：监听成功的消息
         * 参数2：监听失败的消息
         */
        channel.addConfirmListener(ackCallback, nackCallback);
        //开始时间
        long begin = System.currentTimeMillis();
        //循环发送消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i + "";
            /**
             * 2.发消息之前将消息放入容器
             * channel.getNextPublishSeqNo() 是获取下一个消息的序列号
             */
            concurrentSkipListMap.put(channel.getNextPublishSeqNo(), message);
            //发送消息
            channel.basicPublish("", ASYNC_CONCURRENT_CONFIRM_QUEUE, null, message.getBytes());
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息,耗时" + (end - begin) +
                "ms");
    }
}
