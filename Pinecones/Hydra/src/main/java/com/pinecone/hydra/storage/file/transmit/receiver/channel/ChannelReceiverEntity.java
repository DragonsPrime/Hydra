package com.pinecone.hydra.storage.file.transmit.receiver.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.transmit.receiver.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public interface ChannelReceiverEntity extends ReceiveEntity {
    FileChannel getChannel();
    void setChannel( FileChannel channel );

    void receive( LogicVolume volume ) throws IOException, SQLException;
    void receive( Number offset, Number endSize )throws IOException;

}
