package com.pinecone.hydra.umb.broadcast;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.hydra.umb.UMBClientException;
import com.pinecone.hydra.umb.UMBServiceException;
import com.pinecone.hydra.umb.UlfPackageMessageHandler;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public interface BroadcastProducer extends Pinenut {
    void close();

    void start() throws UMBServiceException;

    void sendMessage( String topic, String ns, String name, byte[] body ) throws UMBClientException ;

    void sendMessage( String topic, byte[] body ) throws UMBClientException ;

    void sendMessage( UNT unt, String name, byte[] body ) throws UMBClientException ;
}
