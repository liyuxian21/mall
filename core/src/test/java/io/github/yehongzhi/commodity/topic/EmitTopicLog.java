package io.github.yehongzhi.commodity.topic;

import com.rabbitmq.client.Channel;
import io.github.yehongzhi.commodity.RabbitMqUtils;

import java.util.HashMap;
import java.util.Map;

public class EmitTopicLog {
    /**
     * 声明主题交换机 相关队列
     */
    public static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        Map<String,String> bindingKeyMap = new HashMap<>();
        bindingKeyMap.put("quick.orange.rabbit","被队列Q1Q2接收到");
        bindingKeyMap.put("lazy.orange.elephant","被队列Q1Q2接收到");
        bindingKeyMap.put("quick.orange.fox","被队列Q1接收到");
        bindingKeyMap.put("lazy.brown.fox","被队列Q2接收到");
        bindingKeyMap.put("lazy.pink.rabbit","虽然满足两个绑定，只被队列Q2接收一次");
        bindingKeyMap.put("quick.brown.fox","不匹配任何绑定会被丢弃");
        bindingKeyMap.put("quick.orange.male.rabbit","是四个单词不匹配任何绑定会被丢弃");
        bindingKeyMap.put("lazy.orange.male.rabbit","是四个单词只匹配Q2");
        for (Map.Entry<String, String> bindingStringEntry : bindingKeyMap.entrySet()) {
            String key = bindingStringEntry.getKey();
            String message = bindingStringEntry.getValue();
            channel.basicPublish(EXCHANGE_NAME,key,null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息： "+message);
        }
    }
}
