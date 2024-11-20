package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.InputChannel;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.StorageExportIORequest;
import com.pinecone.hydra.storage.StorageReceiveIORequest;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.List;

public interface LogicVolume extends Volume, TreeNode {
    String getName();

    void setName( String name );

    List<LogicVolume> getChildren();

    void setChildren( List<LogicVolume> children );

    VolumeCapacity64 getVolumeCapacity();

    void setVolumeCapacity( VolumeCapacity64 volumeCapacity );

    void extendLogicalVolume( GUID physicalGuid );
    List< GUID > listPhysicalVolume();

    default MirroredVolume evinceMirroredVolume(){
        return null;
    }
    default SimpleVolume   evinceSimpleVolume(){
        return null;
    }
    default SpannedVolume  evinceSpannedVolume(){
        return null;
    }
    default StripedVolume  evinceStripeVolume(){
        return null;
    }
    void setVolumeTree( VolumeManager volumeManager);


    StorageIOResponse channelReceive     (StorageReceiveIORequest storageReceiveIORequest, FileChannel channel ) throws IOException, SQLException;
    StorageIOResponse channelReceive     (StorageReceiveIORequest storageReceiveIORequest, FileChannel channel, Number offset, Number endSize ) throws IOException, SQLException;
    StorageIOResponse channelExport      (StorageExportIORequest storageExportIORequest, FileChannel channel ) throws IOException, SQLException;
    StorageIOResponse channelRaid0Export (StorageExportIORequest storageExportIORequest, FileChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws IOException, SQLException;

    StorageIOResponse receive( InputChannel inputChannel );

    boolean existStorageObject( GUID storageObject ) throws SQLException;

    void build() throws SQLException;

    void storageExpansion( GUID volumeGuid );

    SQLiteExecutor getSQLiteExecutor() throws SQLException;
}
