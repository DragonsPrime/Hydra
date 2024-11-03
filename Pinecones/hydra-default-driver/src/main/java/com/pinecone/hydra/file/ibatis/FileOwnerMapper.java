package com.pinecone.hydra.file.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.LinkedType;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
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
public interface FileOwnerMapper extends TireOwnerManipulator {
    @Insert("INSERT INTO `hydra_uofs_node_tree` (`guid`, `linked_type`) VALUES ( #{guid}, #{linkedType} )")
    void insertRootNode(@Param("guid")  GUID guid, @Param("linkedType") LinkedType linkedType );

    @Insert( "INSERT INTO `hydra_uofs_node_tree` (`guid`, `parent_guid`,`linked_type`) VALUES (#{targetGuid}, #{parentGuid}, #{linkedType})" )
    void insert( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID, @Param("linkedType") LinkedType linkedType );



    @Update( "UPDATE `hydra_uofs_node_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid}, `linked_type` = #{linkedType} WHERE `guid` = #{targetGuid}" )
    void update( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID, @Param("linkedType") LinkedType linkedType );

    @Update( "UPDATE `hydra_uofs_node_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid} WHERE `guid` = #{targetGuid}" )
    void updateParentGuid( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );

    @Update( "UPDATE `hydra_uofs_node_tree` SET `guid` = #{targetGuid}, `linked_type` = #{linkedType} WHERE `guid` = #{targetGuid}" )
    void updateLinkedType( @Param("targetGuid") GUID targetGuid, @Param("linkedType") LinkedType linkedType );



    @Delete( "DELETE FROM `hydra_uofs_node_tree` WHERE `guid`=#{subordinateGuid}  AND `linked_type` = 'Owned'" )
    void remove( @Param("subordinateGuid") GUID subordinateGuid, @Param("ownerGuid") GUID ownerGuid );

    @Delete( "DELETE FROM `hydra_uofs_node_tree` WHERE `guid`=#{subordinateGuid} AND `linked_type` = 'Owned'" )
    void removeBySubordinate( GUID subordinateGuid );

//    @Delete("DELETE FROM `hydra_registry_node_owner` WHERE `owner_guid`=#{ownerGuid}")
//    void removeByOwner(GUID ownerGuid);

    @Select( "SELECT `parent_guid` FROM `hydra_uofs_node_tree` WHERE `guid`=#{subordinateGuid} AND linked_type = 'Owned'" )
    GUID getOwner( GUID subordinateGuid );

    @Select( "SELECT guid FROM hydra_uofs_node_tree where parent_guid=#{guid} AND linked_type = 'Owned'" )
    List<GUID > getSubordinates( GUID guid );


    @Update("UPDATE `hydra_uofs_node_tree` SET `linked_type` = '#{linkedType}' WHERE `guid` = #{sourceGuid} AND `parent_guid` = #{targetGuid}")
    void setLinkedType( @Param("sourceGuid") GUID sourceGuid, @Param("targetGuid") GUID targetGuid, @Param("linkedType") LinkedType linkedType );

    @Select("SELECT `linked_type` FROM `hydra_uofs_node_tree` WHERE `guid` = #{childGuid} AND `parent_guid` =#{parentGuid}")
    LinkedType getLinkedType( @Param("childGuid") GUID childGuid,@Param("parentGuid") GUID parentGuid );
}
