package com.pinecone.hydra.registry.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.framework.util.uoi.UOI;
import com.pinecone.hydra.registry.DistributedRegistry;
import com.pinecone.hydra.registry.entity.ConfigNode;
import com.pinecone.hydra.registry.entity.ArchConfigNode;
import com.pinecone.hydra.registry.entity.GenericPropertiesNode;
import com.pinecone.hydra.registry.entity.GenericProperty;
import com.pinecone.hydra.registry.entity.GenericTextConfigNode;
import com.pinecone.hydra.registry.entity.PropertiesNode;
import com.pinecone.hydra.registry.entity.Property;
import com.pinecone.hydra.registry.entity.TextConfigNode;
import com.pinecone.hydra.registry.entity.TextValue;
import com.pinecone.hydra.registry.source.RegistryNodeManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.awt.image.RasterFormatException;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface RegistryNodeMapper extends RegistryNodeManipulator {
    @Insert("INSERT INTO `hydra_registry_config_node` (`guid`, `parent_guid`, `create_time`, `update_time`,`name`) VALUES (#{guid},#{parentGuid},#{createTime},#{updateTime},#{name})")
    void insert(ConfigNode configNode);

    @Delete("DELETE FROM `hydra_registry_config_node` WHERE `guid`=#{guid}")
    void remove(@Param("guid") GUID guid);

//    @Select("SELECT `id`, `guid` , `parent_guid` AS parentGuid, `create_time` AS createTime, `update_time` AS updateTime, name FROM `hydra_registry_config_node` WHERE `guid`=#{guid}")
//    ArchConfigNode getConfigurationNode( GUID guid );
    @Select("SELECT `type` FROM `hydra_registry_nodes` WHERE `guid`=#{guid}")
    UOI getUOIByGUID( GUID guid );

    @Select("SELECT `id`, `guid`, `parent_guid` AS parentGuid, `create_time` AS createTime, `update_time` updateTime, `name` FROM `hydra_registry_config_node` WHERE `guid` = #{guid}")
    GenericPropertiesNode getPropertiesNode(GUID guid);

    @Select("SELECT `id`, `guid`, `parent_guid` AS parentGuid, `create_time` AS createTime, `update_time` updateTime, `name` FROM `hydra_registry_config_node` WHERE `guid`=#{guid}")
    GenericTextConfigNode getTextConfigNode(GUID guid);

    default ConfigNode getConfigurationNode ( GUID guid ) {
        String objectName = this.getUOIByGUID(guid).getObjectName();
        if ( objectName.equals(GenericTextConfigNode.class.getName()) ){
            return this.getTextConfigNode(guid);
        }
        else if ( objectName.equals(GenericPropertiesNode.class.getName()) ){
            return this.getPropertiesNode(guid);
        }
        return null;
    }


    default void update( ConfigNode configNode){
        if (configNode.getUpdateTime() != null){
            this.updateUpdateTime(configNode.getUpdateTime(),configNode.getGuid());
        }
        if (configNode.getName() != null){
            //updateName(configNode.getName(),configNode.getGuid());
        }
    }

    @Select("SELECT `guid` FROM `hydra_registry_config_node` WHERE `name`=#{name}")
    List<GUID> getNodeByName(String name);

    @Update("UPDATE `hydra_registry_config_node` SET `update_time` = #{updateTime} WHERE `guid` = #{guid}")
    void updateUpdateTime(@Param("updateTime") LocalDateTime updateTime,@Param("guid") GUID guid);

    @Select("SELECT `guid` FROM `hydra_registry_config_node`")
    List<GUID> getALL();

    @Update("UPDATE `hydra_registry_config_node` SET `name` = #{name} WHERE `guid` = #{guid}")
    void updateName(@Param("guid") GUID guid ,@Param("name") String name);
}
