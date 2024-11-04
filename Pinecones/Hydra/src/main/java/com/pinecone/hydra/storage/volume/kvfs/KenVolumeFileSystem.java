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
    public void insertKVFSDatabaseMeta(GUID physicsGuid, GUID volumeGuid) {
        this.sqLiteVolumeManipulator.insert( physicsGuid, volumeGuid );
    }

    @Override
    public void createKVFSDatabase(MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "CREATE TABLE `table`( `id` INTEGER PRIMARY KEY AUTOINCREMENT, `storage_object_guid` VARCHAR(36) , `storage_object_guid` VARCHAR(36));", false );
    }

    @Override
    public void insertKVFSDatabaseMeta(GUID storageObjectGuid, String storageObjectName, MappedExecutor mappedExecutor) throws SQLException {
        mappedExecutor.execute( "INSERT INTO `table` ( `storage_object_guid` , `storage_object_name` ) VALUES ( '"+ storageObjectGuid+ "', `"+storageObjectName+"` )", false );
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
         return keyGuid.hashCode() ^ 137 % volumeNum ;
    }
}
