package com.pinecone.hydra.umb.rocket;

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
        this.pNameSrvAddr = pNameSrvAddr;
        this.groupName = groupName;
        this.maxMessageSize = 4096;
        this.sendMsgTimeout = 30000;
        this.retryTimesWhenSendFailed = 2;
        this.mqProducer = this.getDefaultMQProducer();
    }

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
