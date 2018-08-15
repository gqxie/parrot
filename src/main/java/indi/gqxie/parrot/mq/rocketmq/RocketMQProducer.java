package indi.gqxie.parrot.mq.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class RocketMQProducer
{
    private static DefaultMQProducer defaultMQProducer;
    /**
     * 生产者的组名
     */
    private static String            producerGroup;

    /**
     * NameServer 地址
     */
    private static String namesrvAddr;

    @Value("${apache.rocketmq.producer.producerGroup}")
    public void setProducerGroup(String group)
    {
        producerGroup = group;
    }

    @Value("${apache.rocketmq.namesrvAddr}")
    public void setNamesrvAddr(String address)
    {
        namesrvAddr = address;
    }

    @PostConstruct
    private static void init()
    {
        log.info("start init RocketMQ,producerGroup={},namesrvAddr={}");
        //生产者的组名
        defaultMQProducer = new DefaultMQProducer(producerGroup);
        //指定NameServer地址，多个地址以 ; 隔开
        defaultMQProducer.setNamesrvAddr(namesrvAddr);
        try
        {
            /**
             * Producer对象在使用之前必须要调用start初始化，初始化一次即可
             * 注意：切记不可以在每次发送消息时，都调用start方法
             */
            defaultMQProducer.start();
            log.info("defaultMQProducer start...");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public SendResult sendMessage(Message message)
    {
        SendResult result = new SendResult();
        try
        {
            return defaultMQProducer.send(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }
}
