package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.service.kom.entity.ServiceElement;
import com.pinecone.hydra.service.kom.source.ServiceMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
@IbatisDataAccessObject
public interface ServiceMetaMapper extends ServiceMetaManipulator {
    @Insert("INSERT INTO `hydra_service_serv_node_meta` (`guid`, `name`, `path`, `type`, `alias`, resource_type, service_type, create_time, update_time) VALUES (#{metaGuid},#{name},#{path},#{type},#{alias},#{resourceType},#{serviceType},#{createTime},#{updateTime})")
    void insert( ServiceElement serviceElement );

    @Delete("DELETE FROM `hydra_service_serv_node_meta` WHERE `guid`=#{guid}")
    void remove( @Param("guid") GUID guid );


    @Select("SELECT `id` AS `enumId`, `guid`, `name`, `path`, `type`, `alias`, `resource_type` AS resourceType, `service_type` AS serviceType, `create_time` AS createTime, `update_time` AS updateTime FROM `hydra_service_serv_node_meta` WHERE `guid`=#{guid}")
    GenericServiceElement getServiceMeta( @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `name` =#{name}, `path` = #{path}, `type` = #{type}, `alias` = #{alias}, `resource_type` = #{resourceType}, `service_type` = #{serviceType}, `update_time` = #{updateTime} WHERE `guid` = #{guid}")
    void update( ServiceElement serviceElement );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `name` = #{name} WHERE `guid` = #{guid}")
    void updateName( @Param("name") String name, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `path` = #{path} WHERE `guid` = #{guid}")
    void updatePath( @Param("path") String path, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `alias` = #{alias} WHERE `guid` = #{guid}")
    void updateAlias( @Param("alias") String alias, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `resource_type` = #{resourceType} WHERE `guid` = #{guid}")
    void updateResourceType( @Param("resourceType") String resourceType, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `service_type` = #{serviceType} WHERE `guid` = #{guid}")
    void updateServiceType( @Param("serviceType") String serviceType, @Param("guid") GUID guid );
    @Update("UPDATE `hydra_service_serv_node_meta` SET `update_time` = #{updateTime} WHERE `guid` = #{guid}")
    void updateUpdateTime( @Param("updateTime") LocalDateTime updateTime, @Param("guid") GUID guid );

}
