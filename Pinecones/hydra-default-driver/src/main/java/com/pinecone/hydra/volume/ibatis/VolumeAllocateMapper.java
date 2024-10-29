package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.source.VolumeAllocateManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
    @IbatisDataAccessObject
    public interface VolumeAllocateMapper extends VolumeAllocateManipulator {
        @Insert("INSERT INTO `hydra_volume_allocate` (`object_guid`, `child_volume_guid`, `parent_volume_guid`) VALUES ( #{objectGuid},#{childVolumeGuid},#{parentVoluemGuid} )")
        void insert(@Param("objectGuid") GUID objectGuid, @Param("childVolumeGuid") GUID childVolumeGuid, @Param("parentVolumeGuid") GUID parentVolumeGuid );
        @Select("SELECT `child_volume_guid` FROM `hydra_volume_allocate` WHERE `object_guid` = #{objectGuid} AND `parent_volume_guid` = #{parentGuid}")
        GUID get( @Param("objectGuid") GUID objectGuid, @Param("parentGuid") GUID parentGuid );
    }

