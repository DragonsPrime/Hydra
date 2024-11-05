package com.pinecone.hydra.service.ibatis;

import com.pinecone.framework.util.id.GUID;
import com.pinecone.hydra.service.kom.entity.GenericServiceElement;
import com.pinecone.hydra.service.kom.source.ServiceNodeManipulator;
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
public interface ServiceNodeMapper extends ServiceNodeManipulator {

    @Insert("INSERT INTO `hydra_service_service_nodes` (`guid`, `name`) VALUES (#{guid},#{name})")
    void insert(GenericServiceElement serviceNode);

    @Delete("DELETE FROM `hydra_service_service_nodes` WHERE `guid`=#{guid}")
    void remove(@Param("guid")GUID guid);

    @Select("SELECT `id` AS `enumId`, `guid`, `name` FROM `hydra_service_service_nodes` WHERE `guid`=#{guid}")
    GenericServiceElement getServiceNode(@Param("guid") GUID guid);
    @Update("UPDATE `hydra_service_service_nodes` SET `name` = #{name} WHERE `guid` = #{guid}")
    void updateServiceNode(GenericServiceElement serviceNode);

    @Select("SELECT `id` AS `enumId`, `guid` , `name` FROM `hydra_service_service_nodes` WHERE name=#{name}")
    List<GenericServiceElement> fetchServiceNodeByName(@Param("name") String name);

    @Override
    @Select( "SELECT `guid` FROM `hydra_service_service_nodes` WHERE `name` = #{name}" )
    List<GUID> getGuidsByName( String name );

    @Override
    @Select( "SELECT `guid` FROM `hydra_service_service_nodes` WHERE `name` = #{name} AND `guid` = #{guid}" )
    List<GUID> getGuidsByNameID( @Param("name") String name, @Param("guid") GUID guid );
}
