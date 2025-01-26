package com.pinecone.hydra.umb.broadcast;

import com.pinecone.hydra.umb.UMBClientException;
import com.pinecone.hydra.umc.msg.UMCMessage;

public interface UMCBroadcastProducer extends BroadcastProducer {

    void sendMessage( String topic, String ns, String name, UMCMessage message ) throws UMBClientException ;

    void sendMessage( String topic, UMCMessage message ) throws UMBClientException ;

    void sendMessage( UNT unt, String name, UMCMessage message ) throws UMBClientException ;

}
