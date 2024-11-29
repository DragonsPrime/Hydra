package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.stream;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanSimpleStreamReceive64 implements SimpleStreamReceiver64{
    protected SimpleVolume                  simpleVolume;

    protected InputStream                   stream;

    protected VolumeManager                 volumeManager;

    protected StorageReceiveIORequest       storageReceiveIORequest;

    protected PhysicalVolume                physicalVolume;


    public TitanSimpleStreamReceive64( SimpleStreamReceiveEntity64 entity ){
        this.volumeManager           = entity.getVolumeManager();
        this.simpleVolume            = entity.getSimpleVolume();
        this.stream                  = entity.getStream();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();

        List<GUID> guids = this.simpleVolume.listPhysicalVolume();
        this.physicalVolume          = this.volumeManager.getPhysicalVolume(guids.get(0));
    }


    @Override
    public StorageIOResponse streamReceive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        TitanDirectStreamReceiveEntity64 titanDirectStreamReceiveEntity64 = new TitanDirectStreamReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.stream, this.physicalVolume.getMountPoint().getMountPoint() );
//        return this.physicalVolume.receive( titanDirectStreamReceiveEntity64 );
        return null;
    }

    @Override
    public StorageIOResponse streamReceive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        TitanDirectStreamReceiveEntity64 titanDirectStreamReceiveEntity64 = new TitanDirectStreamReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.stream, this.physicalVolume.getMountPoint().getMountPoint() );
//        return this.physicalVolume.receive( titanDirectStreamReceiveEntity64, offset, endSize );
        return null;
    }

    @Override
    public StorageIOResponse streamReceive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
//        TitanDirectStreamReceiveEntity64 titanDirectStreamReceiveEntity64 = new TitanDirectStreamReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.stream, this.physicalVolume.getMountPoint().getMountPoint() );
//        return this.physicalVolume.receive( titanDirectStreamReceiveEntity64, cacheBlock, buffer );
        return null;
    }


}
