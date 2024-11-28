package com.pinecone.hydra.storage.volume.entity.local.spanned.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.channel.TitanSpannedChannelReceive64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class TitanSpannedStreamReceiveEntity64 extends ArchReceiveEntity implements SpannedStreamReceiveEntity64{
    protected InputStream   stream;
    protected SpannedVolume spannedVolume;
    protected SpannedStreamReceive64 spannedStreamReceive64;

    public TitanSpannedStreamReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, InputStream stream, SpannedVolume spannedVolume) {
        super(volumeManager, storageReceiveIORequest,null);
        this.stream = stream;
        this.spannedVolume = spannedVolume;
        this.spannedStreamReceive64 = new TitanSpannedStreamReceive64( this );
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        return this.spannedStreamReceive64.streamReceive();
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        return this.spannedStreamReceive64.streamReceive( offset, endSize );
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
    public SpannedVolume getSpannedVolume() {
        return this.spannedVolume;
    }
}
