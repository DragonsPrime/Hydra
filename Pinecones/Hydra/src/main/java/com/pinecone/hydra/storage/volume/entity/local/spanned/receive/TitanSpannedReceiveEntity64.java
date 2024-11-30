package com.pinecone.hydra.storage.volume.entity.local.spanned.receive;

import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class TitanSpannedReceiveEntity64 extends ArchReceiveEntity implements SpannedReceiveEntity64{
    protected SpannedVolume    spannedVolume;

    protected SpannedReceive64 spannedReceive;
    public TitanSpannedReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, Chanface channel, SpannedVolume spannedVolume) {
        super(volumeManager, storageReceiveIORequest, channel);
        this.spannedVolume = spannedVolume;
        this.spannedReceive = new TitanSpannedReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.spannedReceive.receive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return this.spannedReceive.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        return null;
    }

    @Override
    public SpannedVolume getSpannedVolume() {
        return this.spannedVolume;
    }
}
