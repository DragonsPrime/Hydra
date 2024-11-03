package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.GenericNamespace;
import com.pinecone.hydra.service.kom.entity.Namespace;
import com.pinecone.hydra.service.kom.source.ServiceNamespaceManipulator;
import com.pinecone.slime.jelly.source.ibatis.IbatisDataAccessObject;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
@IbatisDataAccessObject
public interface ServiceNamespaceMapper extends ServiceNamespaceManipulator {
    @Insert("INSERT INTO `hydra_service_namespace_node` (`guid`, `name`, `rules_guid`) VALUES (#{guid},#{name},#{rulesGUID})")
    void insert( Namespace ns );

    @Delete("DELETE FROM `hydra_service_namespace_node` WHERE `guid`=#{guid}")
    void remove( @Param("guid") GUID GUID );

    @Select("SELECT `id` AS `enumId`, `guid`, `name`, `rules_guid` AS rulesGUID FROM `hydra_service_namespace_node` WHERE `guid`=#{guid}")
    GenericNamespace getNamespace( @Param("guid") GUID guid );

    void update( Namespace ns );

    @Select("SELECT `id` AS `enumId`, `guid`, `name`, `rules_guid` AS rulesGUID FROM `hydra_service_namespace_node` WHERE name=#{name}")
    List<GenericNamespace > fetchNamespaceNodeByName0( @Param("name") String name );

    @SuppressWarnings( "unchecked" )
    default List<Namespace > fetchNamespaceNodeByName( String name ){
        return (List) this.fetchNamespaceNodeByName0( name );
    }

    @Override
    @Select( "SELECT `guid` FROM `hydra_service_namespace_node` WHERE `name` = #{name}" )
    List<GUID > getGuidsByName(String name);

    @Override
    @Select( "SELECT `guid` FROM `hydra_service_namespace_node` WHERE `name` = #{name} AND `guid` = #{guid}" )
    List<GUID > getGuidsByNameID( @Param("name") String name, @Param("guid") GUID guid );
}
