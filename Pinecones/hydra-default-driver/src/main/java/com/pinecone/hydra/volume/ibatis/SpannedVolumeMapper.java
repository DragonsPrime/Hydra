package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.SpannedVolume;
import com.pinecone.hydra.storage.volume.entity.local.spanned.TitanLocalSpannedVolume;
import com.pinecone.hydra.storage.volume.source.SpannedVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface SpannedVolumeMapper extends SpannedVolumeManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`,  `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{type}, #{extConfig} )")
    void insert( SpannedVolume spannedVolume );
    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );
    default TitanLocalSpannedVolume getSpannedVolume(GUID guid){
        TitanLocalSpannedVolume spannedVolume0 = this.getSpannedVolume0( guid );
        spannedVolume0.setSpannedVolumeManipulator( this );
        return spannedVolume0;
    }
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`, `definition_capacity` AS definitionCapacity, `used_size` AS userdSize, `quota_capacity` AS quotaCapacity, `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `guid` = #{guid}")
    TitanLocalSpannedVolume getSpannedVolume0(GUID guid);
}
