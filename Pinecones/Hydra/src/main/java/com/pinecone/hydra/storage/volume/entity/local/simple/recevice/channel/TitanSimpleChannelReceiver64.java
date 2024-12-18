package com.pinecone.hydra.storage.volume.entity.local.simple.recevice.channel;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.RandomAccessChanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanSimpleChannelReceiver64   implements SimpleChannelReceiver64{
    private SimpleVolume            simpleVolume;

    private Chanface fileChannel;

    private VolumeManager           volumeManager;

    private StorageReceiveIORequest storageReceiveIORequest;

    public TitanSimpleChannelReceiver64( SimpleChannelReceiverEntity entity ){
        this.volumeManager = entity.getVolumeManager();
        this.simpleVolume = entity.getSimpleVolume();
        this.fileChannel = entity.getChannel();
        this.storageReceiveIORequest = entity.getReceiveStorageObject();
    }
    @Override
    public StorageIOResponse channelReceive() throws IOException, SQLException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));

        return physicalVolume.channelReceive( this.volumeManager,this.storageReceiveIORequest,this.fileChannel );
    }

    @Override
    public StorageIOResponse channelReceive(Number offset, Number endSize) throws IOException {
        List<GUID> guids = simpleVolume.listPhysicalVolume();
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(guids.get(0));
        return physicalVolume.channelReceive( this.volumeManager,this.storageReceiveIORequest,this.fileChannel, offset,endSize );
    }

//    @Override
//    public StorageIOResponse receive() throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        return null;
//    }
//
//    @Override
//    public StorageIOResponse receive(Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
//        return null;
//    }

    @Override
    public StorageIOResponse receive(Chanface chanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }

    @Override
    public StorageIOResponse receive(Chanface chanface, Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }

    @Override
    public StorageIOResponse receive(RandomAccessChanface randomAccessChanface, Number offset, Number endSize) throws IOException, SQLException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return null;
    }
}
