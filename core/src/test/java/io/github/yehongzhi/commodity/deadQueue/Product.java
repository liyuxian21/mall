package io.github.yehongzhi.commodity.deadQueue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * 死信队列 生产者
 */
public class Product {
    //普通交换机名称
    public static final String EXCHANGE_NAME = "normal_exchange";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();
        //延迟消息或者叫死信消息
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            channel.basicPublish(EXCHANGE_NAME, "zhangsan", null, message.getBytes());
        }
    }

}
