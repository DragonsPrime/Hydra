package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.entity.StripedVolume;
import com.pinecone.hydra.storage.volume.entity.Volume;
import com.pinecone.hydra.storage.volume.entity.local.striped.TitanLocalStripedVolume;
import com.pinecone.hydra.storage.volume.source.StripedVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@IbatisDataAccessObject
public interface StripedVolumeMapper extends StripedVolumeManipulator, PrimeLogicVolumeMapper {
    @Insert("INSERT INTO `hydra_uofs_volumes` (`guid`, `create_time`, `update_time`, `name`, `type`, `ext_config`) VALUES ( #{guid}, #{createTime}, #{updateTime}, #{name}, #{type}, #{extConfig} )")
    void insert( StripedVolume stripedVolume );

    @Delete("DELETE FROM `hydra_uofs_volumes` where `guid` = #{guid}")
    void remove( GUID guid );

    @Override
    default TitanLocalStripedVolume getStripedVolume(GUID guid){
        TitanLocalStripedVolume stripedVolume0 = this.getStripedVolume0( guid );
        stripedVolume0.setStripedVolumeManipulator( this );
        return stripedVolume0;
    }
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE `guid` = #{guid}")
    TitanLocalStripedVolume getStripedVolume0(GUID guid);

    default List<Volume> queryAllStripedVolume(){
        List<TitanLocalStripedVolume> titanLocalStripedVolumes = this.queryAllStripedVolume0();
        return new ArrayList<>(titanLocalStripedVolumes);
    }
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`,  `type`, `ext_config` AS extConfig FROM `hydra_uofs_volumes` WHERE type = 'StripedVolume'")
    List<TitanLocalStripedVolume> queryAllStripedVolume0();
}
