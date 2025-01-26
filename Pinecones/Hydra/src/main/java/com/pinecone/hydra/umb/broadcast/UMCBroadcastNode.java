package com.pinecone.hydra.umb.broadcast;

import com.pinecone.hydra.umc.msg.extra.ExtraHeadCoder;

public interface UMCBroadcastNode extends BroadcastNode {
    ExtraHeadCoder getExtraHeadCoder();

    UMCBroadcastProducer createUlfProducer() ;


    UMCBroadcastConsumer createUlfConsumer( String topic, String ns ) ;

    UMCBroadcastConsumer createUlfConsumer( String topic ) ;

    UMCBroadcastConsumer createUlfConsumer( UNT unt ) ;
}
