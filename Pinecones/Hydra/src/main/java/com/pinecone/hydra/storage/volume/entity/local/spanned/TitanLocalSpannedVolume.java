package com.pinecone.hydra.storage.volume.entity.local.spanned;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
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
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalSpannedVolume extends ArchLogicVolume implements LocalSpannedVolume {
    private SpannedVolumeManipulator spannedVolumeManipulator;

    public TitanLocalSpannedVolume(VolumeManager volumeManager, SpannedVolumeManipulator spannedVolumeManipulator) {
        super(volumeManager);
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    public TitanLocalSpannedVolume( VolumeManager volumeManager){
        super(volumeManager);
    }

    public TitanLocalSpannedVolume(){
    }
    public void setSpannedVolumeManipulator( SpannedVolumeManipulator spannedVolumeManipulator ){
        this.spannedVolumeManipulator = spannedVolumeManipulator;
    }
    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }


    @Override
    public void setVolumeTree(VolumeManager volumeManager) {
        this.volumeManager = volumeManager;
    }


    @Override
    public StorageIOResponse receive(ReceiveEntity entity) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return entity.receive();
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, Number offset, Number endSize) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return entity.receive( offset, endSize );
    }

    @Override
    public StorageIOResponse receive(ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer) throws SQLException, IOException {
        return null;
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
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        List<LogicVolume> volumes = this.queryChildren();
        for( LogicVolume volume : volumes ){
            if ( volume.existStorageObject( storageObject ) ){
                return true;
            }
        }
        return false;
    }

    // Build 模式，最后去执行
    @Override
    public void build() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + config.getPathSeparator() + this.guid + config.getSqliteFileExtension();
        SQLiteExecutor sqLiteExecutor = (SQLiteExecutor) this.volumeManager.getKenusPool().allot(url);
        this.kenVolumeFileSystem.creatSpanLinkedVolumeTable( sqLiteExecutor );
        this.kenVolumeFileSystem.createSpannedIndexTable( sqLiteExecutor );
        List<LogicVolume> volumes = this.queryChildren();
        int index = 0;
        for( LogicVolume volume : volumes ){
            this.kenVolumeFileSystem.insertSpannedIndexTable( sqLiteExecutor, index, volume.getGuid() );
            index++;
        }
        this.kenVolumeFileSystem.insertSimpleTargetMappingTab( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
        this.volumeManager.put( this );
    }

    @Override
    public void storageExpansion(GUID volumeGuid) {
        this.volumeManager.storageExpansion( this.getGuid(), volumeGuid );
    }

}
