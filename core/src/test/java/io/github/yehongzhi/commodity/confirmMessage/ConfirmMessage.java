package io.github.yehongzhi.commodity.confirmMessage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 发布确认模式
 * 使用的时间 比较哪种确认方式是最好的
 * 1、单个确认
 * 2、批量确认
 * 3、异步批量确认
 */
public class ConfirmMessage {
    //批量发消息个数
    public static final int MESSAGE_COUNT = 1000;
    public static void main(String[] args) throws Exception {
        //1、单个确认
//        ConfirmMessage.publishMessageIndividually();//发布1000个单独确认消息，耗时232ms
        //2、批量确认
//        ConfirmMessage.publishMessageBatch();//发布1000个批量确认消息，耗时36ms
        //3、异步批量确认
        ConfirmMessage.publishMessageAsync(); //发布1000个异步确认消息，耗时29ms
                                               //发布1000个异步确认消息，耗时36ms
    }

    //单个确认
    public static void publishMessageIndividually() throws Exception {
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();
        //发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //单个消息就马上进行发布确认
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发送成功");
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个单独确认消息，耗时"+(end-begin)+"ms");
    }

    //批量发布确认
    public static void publishMessageBatch() throws Exception {
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();
        //开始时间
        long begin = System.currentTimeMillis();
        //批量确认消息大小
        int batchSize = 100;
        //批量发送消息，批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = i+"";
            channel.basicPublish("",queueName,null,message.getBytes());
            //判断达到100消息时 批量确认一次
            if (i%batchSize == 0){
                //发布确认
                channel.waitForConfirms();
            }
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个批量确认消息，耗时"+(end-begin)+"ms");
    }

    //异步发布确认
    public static void publishMessageAsync() throws Exception {
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        //队列声明
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName, true, false, false, null);
        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的一个哈希表 适用于高并发的情况下
         * 1、轻松的将序号与消息进行关联
         * 2、轻松批量删除条目 只要给到序号
         * 3、支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();

        //开始时间
        long begin = System.currentTimeMillis();
        //消息确认成功回调函数
        ConfirmCallback ackCallback = (deliveryTag,multiple)->{
            if (multiple){ //是否批量
                //2.删除掉已经确认的消息 剩下的就是未确认的消息
                ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag);
                confirmed.clear();
            }else {
                outstandingConfirms.remove(deliveryTag);
            }
            System.out.println("确认的消息"+deliveryTag);
        };
        //消息确认失败回调函数
        ConfirmCallback nackCallback = (deliveryTag,multiple)->{
            //3.打印一下未确认的消息有哪些
            String s = outstandingConfirms.get(deliveryTag);
            System.out.println("未确认消息是："+s+"未确认的消息"+deliveryTag);
        };
        //准备消息的监听器 监听哪些消息成功了 哪些失败了
        /**
         * ackCallback 监听哪些消息成功
         * nackCallback 监听哪些消息失败
         */
        channel.addConfirmListener(ackCallback,nackCallback);//异步通知
        //批量发送消息，批量发布确认
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = "消息"+i;
            channel.basicPublish("",queueName,null,message.getBytes());
            //1、此处记录下所有要发送的消息 消息的总和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }
        //结束时间
        long end = System.currentTimeMillis();
        System.out.println("发布"+MESSAGE_COUNT+"个异步确认消息，耗时"+(end-begin)+"ms");
    }
}
