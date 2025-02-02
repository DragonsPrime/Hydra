package com.pinecone.hydra.storage.volume.entity.local.striped;

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
import com.pinecone.hydra.storage.volume.entity.local.LocalStripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.export.channel.TitanStripedChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.receive.channnel.TitanStripedChannelReceiverEntity64;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class TitanLocalStripedVolume extends ArchLogicVolume implements LocalStripedVolume {
    private StripedVolumeManipulator stripedVolumeManipulator;

    public TitanLocalStripedVolume(VolumeManager volumeManager, StripedVolumeManipulator stripedVolumeManipulator) {
        super(volumeManager);
        this.stripedVolumeManipulator = stripedVolumeManipulator;
    }

    public TitanLocalStripedVolume( VolumeManager volumeManager){
        super(volumeManager);
    }

    public TitanLocalStripedVolume(){
    }


    @Override
    public void extendLogicalVolume(GUID physicalGuid) {

    }

    @Override
    public List<GUID> listPhysicalVolume() {
        return null;
    }


    public void setStripedVolumeManipulator(StripedVolumeManipulator stripedVolumeManipulator ){
        this.stripedVolumeManipulator = stripedVolumeManipulator;
    }

    @Override
    public String toJSONString() {
        return BeanJSONEncoder.BasicEncoder.encode( this );
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
        return false;
    }

    @Override
    public void build() throws SQLException {
        VolumeConfig config = this.volumeManager.getConfig();
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + config.getPathSeparator() + this.guid + config.getSqliteFileExtension();
        SQLiteExecutor sqLiteExecutor = (SQLiteExecutor) this.volumeManager.getKenusPool().allot(url);
        this.kenVolumeFileSystem.createStripMetaTable( sqLiteExecutor );
        this.volumeManager.put( this );
        this.kenVolumeFileSystem.insertSimpleTargetMappingTab( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
    }

    @Override
    public void storageExpansion(GUID volumeGuid) {
        this.volumeManager.storageExpansion( this.getGuid(), volumeGuid );
        LogicVolume logicVolume = this.volumeManager.get(volumeGuid);
        this.stripedVolumeManipulator.updateDefinitionCapacity(this.guid, logicVolume.getVolumeCapacity().getDefinitionCapacity());
    }

}
