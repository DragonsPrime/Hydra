package com.pinecone.hydra.storage.volume.entity.local.striped.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class TitanStripedStreamReceiveEntity64 extends ArchReceiveEntity implements StripedStreamReceiveEntity64{
    protected InputStream  stream;
    protected StripedVolume stripedVolume;
    protected StripedStreamReceive64 streamReceive;
    public TitanStripedStreamReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, InputStream stream, StripedVolume stripedVolume) {
        super(volumeManager, storageReceiveIORequest,null);
        this.stream = stream;
        this.stripedVolume = stripedVolume;
        this.streamReceive = new TitanStripedStreamReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.streamReceive.streamReceive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.streamReceive.streamReceive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
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
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }
}
