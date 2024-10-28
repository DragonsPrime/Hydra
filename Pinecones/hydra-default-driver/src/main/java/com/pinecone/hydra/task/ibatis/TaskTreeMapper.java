package com.pinecone.hydra.task.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
@IbatisDataAccessObject
public interface TaskTreeMapper extends TrieTreeManipulator {
    @Insert("INSERT INTO `hydra_task_node_map` (`guid`, `type`, `base_data_guid`, `node_meta_guid`) VALUES (#{guid},#{type},#{baseDataGUID},#{nodeMetadataGUID})")
    void insert(GUIDDistributedTrieNode node);

    default GUIDDistributedTrieNode getNode(GUID guid){
        GUIDDistributedTrieNode nodeMeta = this.getNodeMeta(guid);
        List<GUID> parentNodes = this.fetchParentGuids(guid);
        nodeMeta.setParentGUID(parentNodes);
        return nodeMeta;
    }

    @Select("SELECT `id` AS `enumId`, `guid`, `type`, `base_data_guid` AS baseDataGuid, `node_meta_guid` AS nodeMetadataGuid FROM `hydra_task_node_map` WHERE `guid`=#{guid}")
    GUIDDistributedTrieNode getNodeMeta(GUID guid);

    default void remove(GUID guid){
        this.removeNode(guid);
        this.removeParent(guid);
        this.removePath(guid);
    }

    @Delete("DELETE FROM `hydra_task_node_map` WHERE `guid`=#{guid}")
    void removeNode(GUID guid);

    @Delete("DELETE FROM `hydra_task_node_tree` WHERE `guid`=#{guid}")
    void removeParent(GUID guid);

    @Select("SELECT `path` FROM `hydra_task_node_path` WHERE `guid`=#{guid}")
    String getPath(GUID guid);

    void updatePath( GUID guid, String path);

    @Select("SELECT `guid` FROM `hydra_task_node_path` WHERE `path`=#{path}")
    GUID queryGUIDByPath(String path);

    @Insert("INSERT INTO `hydra_task_node_tree` (`guid`, `parent_guid`) VALUES (#{nodeGuid},#{parentGuid})")
    void insertOwnedNode(@Param("nodeGuid") GUID nodeGUID, @Param("parentGuid") GUID parentGUID);

    @Select("SELECT `guid` FROM `hydra_task_node_tree` WHERE `parent_guid`=#{guid}")
    List<GUIDDistributedTrieNode> getChild(GUID guid);

    @Delete("DELETE FROM `hydra_task_node_path` WHERE `guid`=#{guid}")
    void removePath(GUID guid);

    void putNode(GUID guid, GUIDDistributedTrieNode distributedTreeNode);

    long size();

    @Select("SELECT `parent_guid` FROM `hydra_task_node_tree` WHERE `guid`=#{guid}")
    List<GUID> fetchParentGuids(GUID guid);

    @Delete("DELETE FROM `hydra_task_node_tree` where `guid`=#{childNode} AND `parent_guid`=#{parentGuid}")
    void removeInheritance(@Param("childNode") GUID childNode, @Param("parentGuid") GUID parentGUID);
}
