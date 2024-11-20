package com.pinecone.hydra.storage.volume.entity.local.spanned;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.json.homotype.BeanJSONEncoder;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.ArchLogicVolume;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.volume.entity.LogicVolume;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.entity.local.LocalSpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.export.TitanSpannedChannelExportEntity64;
import com.pinecone.hydra.storage.volume.entity.local.spanned.receive.TitanSpannedChannelReceiveEntity64;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;

import java.io.IOException;
import java.nio.channels.FileChannel;
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
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, FileChannel channel) throws IOException, SQLException {
        TitanSpannedChannelReceiveEntity64 titanSpannedChannelReceiveEntity64 = new TitanSpannedChannelReceiveEntity64( this.volumeManager, storageReceiveIORequest,channel,this );
        StorageIOResponse storageIOResponse = titanSpannedChannelReceiveEntity64.receive();
        storageIOResponse.setBottomGuid( this.guid );
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelReceive(StorageReceiveIORequest storageReceiveIORequest, FileChannel channel, Number offset, Number endSize) throws IOException, SQLException {
        TitanSpannedChannelReceiveEntity64 titanSpannedChannelReceiveEntity64 = new TitanSpannedChannelReceiveEntity64( this.volumeManager, storageReceiveIORequest,channel,this );
        StorageIOResponse storageIOResponse = titanSpannedChannelReceiveEntity64.receive( offset, endSize );
        //storageIOResponse.setBottomGuid( this.guid );
        return storageIOResponse;
    }

    @Override
    public StorageIOResponse channelExport(StorageExportIORequest storageExportIORequest, FileChannel channel) throws IOException, SQLException {
        TitanSpannedChannelExportEntity64 titanSpannedChannelExportEntity64 = new TitanSpannedChannelExportEntity64( this.volumeManager, storageExportIORequest,channel, this );
        return titanSpannedChannelExportEntity64.export();
    }

    @Override
    public StorageIOResponse channelRaid0Export(StorageExportIORequest storageExportIORequest, FileChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer) throws IOException, SQLException {
        return null;
    }


    @Override
    public String toString() {
        return this.toJSONString();
    }

    @Override
    public boolean existStorageObject(GUID storageObject) throws SQLException {
        List<LogicVolume> volumes = this.getChildren();
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
        PhysicalVolume smallestCapacityPhysicalVolume = this.volumeManager.getSmallestCapacityPhysicalVolume();
        String url = smallestCapacityPhysicalVolume.getMountPoint().getMountPoint() + "/" + this.guid + ".db";
        SQLiteExecutor sqLiteExecutor = new SQLiteExecutor( new SQLiteHost(url) );
        this.kenVolumeFileSystem.creatKVFSCollisionTable( sqLiteExecutor );
        this.kenVolumeFileSystem.creatKVFSIndexTable( sqLiteExecutor );
        List<LogicVolume> volumes = this.getChildren();
        int index = 0;
        for( LogicVolume volume : volumes ){
            this.kenVolumeFileSystem.insertKVFSIndexTable( sqLiteExecutor, index, volume.getGuid() );
            index++;
        }
        this.kenVolumeFileSystem.insertKVFSMateTable( smallestCapacityPhysicalVolume.getGuid(), this.getGuid() );
        this.volumeManager.put( this );
    }

    @Override
    public void storageExpansion(GUID volumeGuid) {
        this.volumeManager.storageExpansion( this.getGuid(), volumeGuid );
    }

}
