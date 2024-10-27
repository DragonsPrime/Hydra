package com.pinecone.hydra.storage.file.transmit.receiver.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface ChannelReceiverEntity extends ReceiveEntity {
    FileChannel getChannel();

    void setChannel( FileChannel channel );

    void receive( )throws IOException;
    void receive( Number offset, Number endSize )throws IOException;
    void receive(GUID frameGuid, int threadId, int threadNum) throws IOException;

}
