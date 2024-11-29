package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanSimpleStreamReceiveEntity64 extends ArchReceiveEntity implements SimpleStreamReceiveEntity64{
    protected InputStream stream;

    protected SimpleVolume simpleVolume;

    protected SimpleStreamReceiver64 streamReceiver;

    public TitanSimpleStreamReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, InputStream stream, SimpleVolume simpleVolume ) {
        super(volumeManager, storageReceiveIORequest,null);
        this.stream = stream;
        this.simpleVolume = simpleVolume;
        this.streamReceiver = new TitanSimpleStreamReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.streamReceiver.streamReceive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.streamReceiver.streamReceive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return this.streamReceiver.streamReceive( cacheBlock, buffer );
    }

    @Override
    public InputStream getStream() {
        return this.stream;
    }

    @Override
    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public SimpleVolume getSimpleVolume() {
        return this.simpleVolume;
    }
}
