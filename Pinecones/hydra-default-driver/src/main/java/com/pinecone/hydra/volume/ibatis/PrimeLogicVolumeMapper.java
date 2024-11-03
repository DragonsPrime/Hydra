package com.pinecone.hydra.volume.ibatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.storage.volume.source.LogicVolumeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

@IbatisDataAccessObject
public interface PrimeLogicVolumeMapper extends LogicVolumeManipulator {
    @Select("SELECT `guid` FROM `hydra_uofs_volumes` WHERE `name` = #{name}")
    List<GUID> getGuidsByName( String name );

    @Select("SELECT `guid` FROM `hydra_uofs_volumes` WHERE `name` = #{name} AND `guid` = #{guid}")
    List<GUID > getGuidsByNameID( @Param("name") String name, @Param("guid") GUID guid );
}
