package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.ServicesInstrument;
import com.pinecone.hydra.service.kom.entity.ApplicationElement;
import com.pinecone.hydra.service.kom.entity.GenericApplicationElement;
import com.pinecone.hydra.service.kom.source.ApplicationMetaManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
@IbatisDataAccessObject
public interface AppNodeMetaMapper extends ApplicationMetaManipulator {
    @Insert( "INSERT INTO `hydra_service_app_node_meta` (`guid`, `name`, `path`, `type`, `alias`, resource_type, deployment_method, create_time, update_time) VALUES (#{metaGuid},#{name},#{path},#{type},#{alias},#{resourceType},#{deploymentMethod},#{createTime},#{updateTime})" )
    void insert( ApplicationElement applicationElement );

    @Delete( "DELETE FROM `hydra_service_app_node_meta` WHERE `guid`=#{guid}" )
    void remove( @Param("guid") GUID guid );

    @Select( "SELECT `id` AS `enumId`, `guid`, `name`, `path`, `type`, `alias`, `resource_type` AS resourceType, `deployment_method` AS deploymentMethod, `create_time` AS createTime, `update_time` AS updateTime FROM `hydra_service_app_node_meta` WHERE `guid`=#{guid}" )
    GenericApplicationElement getApplicationElement( @Param("guid") GUID guid );

    default GenericApplicationElement getApplicationElement( GUID guid, ServicesInstrument servicesInstrument ){
        GenericApplicationElement element = this.getApplicationElement( guid );
        element.apply( servicesInstrument );
        return element;
    }

    void update( ApplicationElement applicationElement );
}
