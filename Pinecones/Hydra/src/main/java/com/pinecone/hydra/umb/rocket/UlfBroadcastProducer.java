package com.pinecone.hydra.umb.rocket;

import com.pinecone.hydra.umb.UMBClientException;
import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;
import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.function.Supplier;

public class UlfBroadcastProducer implements BroadcastProducer {
    protected String mszNameServerAddr;

    protected String mszGroupName;

    protected int mnMaxMessageSize ;

    protected int mnSendMsgTimeout;

    protected int mnRetryTimesWhenSendFailed;

    protected MQProducer mWrappedProducer;

    protected RocketClient mRocketClient;


    public UlfBroadcastProducer( RocketClient client, Supplier<DefaultMQProducer> producerSupplier ) {
        this.mRocketClient = client;
        RocketConfig config = client.getRocketConfig();
        this.mszNameServerAddr          = config.getNameServerAddr();
        this.mszGroupName               = config.getGroupName();
        this.mnMaxMessageSize           = config.getMaxMessageSize();
        this.mnSendMsgTimeout           = config.getSendMsgTimeout();
        this.mnRetryTimesWhenSendFailed = config.getRetryTimesWhenSendFailed();

        DefaultMQProducer producer = producerSupplier.get();
        producer.setProducerGroup(this.mszGroupName);
        producer.setNamesrvAddr(this.mszNameServerAddr);
        producer.setMaxMessageSize(this.mnMaxMessageSize);
        producer.setSendMsgTimeout(this.mnSendMsgTimeout);
        producer.setRetryTimesWhenSendFailed(this.mnRetryTimesWhenSendFailed);
        this.mWrappedProducer = producer;
    }

    public UlfBroadcastProducer( RocketClient client ) {
        this( client, DefaultMQProducer::new );
    }


    @Override
    public void sendMessage( String topic, String ns, String name, byte[] body ) throws UMBClientException {
        Message msg = new Message( topic, ns, name, body );
        try {
            this.mWrappedProducer.send( msg );
        }
        catch ( MQClientException | RemotingException | MQBrokerException | InterruptedException e ) {
            throw new UMBClientException( e );
        }
    }

    @Override
    public void sendMessage( String topic, byte[] body ) throws UMBClientException {
        this.sendMessage( topic, "", BroadcastNode.DefaultEntityName, body );
    }

    @Override
    public void sendMessage( UNT unt, String name, byte[] body ) throws UMBClientException {
        this.sendMessage( unt.getTopic(), unt.getNamespace(), name, body );
    }

    @Override
    public void close() {
        this.mWrappedProducer.shutdown();
        this.mRocketClient.deregister( this );
    }

    @Override
    public void start() throws UMBServiceException {
        try{
            this.mWrappedProducer.start();
        }
        catch ( MQClientException e ) {
            throw new UMBServiceException( e );
        }
    }
}
