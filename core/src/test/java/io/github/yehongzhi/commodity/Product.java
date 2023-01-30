package io.github.yehongzhi.commodity;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
public class Product {
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
        /**
         * 生成一个队列
         * 1、队列名称
         * 2、队列里面的信息是否持久化，默认情况下消息存储在内存中
         * 3、true 可以多个消费者消费
         * 4、是否自动删除
         * 5、其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
         //发消息
        String message = "hello world";
        /**
         * 发送消息
         * 1、发送到哪个交换机
         * 2、路由的key值是哪个 本次是队列名称
         * 3、其他参数信息
         * 4、发送消息的消息体
         */
        channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
        System.out.println("消息发送完毕");
    }
}
