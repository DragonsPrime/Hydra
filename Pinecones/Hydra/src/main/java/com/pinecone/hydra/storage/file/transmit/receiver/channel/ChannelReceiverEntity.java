package com.pinecone.hydra.storage.file.transmit.receiver.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;


public interface ChannelReceiverEntity extends ReceiveEntity {
    KChannel getChannel();
    void setChannel( KChannel channel );



}
