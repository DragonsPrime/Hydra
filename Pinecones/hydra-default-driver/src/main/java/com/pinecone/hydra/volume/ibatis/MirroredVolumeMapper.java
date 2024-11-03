package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.MirroredVolume;
import com.pinecone.hydra.storage.volume.entity.local.mirrored.TitanLocalMirroredVolume;
import com.pinecone.hydra.storage.volume.source.MirroredVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface MirroredVolumeMapper extends MirroredVolumeManipulator, PrimeLogicVolumeMapper {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`, `definition_capacity`, `used_size`, `quota_capacity`, `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{definitionCapacity}, #{usedSize}, #{quotaCapacity}, #{type}, #{extConfig} )")
    void insert( MirroredVolume mirroredVolume );

    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );

    default TitanLocalMirroredVolume getMirroredVolume(GUID guid){
        TitanLocalMirroredVolume mirroredVolume0 = this.getMirroredVolume0(guid);
        mirroredVolume0.setMirroredVolumeManipulator( this );
        return mirroredVolume0;
    }

    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`, `definition_capacity` AS definitionCapacity, `used_size` AS userdSize, `quota_capacity` AS quotaCapacity, `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `guid` = #{guid}")
    TitanLocalMirroredVolume getMirroredVolume0(GUID guid);
}
