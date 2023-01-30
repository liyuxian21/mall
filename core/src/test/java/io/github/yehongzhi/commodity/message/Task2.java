package io.github.yehongzhi.commodity.message;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.Scanner;

/**
 * 消息在手动应答时是不丢失的、放回队列重新消费
 */
public class Task2 {
    public static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        //开启发布确认
        channel.confirmSelect();
        /**
         * 生成一个队列
         * 1、队列名称
         * 2、队列里面的信息是否持久化，默认情况下消息存储在内存中
         * 3、true 可以多个消费者消费
         * 4、是否自动删除
         * 5、其他参数
         */
        boolean durable = true; //
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
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
             // 设置生产者发送消息为持久化消息（要求保存到磁盘上）保存在内存中
            channel.basicPublish("",TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
            System.out.println("发送消息完成： "+message);
        }
    }
}
