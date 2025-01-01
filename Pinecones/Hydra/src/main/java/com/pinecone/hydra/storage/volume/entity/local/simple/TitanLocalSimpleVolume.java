package com.pinecone.hydra.storage.volume.entity.local.simple;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeConfig;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.volume.entity.ExporterEntity;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.ReceiveEntity;
import com.pinecone.hydra.storage.volume.entity.local.LocalSimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalSimpleVolume extends ArchLogicVolume implements LocalSimpleVolume {
    protected SimpleVolumeManipulator simpleVolumeManipulator;

    protected MappedExecutor          mappedExecutor;


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
    public List<LogicVolume> queryChildren() {
        return super.queryChildren();
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
    public StorageIOResponse export(ExporterEntity entity, boolean accessRandom) throws SQLException, IOException {
        return null;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, Number offset, Number endSize, boolean accessRandom) {
        return null;
    }

    @Override
    public StorageIOResponse export(ExporterEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer, boolean accessRandom) throws SQLException, IOException {
        return null;
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
        return this.kenVolumeFileSystem.existStorageObject( this.mappedExecutor, storageObject );
    }

    private synchronized void saveMate(StorageIOResponse storageIOResponse, String storageObjectName) throws SQLException {
        if( !kenVolumeFileSystem.existStorageObject( this.mappedExecutor, storageIOResponse.getObjectGuid() ) ){
            this.kenVolumeFileSystem.insertSimpleTargetMappingSoloRecord( storageIOResponse.getObjectGuid(), storageObjectName, storageIOResponse.getSourceName(), this.mappedExecutor );
        }

    }

    @Override
    public void build() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + config.getPathSeparator() + this.guid + config.getSqliteFileExtension();
        SQLiteExecutor sqLiteExecutor = (SQLiteExecutor) this.volumeManager.getKenusPool().allot(url);
        this.mappedExecutor = sqLiteExecutor;
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
        if( physicsGuid == null ){
            return null;
        }
        PhysicalVolume physicalVolume = this.volumeManager.getPhysicalVolume(physicsGuid);

        String url = physicalVolume.getMountPoint().getMountPoint()+ config.getPathSeparator() +this.guid+ config.getSqliteFileExtension();
        return (SQLiteExecutor) this.volumeManager.getKenusPool().allot(url);
    }

    public void assembleSQLiteExecutor() throws SQLException {
        this.mappedExecutor = this.getSQLiteExecutor();
    }
}
