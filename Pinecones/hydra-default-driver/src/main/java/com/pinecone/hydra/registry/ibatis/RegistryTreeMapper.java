package com.pinecone.hydra.registry.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.LinkedType;
import com.pinecone.hydra.unit.udtt.entity.TreeReparseLinkNode;
import com.pinecone.hydra.unit.udtt.source.TireOwnerManipulator;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@IbatisDataAccessObject
public interface RegistryTreeMapper extends TrieTreeManipulator {
    @Insert("INSERT INTO `hydra_registry_node_tree` (`guid`, `linked_type`) VALUES ( #{guid}, #{linkedType} )")
    void insertRootNode( @Param("guid")  GUID guid, @Param("linkedType") LinkedType linkedType );

    @Override
    default void insert ( TireOwnerManipulator ownerManipulator, GUIDDistributedTrieNode node ){
        this.insertTreeNode( node.getGuid(), node.getType(), node.getAttributesGUID(), node.getNodeMetadataGUID() );
        ownerManipulator.insertRootNode( node.getGuid() );
    }

    @Insert("INSERT INTO hydra_registry_nodes (`guid`, `type`,`base_data_guid`,`node_meta_guid`) VALUES (#{guid},#{type},#{baseDataGuid},#{nodeMetaGuid})")
    void insertTreeNode( @Param("guid") GUID guid, @Param("type") UOI type, @Param("baseDataGuid") GUID baseDataGuid, @Param("nodeMetaGuid") GUID nodeMetaGuid );

    @Select("SELECT `id` AS `enumId`, `guid`, `type`, base_data_guid AS baseDataGUID, node_meta_guid AS nodeMetadataGUID FROM hydra_registry_nodes WHERE guid=#{guid}")
    GUIDDistributedTrieNode getNodeExtendsFromMeta( GUID guid );

    @Override
    default GUIDDistributedTrieNode getNode( GUID guid ) {
        GUIDDistributedTrieNode node = this.getNodeExtendsFromMeta( guid );
        List<GUID > parent = this.fetchParentGuids( guid );
        node.setParentGUID( parent );
        return node;
    }

    @Select("SELECT COUNT( `id` ) FROM hydra_registry_nodes WHERE guid=#{guid}")
    boolean contains( GUID key );

    @Select("SELECT id, guid, parent_guid, linked_type FROM hydra_registry_node_tree WHERE guid = #{guid} AND parent_guid = #{parentGuid}")
    GUIDDistributedTrieNode getTreeNodeOnly( @Param("guid") GUID guid, @Param("parentGuid") GUID parentGuid );

    @Select("SELECT count( * ) FROM hydra_registry_node_tree WHERE guid = #{guid} AND parent_guid = #{parentGuid}")
    long countNode( GUID guid, GUID parentGuid );



    @Override
    default void purge( GUID guid ) {
        this.removeNodeMeta( guid );
        this.removeTreeNode( guid );
        this.removeOwnedTreeNode( guid );
    }

    @Delete("DELETE FROM `hydra_registry_nodes` WHERE `guid`=#{guid}")
    void removeNodeMeta( @Param("guid") GUID guid );

    @Delete("DELETE FROM `hydra_registry_node_tree` WHERE `guid` = #{guid}")
    void removeTreeNode( @Param("guid") GUID guid );

    @Delete("DELETE FROM `hydra_registry_node_tree` WHERE `parent_guid` = #{parent_guid}")
    void removeTreeNodeByParentGuid( @Param("parent_guid") GUID parentGuid );

    @Delete("DELETE FROM `hydra_registry_node_tree` WHERE `guid` = #{guid} AND `parent_guid` = #{parent_guid}")
    void removeTreeNodeYoke( @Param("guid") GUID guid, @Param("parent_guid") GUID parentGuid );

    @Delete("DELETE FROM `hydra_registry_node_tree` WHERE `guid` = #{guid} AND `linked_type` = #{linkedType}")
    void removeTreeNodeWithLinkedType( @Param("guid") GUID guid, @Param("linkedType") LinkedType linkedType );




    @Delete("DELETE FROM `hydra_registry_node_tree` WHERE `guid`=#{chileGuid} AND `parent_guid`=#{parentGuid}")
    void removeInheritance( @Param("chileGuid") GUID childGuid, @Param("parentGuid") GUID parentGuid );

    @Select("SELECT `id` AS `enumId`, `guid`, `parent_guid` AS parentGuid FROM `hydra_registry_node_tree` WHERE `parent_guid`=#{guid}")
    List<GUIDDistributedTrieNode > getChildren( GUID guid );

    @Select("SELECT `guid` FROM `hydra_registry_node_tree` WHERE `parent_guid` = #{parentGuid}")
    List<GUID > fetchChildrenGuids( @Param("parentGuid") GUID parentGuid );

