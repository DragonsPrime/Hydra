package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.SimpleVolume;
import com.pinecone.hydra.storage.volume.entity.local.simple.TitanLocalSimpleVolume;
import com.pinecone.hydra.storage.volume.source.SimpleVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@IbatisDataAccessObject
public interface SimpleVolumeMapper extends SimpleVolumeManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`, `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{type}, #{extConfig} )")
    void insert( SimpleVolume simpleVolume );
    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );
    default TitanLocalSimpleVolume getSimpleVolume(GUID guid){
        TitanLocalSimpleVolume simpleVolume0 = this.getSimpleVolume0( guid );
        simpleVolume0.setSimpleVolumeManipulator( this );
        return simpleVolume0;
    }
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `guid` = #{guid}")
    TitanLocalSimpleVolume getSimpleVolume0(GUID guid);

    @Insert("INSERT INTO `hydra_volume_physical_logic` (`logic_guid`, `physical_guid`) VALUES ( #{logicGuid}, #{physicalGuid} )")
    void extendLogicalVolume( @Param("logicGuid") GUID logicGuid, @Param("physicalGuid") GUID physicalGuid );
    @Select("SELECT `physical_guid` FROM `hydra_volume_physical_logic` WHERE `logic_guid` = #{logicGuid}")
    List<GUID> lsblk(GUID logicGuid );
}
