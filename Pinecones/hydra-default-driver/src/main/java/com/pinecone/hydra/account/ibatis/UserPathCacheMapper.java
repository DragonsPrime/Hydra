package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.source.TriePathCacheManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@IbatisDataAccessObject
public interface UserPathCacheMapper extends TriePathCacheManipulator {
    @Insert("INSERT INTO `hydra_account_node_cache_path` (`path`, `guid`) VALUES ( #{path}, #{guid} )")
    void insert(@Param("guid") GUID guid, @Param("path") String path );

    @Delete("DELETE FROM `hydra_account_node_cache_path` WHERE `guid`=#{guid}")
    void remove( GUID guid );

    @Select("SELECT `path` FROM `hydra_account_node_cache_path` WHERE `guid`=#{guid}")
    String getPath( GUID guid );

    @Select("SELECT `guid` FROM `hydra_account_node_cache_path` WHERE `guid`=#{guid}")
    GUID getNode( String path );

    @Select("SELECT `guid` FROM `hydra_account_node_cache_path` WHERE `path`=#{path}")
    GUID queryGUIDByPath( String path );
}
