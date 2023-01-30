package io.github.yehongzhi.commodity.deadQueue;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;

public class Consumer02 {
    //死信队列名称
    public static final String QUEUE_DEAD_NAME= "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("等待接收消息......");
        DeliverCallback deliverCallback = (consumerTag, message)->{
            System.out.println("Consumer02消息接收的内容： "+new String(message.getBody(),"UTF-8"));
        };
        //接收消息
        channel.basicConsume(QUEUE_DEAD_NAME,true,deliverCallback,consumer->{});
    }
}
