package com.pinecone.hydra.policy.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.policy.source.VersionManipulator;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface VersionMapper extends VersionManipulator {
    @Insert("INSERT INTO `hydra_uofs_version` (`version`, `target_storage_object_guid`, `file_path`) VALUES (#{version}, #{targetStorageObjectGuid}, #{filePath})")
    void insertObjectVersion(@Param("version") String version, @Param("targetStorageObjectGuid") GUID target_storage_object_guid, @Param("filePath") String filePath);

    @Delete("DELETE FROM `hydra_uofs_version` WHERE `version` = #{version} AND `file_path` = #{filePath}")
    void removeObjectVersion( @Param("version") String version, @Param("filePath") String filePath );

    @Select("SELECT `target_storage_object_guid` FROM `hydra_uofs_version` WHERE `version` = #{version} AND file_path = #{filePath}")
    GUID queryObjectGuid( @Param("version") String version, @Param("filePath") String filePath );
}
