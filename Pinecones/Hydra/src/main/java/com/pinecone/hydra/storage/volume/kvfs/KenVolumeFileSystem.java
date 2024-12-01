package com.pinecone.hydra.storage.volume.kvfs;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.rdb.MappedExecutor;
import com.pinecone.framework.util.rdb.ResultSession;
import com.pinecone.framework.util.sqlite.SQLiteExecutor;
import com.pinecone.framework.util.sqlite.SQLiteHost;
import com.pinecone.hydra.storage.volume.VolumeManager;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.source.SQLiteVolumeManipulator;
import com.pinecone.hydra.storage.volume.source.VolumeMasterManipulator;
import com.pinecone.ulf.util.id.GUIDs;

import java.sql.ResultSet;
import java.sql.SQLException;

public class KenVolumeFileSystem implements OnVolumeFileSystem {

    private VolumeManager               volumeManager;
    private VolumeMasterManipulator     volumeMasterManipulator;
    private SQLiteVolumeManipulator     sqLiteVolumeManipulator;

    public KenVolumeFileSystem( VolumeManager volumeManager ){
        this.volumeManager           = volumeManager;
        this.volumeMasterManipulator = volumeManager.getMasterManipulator();
        this.sqLiteVolumeManipulator = this.volumeMasterManipulator.getSQLiteVolumeManipulator();
    }

    @Override
    public GUID getKVFSPhysicsVolume(GUID volumeGuid) {
        return this.sqLiteVolumeManipulator.getPhysicsGuid(volumeGuid);
    }

    @Override
    public void insertSimpleTargetMappingTab( GUID physicsGuid, GUID volumeGuid ) {
        this.sqLiteVolumeManipulator.insert( physicsGuid, volumeGuid );
    }

    @Override
    public void createSimpleTargetMappingTab( MappedExecutor mappedExecutor ) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `kvfs_simple_target_mapping`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `storage_object_guid` VARCHAR(36) , `storage_object_name` VARCHAR(36), `source_name` VARCHAR(330) );", false );
    }

    @Override
    public void insertSimpleTargetMappingSoloRecord( GUID storageObjectGuid, String storageObjectName, String sourceName, MappedExecutor mappedExecutor ) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `kvfs_simple_target_mapping` ( `storage_object_guid` , `storage_object_name` , `source_name` ) VALUES ( '"+ storageObjectGuid+ "', '"+storageObjectName+"', '"+sourceName+"' )", false );
    }

    @Override
    public String getSimpleStorageObjectSourceName(GUID storageObjectGuid, MappedExecutor mappedExecutor) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `source_name` FROM `kvfs_simple_target_mapping` WHERE `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            return resultSet.getString("source_name");
        }
        return null;
    }

    @Override
    public boolean existStorageObject(MappedExecutor mappedExecutor, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query(" SELECT COUNT(*) FROM `kvfs_simple_target_mapping` WHERE `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            int count = resultSet.getInt(1);
            return count != 0;
        }
        return false;
    }

    @Override
    public int hashStorageObjectID( GUID keyGuid, int volumeNum ) {
        int hash = (keyGuid.hashCode() ^ 137) % volumeNum; // TODO! CONST
        hash = (hash ^ (hash >> 31)) - (hash >> 31);
        return hash;
    }

    @Override
    public void createSpannedIndexTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `kvfs_span_volume_index`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `hash_key` int , `target_volume_guid` VARCHAR(36));", false );
    }

    @Override
    public void insertSpannedIndexTable(MappedExecutor mappedExecutor, int hashKey, GUID targetVolumeGuid) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `kvfs_span_volume_index` ( `hash_key`, `target_volume_guid` ) VALUES ( "+hashKey+", '"+targetVolumeGuid+"' )", false );
    }

    @Override
    public GUID getSpannedIndexTableTargetGuid(MappedExecutor mappedExecutor, int hashKey) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `target_volume_guid` FROM `kvfs_span_volume_index` WHERE `hash_key` = " + hashKey + " ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            String targetVolumeGuid = resultSet.getString("target_volume_guid");
            return GUIDs.GUID72( targetVolumeGuid );
        }
        return null;
    }

    @Override
    public void creatSpanLinkedVolumeTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `kvfs_span_linked_volume`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `hash_key` int , `key_guid` VARCHAR(36), `target_volume_guid` VARCHAR(36)) ;", false );
    }

    @Override
    public void insertSpanLinkedVolumeTable(MappedExecutor mappedExecutor, int hashKey, GUID keyGuid, GUID targetVolumeGuid) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `kvfs_span_linked_volume` ( `hash_key`, `key_guid`, `target_volume_guid` ) VALUES ( "+hashKey+", '"+keyGuid+"', '"+targetVolumeGuid+"' )", false );
    }

    @Override
    public GUID getSpanLinkedVolumeTableTargetGuid(MappedExecutor mappedExecutor, GUID keyGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `target_volume_guid` FROM `collision_table` WHERE `key_guid` = '" + keyGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            String targetVolumeGuid = resultSet.getString("target_volume_guid");
            return GUIDs.GUID72( targetVolumeGuid );
        }
        return null;
    }

    @Override
    public void createStripMetaTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `kvfs_strip_meta`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `code` int , `volume_guid` VARCHAR(36), `storage_object_guid` VARCHAR(36), `source_name` TEXT) ;", false );
    }

    @Override
    public void insertStripMetaTable(MappedExecutor mappedExecutor, int code, GUID volumeGuid, GUID storageObjectGuid, String sourceName) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `kvfs_strip_meta` ( `code`, `volume_guid`, `storage_object_guid`, `source_name` ) VALUES ( "+code+", '"+volumeGuid+"', '"+storageObjectGuid+"', '"+sourceName+"' )", false );
    }

    @Override
    public String getStripMetaSourceName(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `source_name` FROM `kvfs_strip_meta` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            return resultSet.getString("source_name");
        }
        return null;
    }

    @Override
    public int getStripMetaCode(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `code` FROM `kvfs_strip_meta` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            return resultSet.getInt("code");
        }
        return 0;
    }

    @Override
    public boolean isExistStripMetaTable(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT COUNT(*) FROM `kvfs_strip_meta` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            int count = resultSet.getInt(1);
            return count != 0;
        }
        return false;
    }
}
