package io.github.yehongzhi.commodity.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import io.github.yehongzhi.commodity.RabbitMqUtils;

/**
 * 声明主题交换机 相关队列
 */
public class ReceiveTopicC01 {
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //声明队列
        String queueNme = "Q1";
        channel.queueDeclare(queueNme,false,false,false,null);
        channel.queueBind(queueNme,EXCHANGE_NAME,"*.orange.*");
        System.out.println("等待接收消息.....");

        DeliverCallback deliverCallback = (consumerTag,message)->{
            System.out.println(new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列： "+queueNme+"绑定键: "+message.getEnvelope().getRoutingKey());
        };
        //消费消息
        channel.basicConsume(queueNme,true,deliverCallback,consumer->{});
    }
}
