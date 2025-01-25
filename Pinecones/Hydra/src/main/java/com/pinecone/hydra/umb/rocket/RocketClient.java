package com.pinecone.hydra.umb.rocket;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umb.broadcast.BroadcastConsumer;
import com.pinecone.hydra.umb.broadcast.BroadcastProducer;
import com.pinecone.hydra.umb.broadcast.UNT;

public interface RocketClient extends Pinenut {
    RocketConfig getRocketConfig();

    void close();

    void register( BroadcastProducer producer );

    void register( BroadcastConsumer consumer );

    void deregister( BroadcastProducer producer ) ;

    void deregister( BroadcastConsumer consumer ) ;


    BroadcastProducer createProducer() ;

    BroadcastConsumer createConsumer( String topic, String ns ) ;

    BroadcastConsumer createConsumer( String topic );

    BroadcastConsumer createConsumer( UNT unt ) ;
}
