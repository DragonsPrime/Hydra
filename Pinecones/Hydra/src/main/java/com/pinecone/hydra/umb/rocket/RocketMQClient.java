package com.pinecone.hydra.umb.rocket;

import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;
import com.pinecone.hydra.umc.msg.MessageNodus;
import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

import org.apache.rocketmq.client.producer.DefaultMQProducer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RocketMQClient implements RocketClient {
    protected Map<BroadcastProducer, Object> producerRegister;

    protected Map<BroadcastConsumer, Object> consumerRegister;

    private static final Object PRESENT = new Object();

    protected RocketConfig     mRocketConfig;

    protected long             mnNodeId;


    public RocketMQClient( long nodeId, String nameSrvAddr, String groupName ) {
        this.mRocketConfig = new RocketMQConfig(
                nameSrvAddr, groupName, 4096, 8000, 2
        );

        this.producerRegister = new ConcurrentHashMap<>();
        this.consumerRegister = new ConcurrentHashMap<>();
        this.mnNodeId         = nodeId;
    }

    public RocketMQClient( String nameSrvAddr, String groupName ) {
        this( MessageNodus.nextLocalId(), nameSrvAddr, groupName );
    }


    @Override
    public ExtraHeadCoder getExtraHeadCoder() {
        return null;
    }

    @Override
    public long getMessageNodeId() {
        return this.mnNodeId;
    }

    @Override
    public RocketConfig getRocketConfig() {
        return this.mRocketConfig;
    }

    @Override
    public void close() {
        for( Map.Entry<BroadcastConsumer, Object> kv : this.consumerRegister.entrySet() ) {
            kv.getKey().close();
        }

        for( Map.Entry<BroadcastProducer, Object> kv : this.producerRegister.entrySet() ) {
            kv.getKey().close();
        }

        this.consumerRegister.clear();
        this.producerRegister.clear();
    }


    @Override
    public void register( BroadcastProducer producer ) {
        this.producerRegister.put( producer, PRESENT );
    }

    @Override
    public void register( BroadcastConsumer consumer ) {
        this.consumerRegister.put( consumer, PRESENT );
    }

    @Override
    public void deregister( BroadcastProducer producer ) {
        this.producerRegister.remove( producer );
    }

    @Override
    public void deregister( BroadcastConsumer consumer ) {
        this.consumerRegister.remove( consumer );
    }


    @Override
    public BroadcastProducer createProducer( Supplier<DefaultMQProducer> producerSupplier ) {
        BroadcastProducer producer = new UlfBroadcastProducer( this, producerSupplier );
        this.register( producer );
        return producer;
    }

    @Override
    public BroadcastProducer createProducer() {
        return this.createProducer( DefaultMQProducer::new );
    }

    @Override
    public BroadcastConsumer createConsumer( String topic, String ns ) {
        BroadcastConsumer consumer = new UlfPushConsumer( this, topic, ns );
        this.register( consumer );
        return consumer;
    }

    @Override
    public BroadcastConsumer createConsumer( String topic ) {
        return this.createConsumer( topic, "" );
    }

    @Override
    public BroadcastConsumer createConsumer( UNT unt ) {
        return this.createConsumer( unt.getTopic(), unt.getNamespace() );
    }

}
