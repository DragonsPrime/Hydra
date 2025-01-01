package com.pinecone.hydra.version.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.version.source.VersionManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface VersionMapper extends VersionManipulator {
    @Insert("INSERT INTO `hydra_uofs_version` (`version`, `target_storage_object_guid`, `file_guid`) VALUES (#{version}, #{targetStorageObjectGuid}, #{fileGuid})")
    void insertObjectVersion(@Param("version") String version, @Param("targetStorageObjectGuid") GUID target_storage_object_guid, @Param("fileGuid") String fileGuid);

    @Delete("DELETE FROM `hydra_uofs_version` WHERE `version` = #{version} AND `file_guid` = #{fileGuid}")
    void removeObjectVersion( @Param("version") String version, @Param("fileGuid") String fileGuid );

    @Select("SELECT `target_storage_object_guid` FROM `hydra_uofs_version` WHERE `version` = #{version} AND file_guid = #{fileGuid}")
    GUID queryObjectGuid( @Param("version") String version, @Param("fileGuid") String fileGuid );
}
