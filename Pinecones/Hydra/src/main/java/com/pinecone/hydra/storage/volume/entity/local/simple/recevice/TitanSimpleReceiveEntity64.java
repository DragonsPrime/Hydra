package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.sql.SQLException;

public class TitanSimpleReceiveEntity64 extends ArchReceiveEntity implements SimpleReceiveEntity64{
    protected SimpleVolume    simpleVolume;

    protected ISimpleReceive  simpleReceive;

    public TitanSimpleReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, KChannel channel,SimpleVolume volume) {
        super(volumeManager, storageReceiveIORequest, channel);
        this.simpleVolume  = volume;
        this.simpleReceive = new TitanSimpleReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.simpleReceive.receive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.simpleReceive.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return this.simpleReceive.receive( cacheBlock, buffer );
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }
}
