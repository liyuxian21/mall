package io.github.yehongzhi.commodity.deadQueue;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列 实战
 * 消费者1
 */
public class Consumer01 {
    //普通交换机名称
    public static final String EXCHANGE_NAME= "normal_exchange";
    //死信交换机名称
    public static final String EXCHANGE_DEAD_NAME= "dead_exchange";
    //普通队列名称
    public static final String QUEUE_NAME= "normal_queue";
    //死信队列名称
    public static final String QUEUE_DEAD_NAME= "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        //声明普通和死信交换机 类型direct
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(EXCHANGE_DEAD_NAME, BuiltinExchangeType.DIRECT);
        //声明普通队列
        Map<String, Object> arguments = new HashMap<>();
   /*     //过期时间
        arguments.put("x-message-ttl",100000);*/
        //正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange",EXCHANGE_DEAD_NAME);
        //设置死信roudingKey
        arguments.put("x-dead-letter-routing-key","lisi");
       /* //设置普通队列长度限制
        arguments.put("x-max-length",6);*/
        channel.queueDeclare(QUEUE_NAME,false,false,false,arguments);
        //声明死信队列
        channel.queueDeclare(QUEUE_DEAD_NAME,false,false,false,null);
        //队列交换机绑定
        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"zhangsan");//普通
        channel.queueBind(QUEUE_DEAD_NAME,EXCHANGE_DEAD_NAME,"lisi");//死信
        System.out.println("等待接收消息......");
        DeliverCallback deliverCallback = (consumerTag, message)->{
            String msg = new String(message.getBody(),"UTF-8");
            if (msg.equals("info5")){
                System.out.println("Consumer01接收的消息是： "+msg+"：此消息是被C1拒绝的");
                channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
            }else {
                System.out.println("Consumer01接收的消息是: "+msg);
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
            }
//            System.out.println("Consumer01消息接收的内容： "+new String(message.getBody(),"UTF-8"));
        };
        //接收消息 开启手动应答
        channel.basicConsume(QUEUE_NAME,false,deliverCallback,consumer->{});
    }
}
