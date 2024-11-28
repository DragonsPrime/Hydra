package com.pinecone.hydra.storage.volume.entity.local.physical.receive.stream;

import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class TitanDirectStreamReceiveEntity64 extends ArchReceiveEntity implements DirectStreamReceiveEntity64{
    protected InputStream   stream;
    protected String        destDirPath;

    public TitanDirectStreamReceiveEntity64(VolumeManager volumeManager, StorageReceiveIORequest storageReceiveIORequest, InputStream stream, String destDirPath) {
        super(volumeManager, storageReceiveIORequest,null);
        this.stream = stream;
        this.destDirPath = destDirPath;
    }


    @Override
    public StorageIOResponse receive() throws IOException, SQLException {
        TitanDirectStreamReceive64 titanDirectStreamReceive64 = new TitanDirectStreamReceive64();
        return titanDirectStreamReceive64.receive(this);
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException {
        TitanDirectStreamReceive64 titanDirectStreamReceive64 = new TitanDirectStreamReceive64();
        return titanDirectStreamReceive64.receive(this, offset, endSize);
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        TitanDirectStreamReceive64 titanDirectStreamReceive64 = new TitanDirectStreamReceive64();
        return titanDirectStreamReceive64.receive(this, cacheBlock, buffer);
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
    public String getDestDirPath() {
        return this.destDirPath;
    }

    @Override
    public void setDestDirPath(String destDirPath) {
        this.destDirPath = destDirPath;
    }
}
