package io.github.yehongzhi.commodity.direct;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;

public class ReceiveDirectLog2 {
    //交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明一个交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        //声明一个队列
        channel.queueDeclare("disk",false,false,false,null);
        /**
         * 绑定交换机与队列
         */
        channel.queueBind("disk",EXCHANGE_NAME,"error");
        System.out.println("等待接收消息，把消息打印在屏幕上。。。。。");

        //接收消息
        DeliverCallback deliverCallback = (var1, var2)->{
            System.out.println("ReceiveDirectLog2控制台打印接收到消息"+new String(var1.getBytes()));
        };
        //取消消息回调
        channel.basicConsume("disk",true,deliverCallback,consumer->{});
    }
}
