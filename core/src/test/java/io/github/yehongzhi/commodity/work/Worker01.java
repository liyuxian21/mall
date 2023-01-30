package io.github.yehongzhi.commodity.work;

import com.rabbitmq.client.*;
import io.github.yehongzhi.commodity.RabbitMqUtils;

public class Worker01 {
    public static final String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //申明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println("接收到的消息： "+new String(message.getBody()));
        };
        //申明取消消息
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag+"消费消息被中断");
        };
        /**
         * 1、消费哪个队列
         * 2、消费成功之后 是否自动应答
         * 3、消费未成功消息的回调
         * 4、消费者取消消费的回调
         */
        System.out.println("C2等待接收消息......  ");
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
