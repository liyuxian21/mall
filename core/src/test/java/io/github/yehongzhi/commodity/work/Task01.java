package io.github.yehongzhi.commodity.work;

import com.rabbitmq.client.Channel;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.Scanner;

public class Task01 {
    public static final String QUEUE_NAME = "hello";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 生成一个队列
         * 1、队列名称
         * 2、队列里面的信息是否持久化，默认情况下消息存储在内存中
         * 3、true 可以多个消费者消费
         * 4、是否自动删除
         * 5、其他参数
         */
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        //从控制台接收消息
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()){
            String message = scanner.next();
            /**
             * 发送消息
             * 1、发送到哪个交换机
             * 2、路由的key值是哪个 本次是队列名称
             * 3、其他参数信息
             * 4、发送消息的消息体
             */
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息完成： "+message);
        }
    }
 }