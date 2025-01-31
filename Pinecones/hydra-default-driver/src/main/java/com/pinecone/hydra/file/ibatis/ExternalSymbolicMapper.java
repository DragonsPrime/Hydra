package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.file.entity.ExternalSymbolic;
import com.pinecone.hydra.storage.file.entity.GenericExternalSymbolic;
import com.pinecone.hydra.storage.file.source.ExternalSymbolicManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@IbatisDataAccessObject
public interface ExternalSymbolicMapper extends ExternalSymbolicManipulator {
    @Insert("INSERT INTO hydra_uofs_directly_external_symbolic (`guid`, `create_time`, `update_time`, `name`, `reparsed_point`) VALUES (#{guid},#{createTime},#{updateTime},#{name},#{reparsedPoint})")
    void insert( ExternalSymbolic externalSymbolic );
    @Delete("DELETE FROM hydra_uofs_directly_external_symbolic WHERE `guid` = #{guid}")
    void remove( GUID guid );

    @Select("SELECT `id`, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`, `reparsed_point` AS reparsedPoint FROM hydra_uofs_directly_external_symbolic WHERE `guid` = #{guid}")
    GenericExternalSymbolic getSymbolicByGuid( GUID guid );

    @Select("SELECT `id`, `guid`, `create_time` AS createTime, `update_time` AS updateTime, `name`, `reparsed_point` AS reparsedPoint FROM hydra_uofs_directly_external_symbolic WHERE `guid` = #{guid} AND `name` = #{nodeName}")
    GenericExternalSymbolic getSymbolicByNameGuid(String nodeName, GUID guid );

    @Select("SELECT COUNT(*) FROM hydra_uofs_directly_external_symbolic WHERE `guid` = #{guid} AND `name` = #{nodeName}")
    boolean isSymbolicMatchedByNameGuid(String nodeName, GUID guid );
}
