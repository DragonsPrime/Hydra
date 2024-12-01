package com.pinecone.hydra.storage.volume.kvfs;

import com.pinecone.framework.system.prototype.Pinenut;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;

import java.sql.SQLException;

public interface OnVolumeFileSystem extends Pinenut {
    GUID getKVFSPhysicsVolume(GUID volumeGuid);

    void insertSimpleTargetMappingTab(GUID physicsGuid, GUID volumeGuid);

    void createSimpleTargetMappingTab(MappedExecutor mappedExecutor ) throws SQLException;

    void insertSimpleTargetMappingSoloRecord(GUID storageObjectGuid, String storageObjectName, String sourceName, MappedExecutor mappedExecutor ) throws SQLException;
    String getSimpleStorageObjectSourceName(GUID storageObjectGuid, MappedExecutor mappedExecutor ) throws SQLException;

    boolean existStorageObject( MappedExecutor mappedExecutor, GUID storageObjectGuid ) throws SQLException;

    int hashStorageObjectID( GUID keyGuid, int volumeNum);

    void createSpannedIndexTable(MappedExecutor mappedExecutor ) throws SQLException;
    void insertSpannedIndexTable(MappedExecutor mappedExecutor, int hashKey, GUID targetVolumeGuid ) throws SQLException;
    GUID getSpannedIndexTableTargetGuid(MappedExecutor mappedExecutor, int hashKey ) throws SQLException;


    void creatSpanLinkedVolumeTable( MappedExecutor mappedExecutor ) throws SQLException;
    void insertSpanLinkedVolumeTable( MappedExecutor mappedExecutor, int hashKey, GUID keyGuid, GUID targetVolumeGuid ) throws SQLException;
    GUID getSpanLinkedVolumeTableTargetGuid( MappedExecutor mappedExecutor, GUID keyGuid ) throws SQLException;

    void createStripMetaTable(MappedExecutor mappedExecutor ) throws SQLException;
    void insertStripMetaTable(MappedExecutor mappedExecutor, int code, GUID volumeGuid, GUID storageObjectGuid, String sourceName ) throws SQLException;
    String getStripMetaSourceName(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid ) throws SQLException;
    int getStripMetaCode(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid ) throws SQLException;
    boolean isExistStripMetaTable(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid ) throws SQLException;
}
