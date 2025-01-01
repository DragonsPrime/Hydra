package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.FileNode;
import com.pinecone.hydra.storage.file.entity.GenericFileNode;
import com.pinecone.hydra.storage.file.source.FileManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface FileMapper extends FileManipulator {
    FileNode getFileNode(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_files` (`guid`, `create_time`, `update_time`, `deleted_at`, `name`, `checksum`, `parity_check`, `physical_size`,`logic_size`,`definition_size`,`crc32_xor`,`integrity_check_enable`,`disable_cluster`) VALUES (#{guid},#{createTime},#{updateTime},#{deletedTime},#{name},#{checksum},#{parityCheck},#{physicalSize},#{logicSize},#{definitionSize},#{crc32Xor},#{integrityCheckEnable},#{disableCluster})")
    void insert( FileNode fileNode );
    @Delete("DELETE FROM `hydra_uofs_files` WHERE `guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `deleted_at` AS deletedTime, `name`, `checksum`, `parity_check` AS parityCheck, `physical_size` AS physicalSize,`logic_size` AS logicSize,`definition_size` AS definitionSize,`crc32_xor` AS crc32Xor,`integrity_check_enable` AS integrityCheckEnable,`disable_cluster` AS disableCluster FROM hydra_uofs_files WHERE `guid` = #{guid}")
    GenericFileNode getFileNodeByGuid(GUID guid);

    @Select("SELECT `guid` FROM `hydra_uofs_files` WHERE `name` = #{name}")
    List<GUID > getGuidsByName(String name );

    @Select("SELECT `guid` FROM `hydra_uofs_files` WHERE `name` = #{name} AND `guid` = #{guid}")
    List<GUID > getGuidsByNameID(@Param("name") String name, @Param("guid") GUID guid );

    @Select("SELECT `guid` FROM hydra_uofs_files ")
    List<GUID > dumpGuid();

    @Update("UPDATE hydra_uofs_files SET checksum = #{checksum}, parity_check = #{parityCheck}, physical_size = #{physicalSize}, logic_size = #{logicSize}, crc32_xor = #{crc32Xor}, definition_size = #{definitionSize} WHERE guid = #{guid}")
    void update( FileNode fileNode );
}
