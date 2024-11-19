package com.pinecone.hydra.storage.file.transmit.receiver.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.KOMFileSystem;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.transmit.receiver.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;

public class GenericChannelReceiveEntity extends ArchReceiveEntity implements ChannelReceiverEntity{
    private FileChannel     channel;
    private ChannelReceiver channelReceiver;

    public GenericChannelReceiveEntity(KOMFileSystem fileSystem, String destDirPath, FileNode file, FileChannel channel ) {
        super(fileSystem, destDirPath, file);
        this.channel = channel;
        this.channelReceiver = new ChannelReceiver64( this.getFileSystem() );
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
    public void receive(LogicVolume volume) throws IOException, SQLException {
        this.fileSystem.affirmFileNode( this.destDirPath );
        this.channelReceiver.receive(this, volume);
    }

    @Override
    public void receive(Number offset, Number endSize) throws IOException {
        this.fileSystem.affirmFileNode( this.destDirPath );
        this.channelReceiver.receive( this, offset, endSize );
    }

}
