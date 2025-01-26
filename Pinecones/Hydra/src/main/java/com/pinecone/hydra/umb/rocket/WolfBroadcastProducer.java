package com.pinecone.hydra.umb.rocket;

import com.pinecone.framework.system.Nullable;
import com.pinecone.hydra.umb.UMBClientException;
import com.pinecone.hydra.umb.UMCPackageMessageEncoder;
import com.pinecone.hydra.umb.UlfPackageMessageEncoder;
import com.pinecone.hydra.umb.broadcast.BroadcastNode;
import com.pinecone.hydra.umb.broadcast.UMCBroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.AbstractUMCHead;
import com.pinecone.hydra.umc.msg.UMCMessage;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;
import com.pinecone.hydra.umc.msg.extra.GenericExtraHeadCoder;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;
import java.util.function.Supplier;

public class WolfBroadcastProducer extends UlfBroadcastProducer implements UMCBroadcastProducer {
    protected UMCPackageMessageEncoder mUMCPackageMessageEncoder;

    protected ExtraHeadCoder           mExtraHeadCoder;

    public WolfBroadcastProducer( UlfRocketClient client, Supplier<DefaultMQProducer> producerSupplier, @Nullable ExtraHeadCoder extraHeadCoder ) {
        super( client, producerSupplier );

        this.mExtraHeadCoder           = extraHeadCoder;
        if ( this.mExtraHeadCoder == null ) {
            this.mExtraHeadCoder = client.getExtraHeadCoder();
        }

        this.mUMCPackageMessageEncoder = new UlfPackageMessageEncoder( this.mExtraHeadCoder );
    }

    public WolfBroadcastProducer( UlfRocketClient client ) {
        this( client, DefaultMQProducer::new, null );
    }


    @Override
    public UlfRocketClient getRocketClient() {
        return (UlfRocketClient)this.mRocketClient;
    }


    @Override
    public void sendMessage( String topic, String ns, String name, UMCMessage message ) throws UMBClientException {
        try{
            this.sendMessage( topic, ns, name, this.mUMCPackageMessageEncoder.encode( message ) );
        }
        catch ( IOException e ) {
            throw new UMBClientException( e );
        }
    }

    @Override
    public void sendMessage( String topic, UMCMessage message ) throws UMBClientException {
        this.sendMessage( topic, "", BroadcastNode.DefaultEntityName, message );
    }

    @Override
    public void sendMessage( UNT unt, String name, UMCMessage message ) throws UMBClientException {
        this.sendMessage( unt.getTopic(), unt.getNamespace(), name, message );
    }


}
