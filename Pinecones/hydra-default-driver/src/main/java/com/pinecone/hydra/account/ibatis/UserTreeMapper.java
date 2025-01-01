package com.pinecone.hydra.account.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.unit.imperium.GUIDImperialTrieNode;
import com.pinecone.hydra.unit.imperium.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.imperium.source.TrieTreeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
@IbatisDataAccessObject
public interface UserTreeMapper extends TrieTreeManipulator {
    @Insert("INSERT INTO `hydra_account_node_tree` (`guid`) VALUES ( #{guid} )")
    void insertRootNode(@Param("guid") GUID guid);

    @Override
    default void insert (TireOwnerManipulator ownerManipulator, GUIDImperialTrieNode node ){
        this.insertTreeNode( node.getGuid(), node.getType(), node.getAttributesGUID(), node.getNodeMetadataGUID() );
        ownerManipulator.insertRootNode( node.getGuid() );
    }

    @Insert("INSERT INTO `hydra_account_nodes` (`guid`, `type`,`base_data_guid`,`node_meta_guid`) VALUES (#{guid},#{type},#{baseDataGuid},#{nodeMetaGuid})")
    void insertTreeNode(@Param("guid") GUID guid, @Param("type") UOI type, @Param("baseDataGuid") GUID baseDataGuid, @Param("nodeMetaGuid") GUID nodeMetaGuid );

    @Select("SELECT `id` AS `enumId`, `guid`, `type`, base_data_guid AS baseDataGUID, node_meta_guid AS nodeMetadataGUID FROM hydra_account_nodes WHERE guid=#{guid}")
    GUIDImperialTrieNode getNodeExtendsFromMeta(GUID guid );

    @Select("SELECT COUNT( `id` ) FROM hydra_account_nodes WHERE guid=#{guid}")
    boolean contains( GUID key );

    @Override
    default GUIDImperialTrieNode getNode(GUID guid ) {
        GUIDImperialTrieNode node = this.getNodeExtendsFromMeta( guid );
        if( node == null ){
            return node;
        }
        List<GUID > parent = this.fetchParentGuids( guid );
        node.setParentGUID( parent );
        return node;
    }

    @Select("SELECT id, guid, parent_guid FROM hydra_account_node_tree WHERE guid = #{guid} AND parent_guid = #{parentGuid}")
    GUIDImperialTrieNode getTreeNodeOnly(@Param("guid") GUID guid, @Param("parentGuid") GUID parentGuid );

    @Select("SELECT count( * ) FROM hydra_account_node_tree WHERE guid = #{guid} AND parent_guid = #{parentGuid}")
    long countNode( GUID guid, GUID parentGuid );


    @Override
    default void purge( GUID guid ) {
        this.removeNodeMeta( guid );
        this.removeTreeNode( guid );
    }

    @Delete("DELETE FROM `hydra_account_nodes` WHERE `guid`=#{guid}")
    void removeNodeMeta( @Param("guid") GUID guid );

    @Delete("DELETE FROM `hydra_account_node_tree` WHERE `guid` = #{guid}")
    void removeTreeNode( @Param("guid") GUID guid );

    @Delete("DELETE FROM `hydra_account_node_tree` WHERE `parent_guid` = #{parent_guid}")
    void removeTreeNodeByParentGuid( @Param("parent_guid") GUID parentGuid );

    @Delete("DELETE FROM `hydra_account_node_tree` WHERE `guid` = #{guid} AND `parent_guid` = #{parent_guid}")
    void removeTreeNodeYoke( @Param("guid") GUID guid, @Param("parent_guid") GUID parentGuid );


    @Delete("DELETE FROM `hydra_account_node_tree` WHERE `guid`=#{chileGuid} AND `parent_guid`=#{parentGuid}")
    void removeInheritance( @Param("chileGuid") GUID childGuid, @Param("parentGuid") GUID parentGuid );

    @Select("SELECT `id` AS `enumId`, `guid`, `parent_guid` AS parentGuid FROM `hydra_account_node_tree` WHERE `parent_guid`=#{guid}")
    List<GUIDImperialTrieNode> getChildren(GUID guid );

    @Select("SELECT `guid` FROM `hydra_account_node_tree` WHERE `parent_guid` = #{parentGuid}")
    List<GUID > fetchChildrenGuids( @Param("parentGuid") GUID parentGuid );

    @Select("SELECT `parent_guid` FROM `hydra_account_node_tree` WHERE `guid`=#{guid}")
    List<GUID > fetchParentGuids( GUID guid );

    @Update("UPDATE `hydra_account_nodes` SET `type` = #{type} WHERE guid=#{guid}")
    void updateType( UOI type , GUID guid );

    @Select( "SELECT guid FROM hydra_account_node_tree WHERE parent_guid IS NULL " )
    List<GUID > fetchRoot();

    @Override
    @Select( "SELECT COUNT( `guid` ) FROM hydra_account_node_tree WHERE `parent_guid` IS NULL AND guid = #{guid}" )
    boolean isRoot( GUID guid );

    @Update("UPDATE hydra_account_node_tree SET parent_guid = #{parentGuid} WHERE guid = #{childGuid}")
    void addChild( @Param("childGuid") GUID childGuid, @Param("parentGuid") GUID parentGuid );
}
