package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.source.SQLiteVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@IbatisDataAccessObject
public interface SQLiteVolumeMapper extends SQLiteVolumeManipulator {
    @Insert("INSERT INTO `hydra_volume_sqlite_volume` (`physics_volume_guid`, `volume_guid`) VALUES ( #{physicsGuid}, #{volumeGuid} )")
    void insert(@Param("physicsGuid") GUID physicsGuid, @Param("volumeGuid") GUID volumeGuid );

    @Select("SELECT `physics_volume_guid` FROM `hydra_volume_sqlite_volume` WHERE `volume_guid` = #{volumeGuid}")
    GUID getPhysicsGuid( GUID volumeGuid );
}
