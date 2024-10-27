package com.pinecone.hydra.storage.file.transmit.receiver.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiveEntity;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class GenericChannelReceiveEntity extends ArchReceiveEntity implements ChannelReceiverEntity{
    private FileChannel     channel;
    private ChannelReceiver channelReceiver;

    public GenericChannelReceiveEntity(KOMFileSystem fileSystem, String destDirPath, FileNode file, FileChannel channel ) {
        super(fileSystem, destDirPath, file);
        this.channel = channel;
        this.channelReceiver = new GenericChannelReceiver( this.getFileSystem() );
    }

    @Override
    public ChannelReceiverEntity evinceChannelReceiverEntity() {
        return this;
    }

    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }

    @Override
    public void receive() throws IOException {
        this.channelReceiver.receive(this);
    }

    @Override
    public void receive(Number offset, Number endSize) throws IOException {
        this.channelReceiver.receive( this, offset, endSize );
    }

    @Override
    public void receive(GUID frameGuid, int threadId, int threadNum) throws IOException {
        this.channelReceiver.receive( this,frameGuid,threadId,threadNum );
    }
}
