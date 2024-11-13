package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.MiddleStorageObject;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripExportFlyweightEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.StripLockEntity;
import com.pinecone.hydra.storage.volume.entity.local.striped.TerminalStateRecord;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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


    MiddleStorageObject channelReceive( ReceiveStorageObject receiveStorageObject, FileChannel channel ) throws IOException, SQLException;
    MiddleStorageObject channelReceive( ReceiveStorageObject receiveStorageObject, FileChannel channel, Number offset, Number endSize ) throws IOException, SQLException;
    MiddleStorageObject channelExport( ExportStorageObject exportStorageObject, FileChannel channel ) throws IOException, SQLException;
    MiddleStorageObject channelRaid0Export(ExportStorageObject exportStorageObject, FileChannel channel, CacheBlock cacheBlock, Number offset, Number endSize, StripExportFlyweightEntity flyweightEntity) throws IOException, SQLException;

    boolean existStorageObject( GUID storageObject ) throws SQLException;

    void build() throws SQLException;

    void storageExpansion( GUID volumeGuid );

    SQLiteExecutor getSQLiteExecutor() throws SQLException;
}
