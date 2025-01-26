package com.pinecone.hydra.umb.broadcast;

import com.pinecone.framework.system.prototype.Pinenut;

public interface BroadcastNode extends Pinenut {
    String DefaultEntityName = "__DEFAULT__";

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
