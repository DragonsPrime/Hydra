package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanSimpleReceiveEntity64 extends ArchReceiveEntity implements SimpleReceiveEntity64{
    protected SimpleVolume      simpleVolume;

    protected SimpleReceive     simpleReceive;

    protected Chanface          chanface;

    public TitanSimpleReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, Chanface channel, SimpleVolume volume) {
        super(volumeManager, storageReceiveIORequest, channel);
        this.simpleVolume  = volume;
        this.simpleReceive = new TitanSimpleReceive64( this );
        this.chanface      = channel;
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.simpleReceive.receive(this.chanface);
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.simpleReceive.receive(this.chanface, offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return this.simpleReceive.receive(this.chanface, cacheBlock, buffer );
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }
}