    @Select("SELECT `parent_guid` FROM `hydra_registry_node_tree` WHERE `guid`=#{guid}")
    List<GUID > fetchParentGuids( GUID guid );

    @Update("UPDATE `hydra_registry_nodes` SET `type` = #{type} WHERE guid=#{guid}")
    void updateType( UOI type , GUID guid );

    @Select( "SELECT guid FROM hydra_registry_node_tree WHERE parent_guid IS NULL " )
    List<GUID > fetchRoot();

    @Override
    @Select( "SELECT COUNT( `guid` ) FROM hydra_registry_node_tree WHERE `parent_guid` IS NULL AND guid = #{guid}" )
    boolean isRoot( GUID guid );




    @Override
    @Select( "SELECT COUNT( `guid` ) FROM hydra_registry_node_tree WHERE `guid` = #{guid} AND `linked_type` = #{linkedType}" )
    long queryLinkedCount( @Param("guid") GUID guid, @Param("linkedType") LinkedType linkedType );

    @Override
    @Select( "SELECT COUNT( `guid` ) FROM hydra_registry_node_tree WHERE `guid` = #{guid}" )
    long queryAllLinkedCount( @Param("guid") GUID guid );


    @Override
    @Insert(
            "INSERT INTO `hydra_registry_node_tree` (`guid`, `linked_type`,`tag_name`,`tag_guid`,`parent_guid`) " +
            "VALUES (#{originalGuid}, #{linkedType}, #{tagName}, #{tagGuid}, #{dirGuid})"
    )
    void newLinkTag(
            @Param("originalGuid") GUID originalGuid, @Param("dirGuid") GUID dirGuid,
            @Param("tagName") String tagName, @Param("tagGuid") GUID tagGuid, @Param("linkedType") LinkedType linkedType
    );

    @Override
    @Update( "UPDATE hydra_registry_node_tree SET tag_name = #{tagName} WHERE tag_guid =#{tagGuid}" )
    void updateLinkTagName( @Param("tagGuid") GUID tagGuid, @Param("tagName") String tagName );

    @Override
    @Select( "SELECT `guid` FROM hydra_registry_node_tree WHERE tag_name = #{tagName} AND parent_guid = #{dirGuid}" )
    GUID getOriginalGuid( @Param("tagName") String tagName, @Param("dirGuid") GUID dirGuid );

    @Override
    @Select( "SELECT `guid` FROM hydra_registry_node_tree WHERE tag_name = #{tagName} AND guid = #{nodeGuid}" )
    GUID getOriginalGuidByNodeGuid( @Param("tagName") String tagName, @Param("nodeGuid") GUID nodeGUID );

    @Override
    @Select( "SELECT `guid` AS targetNodeGuid, `parent_guid` AS parentNodeGuid, `linked_type` AS linkedType, `tag_name` AS tagName, `tag_guid` AS tagGuid FROM hydra_registry_node_tree WHERE tag_name = #{tagName} AND parent_guid = #{parentDirGuid}" )
    TreeReparseLinkNode getReparseLinkNode( @Param("tagName") String tagName, @Param("parentDirGuid") GUID parentDirGuid );

    @Override
    @Select( "SELECT `guid` AS targetNodeGuid, `parent_guid` AS parentNodeGuid, `linked_type` AS linkedType, `tag_name` AS tagName, `tag_guid` AS tagGuid FROM hydra_registry_node_tree WHERE tag_name = #{tagName} AND guid = #{nodeGuid}" )
    TreeReparseLinkNode getReparseLinkNodeByNodeGuid( @Param("tagName") String tagName, @Param("nodeGuid") GUID nodeGUID );

    @Override
    @Select( "SELECT `guid` FROM hydra_registry_node_tree WHERE `tag_name` = #{tagName}" )
    List<GUID > fetchOriginalGuid( String tagName );

    @Override
    @Select( "SELECT `guid` FROM hydra_registry_node_tree WHERE `tag_name` = #{tagName} AND `parent_guid` IS NULL" )
    List<GUID > fetchOriginalGuidRoot( String tagName );

    @Override
    @Select( "SELECT COUNT(*) FROM `hydra_registry_node_tree` WHERE `tag_guid` = #{guid}" )
    boolean isTagGuid(GUID guid);

    @Override
    @Delete( "DELETE FROM `hydra_registry_node_tree` WHERE `tag_guid` = #{guid}" )
    void removeReparseLink( GUID guid );

    @Override
    @Select( "SELECT `guid` FROM `hydra_registry_node_tree` WHERE `tag_guid` = #{tagGuid}" )
    GUID getOriginalGuidByTagGuid(GUID tagGuid);
}
