package com.pinecone.hydra.storage.volume.entity.local.simple;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.Chanface;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.recevice.channel.TitanSimpleChannelReceiverEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalSimpleVolume extends ArchLogicVolume implements LocalSimpleVolume {
    private SimpleVolumeManipulator simpleVolumeManipulator;


    public TitanLocalSimpleVolume(VolumeManager volumeManager, SimpleVolumeManipulator simpleVolumeManipulator) {
        super(volumeManager);
        this.simpleVolumeManipulator = simpleVolumeManipulator;
    }

    public TitanLocalSimpleVolume( VolumeManager volumeManager){
        super(volumeManager);
    }

    public TitanLocalSimpleVolume(){
    }

    public void setSimpleVolumeManipulator( SimpleVolumeManipulator simpleVolumeManipulator ){
        this.simpleVolumeManipulator = simpleVolumeManipulator;
    }

    @Override
    public List<LogicVolume> getChildren() {
        return super.getChildren();
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {
        this.simpleVolumeManipulator.extendLogicalVolume( this.guid, physicalGuid );
    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return this.simpleVolumeManipulator.listPhysicalVolume( this.guid );
    }


    @Override
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, Chanface channel ) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeManager, storageReceiveIORequest, channel, this );
        StorageIOResponse storageIOResponse = titanSimpleChannelReceiverEntity64.receive();
        this.saveMate( storageIOResponse, storageReceiveIORequest.getName() );
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, Chanface channel, Number offset, Number endSize ) throws IOException, SQLException {
        TitanSimpleChannelReceiverEntity64 titanSimpleChannelReceiverEntity64 = new TitanSimpleChannelReceiverEntity64( this.volumeManager, storageReceiveIORequest, channel, this );
        StorageIOResponse storageIOResponse = titanSimpleChannelReceiverEntity64.receive(offset,endSize);
        this.saveMate(storageIOResponse, storageReceiveIORequest.getName());
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelExport(StorageExportIORequest storageExportIORequest, Chanface channel ) throws IOException, SQLException {
        SQLiteExecutor sqLiteExecutor = this.getSQLiteExecutor();
        String sourceName = this.kenVolumeFileSystem.getSimpleStorageObjectSourceName(storageExportIORequest.getStorageObjectGuid(), sqLiteExecutor);
        storageExportIORequest.setSourceName( sourceName );
//        TitanSimpleChannelExportEntity64 titanSimpleChannelExportEntity64 = new TitanSimpleChannelExportEntity64( this.volumeManager, storageExportIORequest, channel );
//        return titanSimpleChannelExportEntity64.export();
        return null;
    }

    @Override
    public StorageIOResponse channelExport(StorageExportIORequest storageExportIORequest, Chanface channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws IOException, SQLException {
//        TitanSimpleChannelExportEntity64 titanSimpleChannelExportEntity64 = new TitanSimpleChannelExportEntity64( this.volumeManager, storageExportIORequest, channel );
//        return titanSimpleChannelExportEntity64.export( cacheBlock, offset, endSize, buffer );
        return null;
    }


    @Override
    public StorageIOResponse receive(ReceiveEntity entity) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        StorageIOResponse response = entity.receive();
        this.saveMate( response, entity.getReceiveStorageObject().getName() );
        return response ;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, Number offset, Number endSize) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        StorageIOResponse response = entity.receive( offset, endSize );
        this.saveMate( response, entity.getReceiveStorageObject().getName() );
        return response;
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer) throws SQLException, IOException {
        StorageIOResponse response = entity.receive(cacheBlock, buffer);
        this.saveMate( response, entity.getReceiveStorageObject().getName() );
        return response;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity) throws SQLException, IOException {
        return entity.export();
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, Number offset, Number endSize) {
        return null;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws SQLException, IOException {
        return entity.export( cacheBlock, offset, endSize, buffer );
    }

    @Override
    public void setVolumeTree( VolumeManager volumeManager ) {
        this.volumeManager = volumeManager;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        SQLiteExecutor sqLiteExecutor = this.getSQLiteExecutor();
        return this.kenVolumeFileSystem.existStorageObject( sqLiteExecutor, storageObject );
    }

    private synchronized void saveMate(StorageIOResponse storageIOResponse, String storageObjectName) throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.guid);
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.guid+ config.getSqliteFileExtension();
        File file = new File(url);
        //long totalSpace = file.getTotalSpace();
        SQLiteHost sqLiteHost = new SQLiteHost(url);
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( sqLiteHost );
        if( !kenVolumeFileSystem.existStorageObject( sqLiteExecutor, storageIOResponse.getObjectGuid() ) ){
            this.kenVolumeFileSystem.insertSimpleTargetMappingSoloRecord( storageIOResponse.getObjectGuid(), storageObjectName, storageIOResponse.getSourceName(), sqLiteExecutor );
        }
        sqLiteHost.close();

//        long newTotalSpace = file.getTotalSpace();
//        VolumeCapacity64 physicalVolumeVolumeCapacity = physicalVolume.getVolumeCapacity();
//        physicalVolumeVolumeCapacity.setUsedSize( physicalVolumeVolumeCapacity.getUsedSize() - ( totalSpace - newTotalSpace ) );
//        this.volumeCapacity.setUsedSize( this.volumeCapacity.getUsedSize() - ( totalSpace - newTotalSpace ) );
    }

    @Override
    public void build() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + config.getPathSeparator() + this.guid + config.getSqliteFileExtension();
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        this.kenVolumeFileSystem.createSimpleTargetMappingTab( sqLiteExecutor );
        this.volumeManager.put( this );
        this.kenVolumeFileSystem.insertSimpleTargetMappingTab( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
    }

    @Override
    public void storageExpansion(GUID volumeGuid) {
        this.extendLogicalVolume( volumeGuid );
    }
    @Override
    public SQLiteExecutor getSQLiteExecutor() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        GUID physicsGuid = this.kenVolumeFileSystem.getKVFSPhysicsVolume(this.guid);
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);
        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.guid+ config.getSqliteFileExtension();
        SQLiteHost sqLiteHost = new SQLiteHost(url);
        return new SQLiteExecutor( sqLiteHost );
    }
}
