package com.pinecone.hydra.storage.volume.kvfs;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;

import java.sql.SQLException;

public interface OnVolumeFileSystem extends Pinenut {
    GUID getKVFSPhysicsVolume(GUID volumeGuid);

    void insertKVFSMateTable(GUID physicsGuid, GUID volumeGuid);

    void createKVFSMetaTable(MappedExecutor mappedExecutor ) throws SQLException;

    void insertKVFSTable(GUID storageObjectGuid, String storageObjectName, MappedExecutor mappedExecutor ) throws SQLException;

    boolean existStorageObject( MappedExecutor mappedExecutor, GUID storageObjectGuid ) throws SQLException;

    int KVFSHash( GUID keyGuid, int volumeNum);

    void creatKVFSIndexTable( MappedExecutor mappedExecutor ) throws SQLException;
    void insertKVFSIndexTable( MappedExecutor mappedExecutor, int hashKey, GUID targetVolumeGuid ) throws SQLException;
    GUID getKVFSIndexTableTargetGuid(MappedExecutor mappedExecutor, int hashKey ) throws SQLException;


    void creatKVFSCollisionTable( MappedExecutor mappedExecutor ) throws SQLException;
    void insertKVFSCollisionTable( MappedExecutor mappedExecutor, int hashKey, GUID keyGuid, GUID targetVolumeGuid ) throws SQLException;
    GUID getKVFSCollisionTableTargetGuid( MappedExecutor mappedExecutor, GUID keyGuid ) throws SQLException;

    void createKVFSFileStripTable( MappedExecutor mappedExecutor ) throws SQLException;
    void insertKVFSFileStripTable( MappedExecutor mappedExecutor, int code, GUID volumeGuid, GUID storageObjectGuid, String sourceName ) throws SQLException;
    String getKVFSFileStripSourceName( MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid ) throws SQLException;
}
