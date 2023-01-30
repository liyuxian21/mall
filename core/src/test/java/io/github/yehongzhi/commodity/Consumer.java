package io.github.yehongzhi.commodity;

import com.rabbitmq.client.*;

/**
 * 消费者 接收消息
 */
public class Consumer {
    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("guest");
        factory.setPassword("guest");
        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        //申明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            System.out.println(new String(message.getBody()));
        };
        //申明取消消息
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println("消费消息被中断");
        };
        /**
         * 1、消费哪个队列
         * 2、消费成功之后 是否自动应答
         * 3、消费未成功消息的回调
         * 4、消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
