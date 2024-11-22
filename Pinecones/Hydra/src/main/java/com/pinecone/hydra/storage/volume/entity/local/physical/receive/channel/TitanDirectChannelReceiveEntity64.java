package com.pinecone.hydra.storage.volume.entity.local.physical.receive.channel;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.StorageReceiveIORequest;

import java.io.IOException;
import java.nio.channels.FileChannel;

public class TitanDirectChannelReceiveEntity64 extends ArchReceiveEntity implements DirectChannelReceiveEntity64{
    private KChannel     channel;
    private String       destDirPath;

    public TitanDirectChannelReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, String destDirPath, KChannel channel) {
        super(volumeManager, storageReceiveIORequest);
        this.channel = channel;
        this.destDirPath = destDirPath;
    }


    @Override
    public StorageIOResponse receive() throws IOException {
        TitanDirectChannelReceive64 receive64 = new TitanDirectChannelReceive64();
        return receive64.receive( this );
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException {
        TitanDirectChannelReceive64 receive64 = new TitanDirectChannelReceive64();
        return receive64.receive( this ,offset,endSize);
    }

    @Override
    public KChannel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(KChannel channel) {
        this.channel = channel;
    }

    @Override
    public String getDestDirPath() {
        return this.destDirPath;
    }

    @Override
    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }
}
