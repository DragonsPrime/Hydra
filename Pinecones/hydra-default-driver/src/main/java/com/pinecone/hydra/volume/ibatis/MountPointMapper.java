package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.MountPoint;
import com.pinecone.hydra.storage.volume.entity.TitanMountPoint;
import com.pinecone.hydra.storage.volume.source.MountPointManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
@Mapper
public interface MountPointMapper extends MountPointManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes_mount_point` (`guid`, `volume_guid`, `create_time`, `update_time`, `name`, `mount_point`) VALUES (#{guid},#{volumeGuid},#{createTime},#{updateTime},#{name},#{mountPoint})")
    void insert( MountPoint mountPoint );
    @Delete("DELETE FROM `hydra_uofs_volumes_mount_point` WHERE `guid` = #{guid}")
    void remove( GUID guid );
    @Delete("DELETE FROM `hydra_uofs_volumes_mount_point` WHERE `volume_guid` = #{guid}")
    void removeByVolumeGuid( GUID guid );
    default TitanMountPoint getMountPoint(GUID guid){
        TitanMountPoint mountPoint0 = this.getMountPoint0( guid );
        if ( mountPoint0 == null ){
            return null;
        }
        mountPoint0.setMountPointManipulator( this );
        return mountPoint0;
    }
    @Select("SELECT `id` AS enumId, `guid`, `volume_guid` AS volumeGuid, `create_time` AS createTime, `update_time` AS updateTime, `name`, `mount_point` AS mountPoint FROM `hydra_uofs_volumes_mount_point` WHERE `guid` = #{guid}")
    TitanMountPoint getMountPoint0(GUID guid);

    default TitanMountPoint getMountPointByVolumeGuid( GUID guid ){
        TitanMountPoint mountPoint = this.getMountPointByVolumeGuid0(guid);
        if ( mountPoint == null ){
            return null;
        }
        mountPoint.setMountPointManipulator( this );
        return mountPoint;
    }
    @Select("SELECT `id` AS enumId, `guid`, `volume_guid` AS volumeGuid, `create_time` AS createTime, `update_time` AS updateTime, `name`, `mount_point` AS mountPoint FROM `hydra_uofs_volumes_mount_point` WHERE `volume_guid` = #{guid}")
    TitanMountPoint getMountPointByVolumeGuid0( GUID guid );
}
