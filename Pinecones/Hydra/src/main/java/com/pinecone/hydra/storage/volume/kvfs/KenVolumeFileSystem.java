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
    public void insertKVFSMateTable(GUID physicsGuid, GUID volumeGuid) {
        this.sqLiteVolumeManipulator.insert( physicsGuid, volumeGuid );
    }

    @Override
    public void createKVFSMetaTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `storage_object_guid` VARCHAR(36) , `storage_object_name` VARCHAR(36), `source_name` VARCHAR(330) );", false );
    }

    @Override
    public void insertKVFSTable(GUID storageObjectGuid, String storageObjectName, String sourceName, MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `table` ( `storage_object_guid` , `storage_object_name` , `source_name` ) VALUES ( '"+ storageObjectGuid+ "', '"+storageObjectName+"', '"+sourceName+"' )", false );
    }

    @Override
    public String getKVFSTableSourceName(GUID storageObjectGuid, MappedExecutor mappedExecutor) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `source_name` FROM `table` WHERE `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            return resultSet.getString("source_name");
        }
        return null;
    }

    @Override
    public boolean existStorageObject(MappedExecutor mappedExecutor, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query(" SELECT COUNT(*) FROM `table` WHERE `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            int count = resultSet.getInt(1);
            return count != 0;
        }
        return false;
    }

    @Override
    public int KVFSHash(GUID keyGuid, int volumeNum) {
        int hash = (keyGuid.hashCode() ^ 137) % volumeNum;
        hash = (hash ^ (hash >> 31)) - (hash >> 31);
        return hash;
    }

    @Override
    public void creatKVFSIndexTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `index_table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `hash_key` int , `target_volume_guid` VARCHAR(36));", false );
    }

    @Override
    public void insertKVFSIndexTable(MappedExecutor mappedExecutor, int hashKey, GUID targetVolumeGuid) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `index_table` ( `hash_key`, `target_volume_guid` ) VALUES ( "+hashKey+", '"+targetVolumeGuid+"' )", false );
    }

    @Override
    public GUID getKVFSIndexTableTargetGuid(MappedExecutor mappedExecutor, int hashKey) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `target_volume_guid` FROM `index_table` WHERE `hash_key` = " + hashKey + " ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            String targetVolumeGuid = resultSet.getString("target_volume_guid");
            return GUIDs.GUID72( targetVolumeGuid );
        }
        return null;
    }

    @Override
    public void creatKVFSCollisionTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `collision_table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `hash_key` int , `key_guid` VARCHAR(36), `target_volume_guid` VARCHAR(36)) ;", false );
    }

    @Override
    public void insertKVFSCollisionTable(MappedExecutor mappedExecutor, int hashKey, GUID keyGuid, GUID targetVolumeGuid) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `collision_table` ( `hash_key`, `key_guid`, `target_volume_guid` ) VALUES ( "+hashKey+", '"+keyGuid+"', '"+targetVolumeGuid+"' )", false );
    }

    @Override
    public GUID getKVFSCollisionTableTargetGuid(MappedExecutor mappedExecutor, GUID keyGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `target_volume_guid` FROM `collision_table` WHERE `key_guid` = '" + keyGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            String targetVolumeGuid = resultSet.getString("target_volume_guid");
            return GUIDs.GUID72( targetVolumeGuid );
        }
        return null;
    }

    @Override
    public void createKVFSFileStripTable(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `file_strip_table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `code` int , `volume_guid` VARCHAR(36), `storage_object_guid` VARCHAR(36), `source_name` TEXT) ;", false );
    }

    @Override
    public void insertKVFSFileStripTable(MappedExecutor mappedExecutor, int code, GUID volumeGuid, GUID storageObjectGuid, String sourceName) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `file_strip_table` ( `code`, `volume_guid`, `storage_object_guid`, `source_name` ) VALUES ( "+code+", '"+volumeGuid+"', '"+storageObjectGuid+"', '"+sourceName+"' )", false );
    }

    @Override
    public String getKVFSFileStripSourceName(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `source_name` FROM `file_strip_table` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            return resultSet.getString("source_name");
        }
        return null;
    }

    @Override
    public int getKVFSFileStripCode(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT `code` FROM `file_strip_table` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if ( resultSet.next() ){
            return resultSet.getInt("code");
        }
        return 0;
    }

    @Override
    public boolean isExistKVFSFileStripTable(MappedExecutor mappedExecutor, GUID volumeGuid, GUID storageObjectGuid) throws SQLException {
        ResultSession query = mappedExecutor.query("SELECT COUNT(*) FROM `file_strip_table` WHERE `volume_guid` = '" + volumeGuid + "' AND `storage_object_guid` = '" + storageObjectGuid + "' ");
        ResultSet resultSet = query.getResultSet();
        if( resultSet.next() ){
            int count = resultSet.getInt(1);
            return count != 0;
        }
        return false;
    }
}
