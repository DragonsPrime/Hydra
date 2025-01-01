package com.pinecone.hydra.registry.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.imperium.source.TriePathCacheManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface RegistryNodePathCacheMapper extends TriePathCacheManipulator {
    @Insert("INSERT INTO `hydra_registry_node_path` (`path`, `guid`) VALUES ( #{path}, #{guid} )")
    void insert( @Param("guid") GUID guid, @Param("path") String path );

    @Delete("DELETE FROM `hydra_registry_node_path` WHERE `guid`=#{guid}")
    void remove( GUID guid );

    @Select("SELECT `path` FROM `hydra_registry_node_path` WHERE `guid`=#{guid}")
    String getPath( GUID guid );

    @Select("SELECT `guid` FROM `hydra_registry_node_path` WHERE `guid`=#{guid}")
    GUID getNode( String path );

    @Select("SELECT `guid` FROM `hydra_registry_node_path` WHERE `path`=#{path}")
    GUID queryGUIDByPath( String path );
}
