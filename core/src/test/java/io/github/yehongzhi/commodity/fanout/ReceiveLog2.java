package io.github.yehongzhi.commodity.fanout;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;

public class ReceiveLog2 {
    public static final String EXCAHNGE_NAME = "logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCAHNGE_NAME,"fanout");
        //声明一个队列 临时队列
        /**
         * 生成一个临时队列 队列的名称是随机的
         * 当消费者断开与队列的连接的时候 队列将自动删除
         */
        String queueName = channel.queueDeclare().getQueue();
        /**
         * 绑定交换机与队列
         */
        channel.queueBind(queueName,EXCAHNGE_NAME,"");
        System.out.println("等待接收消息，把消息打印在屏幕上。。。。。");

        //接收消息
        DeliverCallback deliverCallback = (var1, var2)->{
            System.out.println("ReceiveLog2控制台打印接收到消息"+new String(var1.getBytes()));
        };
        //取消消息回调
        channel.basicConsume(queueName,true,deliverCallback,consumer->{});
    }
}
