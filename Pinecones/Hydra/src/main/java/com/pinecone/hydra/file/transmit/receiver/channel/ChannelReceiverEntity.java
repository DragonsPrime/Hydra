package com.pinecone.hydra.file.transmit.receiver.channel;

import com.pinecone.hydra.file.transmit.receiver.ReceiveEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;

public interface ChannelReceiverEntity extends ReceiveEntity {
    FileChannel getChannel();

    void setChannel( FileChannel channel );

    void receive( )throws IOException;
}
