package com.pinecone.hydra.storage.volume.entity;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.hydra.storage.StorageIOResponse;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.local.striped.CacheBlock;
import com.pinecone.hydra.unit.udtt.entity.TreeNode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public interface LogicVolume extends Volume, TreeNode {
    String getName();

    void setName( String name );

    List<LogicVolume> queryChildren();

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


    StorageIOResponse receive( ReceiveEntity entity ) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive( ReceiveEntity entity, Number offset, Number endSize ) throws SQLException, IOException, InvocationTargetException, InstantiationException, IllegalAccessException;
    StorageIOResponse receive( ReceiveEntity entity, CacheBlock cacheBlock, byte[] buffer ) throws SQLException, IOException;

    StorageIOResponse export( ExporterEntity entity ) throws SQLException, IOException;
    //敬请期待
    StorageIOResponse export( ExporterEntity entity, Number offset, Number endSize );
    StorageIOResponse export( ExporterEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer ) throws SQLException, IOException;

    StorageIOResponse export( ExporterEntity entity, boolean accessRandom ) throws SQLException, IOException;
    //敬请期待
    StorageIOResponse export( ExporterEntity entity, Number offset, Number endSize, boolean accessRandom );
    StorageIOResponse export( ExporterEntity entity, CacheBlock cacheBlock, Number offset, Number endSize, byte[] buffer, boolean accessRandom ) throws SQLException, IOException;


    boolean existStorageObject( GUID storageObject ) throws SQLException;

    void build() throws SQLException;

    void storageExpansion( GUID volumeGuid );

    SQLiteExecutor getSQLiteExecutor() throws SQLException;
}
