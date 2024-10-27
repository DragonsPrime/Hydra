package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;

@IbatisDataAccessObject
public interface StripedVolumeMapper extends StripedVolumeManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`, `definition_capacity`, `used_size`, `quota_capacity`, `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{definitionCapacity}, #{usedSize}, #{quotaCapacity}, #{type}, #{extConfig} )")
    void insert( StripedVolume stripedVolume );
    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );
    default TitanLocalStripedVolume getStripedVolume(GUID guid){
        TitanLocalStripedVolume stripedVolume0 = this.getStripedVolume0( guid );
        stripedVolume0.setStripedVolumeManipulator( this );
        return stripedVolume0;
    }

    TitanLocalStripedVolume getStripedVolume0(GUID guid);
}
