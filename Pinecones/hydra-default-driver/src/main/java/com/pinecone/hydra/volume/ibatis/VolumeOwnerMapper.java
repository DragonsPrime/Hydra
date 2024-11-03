package com.pinecone.hydra.volume.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.LinkedType;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@IbatisDataAccessObject
public interface VolumeOwnerMapper extends TireOwnerManipulator {
    @Insert("INSERT INTO `hydra_uofs_volumes_tree` (`guid`) VALUES ( #{guid} )")
    void insertRootNode(@Param("guid") GUID guid );

    @Insert( "INSERT INTO `hydra_uofs_volumes_tree` (`guid`, `parent_guid`) VALUES (#{targetGuid}, #{parentGuid})" )
    void insert( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );


    @Update( "UPDATE `hydra_uofs_volumes_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid} WHERE `guid` = #{targetGuid}" )
    void update( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );

    @Update( "UPDATE `hydra_uofs_volumes_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid} WHERE `guid` = #{targetGuid}" )
    void updateParentGuid( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );

    @Delete( "DELETE FROM `hydra_uofs_volumes_tree` WHERE `guid`=#{subordinateGuid} " )
    void remove( @Param("subordinateGuid") GUID subordinateGuid );

    @Delete( "DELETE FROM `hydra_uofs_volumes_tree` WHERE `guid`=#{subordinateGuid} " )
    void removeBySubordinate( GUID subordinateGuid );

//    @Delete("DELETE FROM `hydra_registry_node_owner` WHERE `owner_guid`=#{ownerGuid}")
//    void removeByOwner(GUID ownerGuid);

}
