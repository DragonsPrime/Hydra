package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeTree;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.ReceiveStorageObject;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanDirectChannelReceiveEntity64 extends ArchReceiveEntity implements DirectChannelReceiveEntity64{
    private FileChannel channel;

    public TitanDirectChannelReceiveEntity64(VolumeTree volumeTree, ReceiveStorageObject receiveStorageObject, String destDirPath, FileChannel channel) {
        super(volumeTree, receiveStorageObject, destDirPath);
        this.channel = channel;
    }


    @Override
    public MiddleStorageObject receive() throws IOException {
        TitanDirectChannelReceive64 receive64 = new TitanDirectChannelReceive64();
        return receive64.receive( this );
    }

    @Override
    public MiddleStorageObject receive(Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceive64 receive64 = new TitanDirectChannelReceive64();
        return receive64.receive( this ,offset,endSize);
    }

    @Override
    public FileChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(FileChannel channel) {
        this.channel = channel;
    }
}
