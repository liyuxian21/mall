package io.github.yehongzhi.commodity.message;

import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;
import io.github.yehongzhi.commodity.SleepUtils;

/**
 * 消息手动应答的消费者
 */
public class Worker04 {
    public static final String TASK_QUEUE_NAME = "ack_queue";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2等待接收消息处理时间较长");
        //申明接收消息
        DeliverCallback deliverCallback = (consumerTag, message) ->{
            //沉睡30s
            SleepUtils.sleep(30);
            System.out.println("接收到的消息： "+new String(message.getBody()));
            //手动应答
            /**
             * 1、消息的标记
             * 2、是否批量处理
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };
        //申明取消消息
        CancelCallback cancelCallback = consumerTag ->{
            System.out.println(consumerTag+"消费者取消消费接口回调逻辑");
        };
        channel.basicQos(5);
        //采用手动应答
        boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);
    }
}
