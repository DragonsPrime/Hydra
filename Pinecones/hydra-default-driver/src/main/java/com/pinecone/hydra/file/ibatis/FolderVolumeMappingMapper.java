package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.source.FolderVolumeMappingManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@IbatisDataAccessObject
public interface FolderVolumeMappingMapper extends FolderVolumeMappingManipulator {
    @Insert("INSERT INTO `hydra_uofs_file_volume_mapping` (`folder_guid`, `volume_guid`) VALUES (#{folderGuid}, #{volumeGuid})")
    void insert(@Param("folderGuid") GUID folderGuid, @Param("volumeGuid") GUID volumeGuid );

    @Delete("DELETE FROM `hydra_uofs_file_volume_mapping` WHERE `volume_guid` = #{volumeGuid} AND `folder_guid` = #{folderGuid}")
    void remove( @Param("folderGuid") GUID folderGuid, @Param("volumeGuid") GUID volumeGuid );

    @Select("SELECT `volume_guid` FROM `hydra_uofs_file_volume_mapping` WHERE `folder_guid` = #{folderGuid}")
    GUID getVolumeGuid( GUID folderGuid );
}
