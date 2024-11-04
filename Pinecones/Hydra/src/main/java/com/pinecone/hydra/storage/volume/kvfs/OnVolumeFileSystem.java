package com.pinecone.hydra.storage.volume.kvfs;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;

import java.sql.SQLException;

public interface OnVolumeFileSystem extends Pinenut {
    GUID getKVFSPhysicsVolume(GUID volumeGuid);

    void insertKVFSDatabaseMeta(GUID physicsGuid, GUID volumeGuid);

    void createKVFSDatabase( MappedExecutor mappedExecutor ) throws SQLException;

    void insertKVFSDatabaseMeta(GUID storageObjectGuid, String storageObjectName, MappedExecutor mappedExecutor ) throws SQLException;

    boolean existStorageObject( MappedExecutor mappedExecutor, GUID storageObjectGuid ) throws SQLException;

    int KVFSHash( GUID keyGuid, int volumeNum);
}
