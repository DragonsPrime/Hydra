package com.pinecone.hydra.umq;

import com.pinecone.framework.system.prototype.Pinenut;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.client.producer.DefaultMQProducer;

public class RocketMQClient implements Pinenut {

    protected String pNameSrvAddr;

    protected Integer maxMessageSize ;

    protected Integer sendMsgTimeout;

    protected Integer retryTimesWhenSendFailed;

    protected String  groupName;

    protected DefaultMQProducer mqProducer;

    public RocketMQClient( String pNameSrvAddr, String groupName ){
        this.pNameSrvAddr = pNameSrvAddr; // 生产者的nameService地址
        this.groupName = groupName;       // 消息生产者组名，一般一个应用的消息生产者应将其归为同一个消息生产组
        this.maxMessageSize = 4096;       // 消息最大大小，默认4KB
        this.sendMsgTimeout = 30000;      // 消息发送超时时间，默认30秒
        this.retryTimesWhenSendFailed = 2;// 消息发送失败重试次数，默认为2次
        this.mqProducer = this.getDefaultMQProducer();
    }

    /**
     * 发送消息
     *
     * @param topic 消息主题
     * @param tags  消息标签
     * @param keys  消息键
     * @param body  消息体
     * @return 发送结果
     * @throws MQClientException 如果发送失败
     */
    public boolean sendMessage(String topic, String tags, String keys, byte[] body) throws MQClientException {
        Message msg = new Message(topic, tags, keys, body);
        try {
            mqProducer.send(msg);
            return true;
        } catch (Exception e) {
            System.out.println("Failed to send message: " + e.getMessage());
            return false;
        }
    }

    /**
     * 关闭生产者
     */
    public void shutdown() {
        if (mqProducer != null) {
            mqProducer.shutdown();
        }
    }

    private DefaultMQProducer getDefaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.pNameSrvAddr);
        producer.setMaxMessageSize(this.maxMessageSize);
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        try {
            producer.start();
        } catch (MQClientException e) {
            System.out.println(e.getErrorMessage());
        }
        return producer;
    }

}
