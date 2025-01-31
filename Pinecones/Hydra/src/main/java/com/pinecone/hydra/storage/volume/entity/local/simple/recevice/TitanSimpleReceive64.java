package com.pinecone.hydra.storage.volume.entity.local.simple.recevice;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.io.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
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

    private VolumeManager           volumeManager;

    private StorageReceiveIORequest storageReceiveIORequest;

    public TitanSimpleReceive64( SimpleReceiveEntity entity ){
        this.simpleVolume = entity.getSimpleVolume();
        this.volumeManager = entity.getVolumeManager();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
    }

    @Override
    public StorageIOResponse receive(Chanface chanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, chanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity );
    }

    @Override
    public StorageIOResponse receive(Chanface chanface,Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, chanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, offset, endSize );
    }

    @Override
    public StorageIOResponse receive(Chanface chanface,CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, chanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, cacheBlock, buffer );
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, randomAccessChanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity );
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface, Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, randomAccessChanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, offset, endSize );
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface, CacheBlock cacheBlock, byte[] buffer) throws IOException, SQLException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        TitanDirectReceiveEntity64 receiveEntity = new TitanDirectReceiveEntity64( this.volumeManager, this.storageReceiveIORequest, randomAccessChanface, physicalVolume.getMountPoint().getMountPoint() );
        return physicalVolume.receive( receiveEntity, cacheBlock, buffer );
    }
}
