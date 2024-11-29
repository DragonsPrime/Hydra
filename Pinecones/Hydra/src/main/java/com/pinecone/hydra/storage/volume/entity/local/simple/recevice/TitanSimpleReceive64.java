package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.KChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.receive.TitanDirectReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanSimpleReceive64 implements SimpleReceive64{
    private SimpleVolume            simpleVolume;

    private KChannel                fileChannel;

    private VolumeManager           volumeManager;

    private StorageReceiveIORequest storageReceiveIORequest;

    public TitanSimpleReceive64( ISimpleReceiveEntity entity ){
        this.simpleVolume = entity.getSimpleVolume();
        this.fileChannel  = entity.getKChannel();
        this.volumeManager = entity.getVolumeManager();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
    }

    @Override
    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.fileChannel, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity );
    }

    @Override
    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.fileChannel, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, offset, endSize );
    }

    @Override
    public StorageIOResponse receive(CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, this.fileChannel, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, cacheBlock, buffer );
    }
}
