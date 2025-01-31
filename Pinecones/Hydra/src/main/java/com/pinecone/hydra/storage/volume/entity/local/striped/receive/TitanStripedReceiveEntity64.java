package com.pinecone.hydra.storage.volume.entity.local.striped.receive;

import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanStripedReceiveEntity64 extends ArchReceiveEntity implements StripedReceiveEntity64{
    protected StripedVolume         stripedVolume;

    protected StripedReceive64      stripedReceive;

    protected Chanface              chanface;

    public TitanStripedReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, Chanface channel, StripedVolume stripedVolume) {
        super(volumeManager, storageReceiveIORequest, channel);
        this.stripedVolume  = stripedVolume;
        this.stripedReceive = new TitanStripedReceive64( this );
        this.chanface = channel;
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.stripedReceive.receive(this.chanface);
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.stripedReceive.receive( this.chanface, offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
    }

    @Override
    public StripedVolume getStripedVolume() {
        return this.stripedVolume;
    }
}
