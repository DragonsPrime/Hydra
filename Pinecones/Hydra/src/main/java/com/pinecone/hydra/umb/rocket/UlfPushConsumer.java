package com.pinecone.hydra.umb.rocket;

import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;
import com.pinecone.hydra.umb.broadcast.PushConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

public class UlfPushConsumer extends ArchMQConsumer implements PushConsumer {

    protected MQPushConsumer  wrappedConsumer;

    protected RocketClient    mRocketClient;

    public UlfPushConsumer( RocketClient client, String topic, String tag ) {
        super(
                client.getRocketConfig().getNameServerAddr(),
                client.getRocketConfig().getGroupName(),
                topic, tag
        );
        this.mRocketClient = client;
    }

    public MQPushConsumer newMQPushConsumer( UlfPackageMessageHandler handler ) throws UMBServiceException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer( this.mszGroupName );
        consumer.setNamesrvAddr( this.mszNameServerAddr );

        try {
            consumer.subscribe( this.mszTopic, this.mszTag );
            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage( List<MessageExt> msgs, ConsumeConcurrentlyContext context ) {
                    for ( MessageExt msg : msgs ) {
                        try{
                            handler.onSuccessfulMsgReceived( msg.getBody(), new Object[] { msg, msgs, context } );
                        }
                        catch ( Exception e ) {
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            //consumer.start();
        }
        catch ( MQClientException e ) {
            throw new UMBServiceException( e );
        }
        return consumer;
    }

    @Override
    public void start( UlfPackageMessageHandler handler ) throws UMBServiceException {
        MQPushConsumer consumer = this.newMQPushConsumer( handler );

        if ( this.wrappedConsumer == null ) {
            this.wrappedConsumer = consumer;
        }

        try{
            consumer.start();
        }
        catch ( MQClientException e ) {
            throw new UMBServiceException( e );
        }
    }

    @Override
    public void close() {
        if ( this.wrappedConsumer != null ) {
            this.wrappedConsumer.shutdown();
            this.mRocketClient.deregister( this );
            this.wrappedConsumer = null;
        }
    }

}
