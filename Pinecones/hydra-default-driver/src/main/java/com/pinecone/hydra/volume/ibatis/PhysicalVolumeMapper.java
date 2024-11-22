package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.Debug;
import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.PhysicalVolume;
import com.pinecone.hydra.storage.volume.entity.local.physical.TitanLocalPhysicalVolume;
import com.pinecone.hydra.storage.volume.source.PhysicalVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

class GG {
    public static int id = 0;
}

@IbatisDataAccessObject
public interface PhysicalVolumeMapper extends PhysicalVolumeManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`, `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{type}, #{extConfig} )")
    void insert( PhysicalVolume physicalVolume );

    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );

    @Override
    default TitanLocalPhysicalVolume getPhysicalVolume(GUID guid){
        TitanLocalPhysicalVolume physicalVolume0 = this.getPhysicalVolume0( guid );
        physicalVolume0.setPhysicalVolumeManipulator( this );
        return physicalVolume0;
    }

    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `guid` = #{guid}")
    TitanLocalPhysicalVolume getPhysicalVolume0(GUID guid);

    @Override
    default TitanLocalPhysicalVolume getPhysicalVolumeByName( String name ){
        TitanLocalPhysicalVolume physicalVolumeByName0 = this.getPhysicalVolumeByName0(name);
        physicalVolumeByName0.setPhysicalVolumeManipulator( this );
        return physicalVolumeByName0;
    }

    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `name` = #{name}")
    TitanLocalPhysicalVolume getPhysicalVolumeByName0( String name );
    default TitanLocalPhysicalVolume getSmallestCapacityPhysicalVolume(){
        TitanLocalPhysicalVolume physicalVolume0 = this.getSmallestCapacityPhysicalVolume0();
        physicalVolume0.setPhysicalVolumeManipulator( this );
        return physicalVolume0;
    }

    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` ORDER BY ( `definition_capacity` - hydra_uofs_volumes.`used_size` ) ASC LIMIT 1")
    TitanLocalPhysicalVolume getSmallestCapacityPhysicalVolume0();

    @Select("SELECT `logic_guid` FROM `hydra_volume_physical_logic` WHERE `physical_guid` = #{guid}")
    GUID getParent( GUID guid );
}
