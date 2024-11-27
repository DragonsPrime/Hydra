package com.pinecone.hydra.storage.volume.entity.local.physical.receive;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanDirectReceiveEntity64 extends ArchReceiveEntity implements DirectReceiveEntity64{
    protected String destDirPath;

    protected DirectReceive64 directReceive;

    public TitanDirectReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel, String destDirPath) {
        super(volumeManager, storageReceiveIORequest, channel);
        this.destDirPath = destDirPath;
        this.directReceive = new TitanDirectReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.directReceive.receive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.directReceive.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return this.directReceive.receive( cacheBlock, buffer );
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
