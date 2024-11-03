package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ElementNode;
import com.pinecone.hydra.storage.file.entity.Folder;
import com.pinecone.hydra.storage.file.entity.GenericFolder;
import com.pinecone.hydra.storage.file.source.FolderManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface FolderMapper extends FolderManipulator {
    Folder getFolder(GUID guid, ElementNode element);
    @Insert("INSERT INTO `hydra_uofs_folders` (`guid`, `create_time`, `update_time`, `name`) VALUES (#{guid},#{createTime},#{updateTime},#{name})")
    void insert( Folder folder );
    @Delete("DELETE FROM `hydra_uofs_folders` WHERE `guid` = #{guid}")
    void remove( GUID guid );
    @Select("SELECT `id` AS enumId, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name` FROM `hydra_uofs_folders` WHERE `guid` = #{guid}")
    GenericFolder getFolderByGuid(GUID guid);
    @Select("SELECT `guid` FROM `hydra_uofs_folders` WHERE `name` = #{name}")
    List<GUID > getGuidsByName(String name );
    @Select("SELECT `guid` FROM `hydra_uofs_folders` WHERE `name` = #{name} AND `guid` = #{guid}")
    List<GUID > getGuidsByNameID(@Param("name") String name, @Param("guid") GUID guid );

    @Select("SELECT `guid` FROM hydra_uofs_folders")
    List<GUID > dumpGuid();
    @Select("SELECT COUNT('id') FROM hydra_uofs_folders WHERE guid = #{guid}")
    boolean isFolder(GUID guid);
}
