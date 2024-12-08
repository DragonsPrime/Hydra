package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
@IbatisDataAccessObject
public interface UserOwnerMapper extends TireOwnerManipulator {
    @Insert("INSERT INTO `hydra_account_node_tree` (`guid`) VALUES ( #{guid} )")
    void insertRootNode(@Param("guid") GUID guid );

    @Insert( "INSERT INTO `hydra_account_node_tree` (`guid`, `parent_guid`) VALUES (#{targetGuid}, #{parentGuid})" )
    void insert( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );


    @Update( "UPDATE `hydra_account_node_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid} WHERE `guid` = #{targetGuid}" )
    void update( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );

    @Update( "UPDATE `hydra_account_node_tree` SET `guid` = #{targetGuid}, `parent_guid` = #{parentGuid} WHERE `guid` = #{targetGuid}" )
    void updateParentGuid( @Param("targetGuid") GUID targetGuid, @Param("parentGuid") GUID parentGUID );

    @Delete( "DELETE FROM `hydra_account_node_tree` WHERE `guid`=#{subordinateGuid} " )
    void remove( @Param("subordinateGuid") GUID subordinateGuid );

    @Delete( "DELETE FROM `hydra_account_node_tree` WHERE `guid`=#{subordinateGuid} " )
    void removeBySubordinate( GUID subordinateGuid );
}
