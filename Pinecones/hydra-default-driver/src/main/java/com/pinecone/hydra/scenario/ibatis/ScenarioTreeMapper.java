package com.pinecone.hydra.scenario.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.unit.udtt.GUIDDistributedTrieNode;
import com.pinecone.hydra.unit.udtt.source.TrieTreeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface ScenarioTreeMapper extends TrieTreeManipulator {
    @Insert("INSERT INTO hydra_scenario_node_map (guid, type, base_data_guid, node_meta_guid) VALUES (#{guid},#{type},#{baseDataGUID},#{nodeMetadataGUID})")
    void insert (GUIDDistributedTrieNode distributedConfTreeNode);

    @Select("SELECT COUNT( `id` ) FROM hydra_scenario_node_map WHERE guid=#{guid}")
    boolean contains( GUID key );

    default GUIDDistributedTrieNode getNode(GUID guid){
        GUIDDistributedTrieNode metaNode = this.getMetaNode(guid);
        List<GUID> parentNodes = this.fetchParentGuids(guid);
        if (parentNodes != null){
            metaNode.setParentGUID(parentNodes);
        }else {
            metaNode.setParentGUID(new ArrayList<GUID>());
        }
        return metaNode;
    }

    @Select("SELECT id, guid, type, base_data_guid AS baseDataGUID, node_meta_guid AS nodeMetadataGUID FROM hydra_scenario_node_map WHERE guid=#{guid}")
    GUIDDistributedTrieNode getMetaNode(GUID guid);

    default void remove(GUID guid){
        removeMeta(guid);
        removeParentNode(guid);
    }

    @Delete("DELETE FROM hydra_scenario_node_map WHERE guid=#{guid}")
    void removeMeta(GUID guid);

    @Delete("DELETE FROM hydra_scenario_node_tree WHERE guid=#{guid}")
    void removeParentNode(GUID guid);

    @Delete("DELETE FROM `hydra_scenario_node_tree` WHERE `guid`=#{childGuid} AND `parent_guid`=#{parentGuid}")
    void removeInheritance(@Param("childGuid") GUID childGuid, @Param("parentGuid") GUID parentGuid);

    @Select("SELECT `parent_guid` FROM `hydra_scenario_node_tree` WHERE `guid`=#{guid}")
    List<GUID> fetchParentGuids(GUID guid);

    @Select("SELECT `path` FROM `hydra_scenario_node_path` WHERE `guid`=#{guid}")
    String getPath(GUID guid);

    void updatePath( GUID guid, String path);

    @Select("SELECT `guid` FROM `hydra_scenario_node_path` WHERE `path`=#{path}")
    GUID queryGUIDByPath( String path );

    void insertOwnedNode(GUID nodeGUID,GUID parentGUID);
    @Select("SELECT guid FROM hydra_scenario_node_tree WHERE parent_guid=#{guid}")

    List<GUIDDistributedTrieNode> getChild(GUID guid);
    @Delete("DELETE FROM `hydra_scenario_node_path` WHERE `guid`=#{guid}")
    void removePath(GUID guid);

}
